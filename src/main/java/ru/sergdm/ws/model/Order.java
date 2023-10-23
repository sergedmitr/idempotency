package ru.sergdm.ws.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.sergdm.ws.clients.model.Timeslot;
import ru.sergdm.ws.enums.OrderStatuses;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long orderId;
	String product;
	Long quantity;
	BigDecimal price;
	Timeslot delivery;
	OrderStatuses status;
	Long userId;
	Long deliveryId;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Timeslot getDelivery() {
		return delivery;
	}

	public void setDelivery(Timeslot delivery) {
		this.delivery = delivery;
	}

	public OrderStatuses getStatus() {
		return status;
	}

	public void setStatus(OrderStatuses status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Long deliveryId) {
		this.deliveryId = deliveryId;
	}

	@Override
	public String toString() {
		return "Order{" +
				"orderId=" + orderId +
				", product='" + product + '\'' +
				", quantity=" + quantity +
				", price=" + price +
				", delivery=" + delivery +
				", userId=" + userId +
				", deliveryId=" + deliveryId +
				'}';
	}
}
