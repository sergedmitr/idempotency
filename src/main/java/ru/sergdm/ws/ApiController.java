package ru.sergdm.ws;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sergdm.ws.clients.DeliveryClient;
import ru.sergdm.ws.clients.PaymentClient;
import ru.sergdm.ws.clients.WarehouseClient;
import ru.sergdm.ws.clients.model.ConfirmRequest;
import ru.sergdm.ws.clients.model.Delivery;
import ru.sergdm.ws.clients.model.MoneyMove;
import ru.sergdm.ws.clients.model.Reserve;
import ru.sergdm.ws.clients.model.ReturnRequest;
import ru.sergdm.ws.clients.model.ShipmentRequest;
import ru.sergdm.ws.enums.OrderStatuses;
import ru.sergdm.ws.exception.BadResourceException;
import ru.sergdm.ws.exception.ResourceAlreadyExistsException;
import ru.sergdm.ws.exception.ResourceNotExpectedException;
import ru.sergdm.ws.exception.ResourceNotFoundException;
import ru.sergdm.ws.model.Order;
import ru.sergdm.ws.model.OrderReserve;
import ru.sergdm.ws.model.SystemName;
import ru.sergdm.ws.service.OrderService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

@RestController
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;

	@Autowired
	DeliveryClient deliveryClient;

	@Autowired
	PaymentClient paymentClient;

	@Autowired
	WarehouseClient warehouseClient;

	@GetMapping("/deliveries-all")
	public List<Delivery> getDeliveries()
	{
		return deliveryClient.getDeliveries();
	}

	@GetMapping("/")
	public ResponseEntity<Object> name() {
		SystemName name = new SystemName();
		return new ResponseEntity<>(name, HttpStatus.OK);
	}

	@GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Order>> findAll(
			@RequestParam(value = "page", defaultValue="1") int pageNumber,
			@RequestParam(required = false) String name) {
		List<Order> orders = orderService.findAll();
		System.out.println("Orders = " + orders);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@DeleteMapping("/orders")
	public ResponseEntity<?> deleteDeliveries(){
		logger.info("Delete all orders");
		orderService.deleteAll();
		return ResponseEntity.ok().body(HttpStatus.OK);
	}

	@GetMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> findUserById(@PathVariable long orderId) {
		try {
			Order user = orderService.findById(orderId);
			return ResponseEntity.ok(user);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PostMapping(value = "/orders/reserve")
	public ResponseEntity<OrderReserve> reserveOrder() {
		return ResponseEntity.ok(orderService.reserve());
	}

	@PostMapping(value = "/orders")
	public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
		try {
			Order newOrder = orderService.saveIdempotent(order);
			return ResponseEntity.created(new URI("/api/orders/" + newOrder.getOrderId())).body(newOrder);
		} catch (ResourceAlreadyExistsException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (BadResourceException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (ResourceNotExpectedException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(417).build();
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(order);
		}
	}

	@PostMapping(value = "/orders/complete/{orderId}")
	public ResponseEntity<?> completeOrder(@PathVariable Long orderId) {
		try {
			logger.info("processOrder. orderId = " + orderId);
			Order order = orderService.findById(orderId);
			if (order.getStatus() != OrderStatuses.PROCESSED) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderId);
			}
			try {
				// ship Reserve
				ShipmentRequest shipmentRequest = new ShipmentRequest();
				shipmentRequest.setOrderId(order.getOrderId());
				shipmentRequest.setReserveId(order.getReserveId());
				warehouseClient.shipRequest(shipmentRequest);
				logger.info("reserve shipped");
			} catch (FeignException ex) {
				logger.error("Warehouse error. ex = {}", ex.getMessage(), ex);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
			}
			try {
				// confirm Delivery
				ConfirmRequest confirmRequest = new ConfirmRequest();
				confirmRequest.setOrderId(order.getOrderId());
				confirmRequest.setDeliveryId(order.getDeliveryId());
				deliveryClient.confirmDelivery(confirmRequest);
				logger.info("delivery completed");
				orderService.setCompleted(order);
				return ResponseEntity.ok(order);
			} catch (FeignException ex) {
				logger.error("Warehouse error. ex = {}", ex.getMessage(), ex);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
			}
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderId);
		}
	}

	@PostMapping(value = "/orders/process/{orderId}")
	public ResponseEntity<?> processOrder(@PathVariable Long orderId) {
		Long storedMoveId = null;
		Long storedReserveID = null;
		try {
			logger.info("processOrder. orderId = " + orderId);
			Order order = orderService.findById(orderId);
			if (order.getStatus() != OrderStatuses.CREATED) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderId);
			}
			try {
				// do Payment
				MoneyMove moneyMove = new MoneyMove();
				moneyMove.setUserId(order.getUserId());
				moneyMove.setOrderId(order.getOrderId());
				moneyMove.setAmount(order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
				moneyMove.setMoveDt(new Date());
				MoneyMove result = paymentClient.doPayment(order.getAccountId(), moneyMove);
				storedMoveId = result.getMoveId();
				logger.info("processOrder. result = {}", result);
			} catch (FeignException ex) {
				logger.error("Payment error. ex ={}", ex.getMessage(), ex);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
			}
			try {
				// do Reserve
				Reserve reserve = new Reserve();
				reserve.setProductId(order.getProduct());
				reserve.setOrderId(order.getOrderId());
				reserve.setAmount(order.getQuantity());
				Reserve newReserve = warehouseClient.createReserve(reserve);
				storedReserveID = newReserve.getReserveId();
				order.setReserveId(newReserve.getReserveId());
				logger.info("processOrder. reserve = {}", newReserve);
			} catch (FeignException ex) {
				logger.error("Warehouse error. ex ={}", ex.getMessage(), ex);
				// return Payment
				ReturnRequest returnRequest = new ReturnRequest();
				returnRequest.setOrderId(order.getOrderId());
				returnRequest.setReturnMoveId(storedMoveId);
				paymentClient.returnPayment(order.getAccountId(), returnRequest);
				logger.info("payment returned(1)");
				return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
			}
			// schedule Delivery
			try {
				Delivery delivery = new Delivery();
				delivery.setOrderId(order.getOrderId());
				delivery.setCourierId("af-32");
				delivery.setTimeslot(order.getDelivery());
				Delivery deliveryNew = deliveryClient.scheduleDelivery(delivery);
				order.setDeliveryId(deliveryNew.getDeliveryId());
				logger.info("processOrder. delivery = {}", deliveryNew);
			} catch (FeignException ex) {
				logger.error("Delivery error. ex ={}", ex.getMessage(), ex);
				// return Payment
				ReturnRequest returnRequest = new ReturnRequest();
				returnRequest.setOrderId(order.getOrderId());
				returnRequest.setReturnMoveId(storedMoveId);
				paymentClient.returnPayment(order.getAccountId(), returnRequest);
				logger.info("payment returned(2)");
				// cancel Reserve
				ShipmentRequest shipmentRequest = new ShipmentRequest();
				shipmentRequest.setOrderId(order.getOrderId());
				shipmentRequest.setReserveId(storedReserveID);
				warehouseClient.cancelRequest(shipmentRequest);
				logger.info("shipment cancelled");
				return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
			}
			//
			orderService.setProcessed(order);
			return ResponseEntity.ok(order);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderId);
		}
	}

}