package ru.sergdm.ws.service;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sergdm.ws.exception.BadResourceException;
import ru.sergdm.ws.exception.ResourceAlreadyExistsException;
import ru.sergdm.ws.exception.ResourceNotExpectedException;
import ru.sergdm.ws.exception.ResourceNotFoundException;
import ru.sergdm.ws.model.Order;
import ru.sergdm.ws.model.OrderReserve;
import ru.sergdm.ws.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;

	public List<Order> findAll() {
		List<Order> orders = new ArrayList<>();
		orderRepository.findAll().forEach(orders::add);
		System.out.println("orders = " + orders);
		return orders;
	}

	public Order findById(Long id) throws ResourceNotFoundException {
		Optional<Order> orderOpt = orderRepository.findById(id);
		Order order = orderOpt.orElseThrow(() -> new ResourceNotFoundException("Cannt find Order with id: " + id));
		return order;
	}

	public Order saveIdempotent(Order order) throws BadResourceException, ResourceAlreadyExistsException,
			ResourceNotExpectedException, ResourceNotFoundException {
		if (order.getOrderId() == null) {
			throw new ResourceNotExpectedException("OrderId must be not null");
		} else if (!StringUtils.isEmpty(order.getProduct())) {
			Long id = order.getOrderId();
			Order reservedOrder = orderRepository.findById(order.getOrderId())
					.orElseThrow(() -> new ResourceNotFoundException("Cannt find Reserve with id: " + id));
			if (reservedOrder.getProduct() != null) {
				return reservedOrder;
			}
			return orderRepository.save(order);
		} else {
			BadResourceException exc = new BadResourceException("Failed to save order");
			exc.addErrorMessage("Order Product is null or empty");
			throw exc;
		}
	}

	public OrderReserve reserve() {
		Order order = new Order();
		Order orderNew = orderRepository.save(order);
		return new OrderReserve(orderNew.getOrderId());
	}
}
