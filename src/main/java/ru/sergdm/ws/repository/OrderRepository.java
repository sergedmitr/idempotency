package ru.sergdm.ws.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.sergdm.ws.model.Order;

public interface OrderRepository extends CrudRepository<Order, Long>,
		JpaSpecificationExecutor<Order> {
}
