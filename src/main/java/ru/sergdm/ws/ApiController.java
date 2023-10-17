package ru.sergdm.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sergdm.ws.exception.BadResourceException;
import ru.sergdm.ws.exception.ResourceAlreadyExistsException;
import ru.sergdm.ws.exception.ResourceNotExpectedException;
import ru.sergdm.ws.exception.ResourceNotFoundException;
import ru.sergdm.ws.model.Order;
import ru.sergdm.ws.model.OrderReserve;
import ru.sergdm.ws.model.SystemName;
import ru.sergdm.ws.service.OrderService;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class ApiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;

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

}