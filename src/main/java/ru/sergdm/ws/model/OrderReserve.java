package ru.sergdm.ws.model;

public class OrderReserve {
	private Long orderId;

	public OrderReserve(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}
