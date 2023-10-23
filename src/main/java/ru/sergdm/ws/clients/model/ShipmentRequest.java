package ru.sergdm.ws.clients.model;

public class ShipmentRequest {
	private Long reserveId;
	private Long orderId;

	public Long getReserveId() {
		return reserveId;
	}

	public void setReserveId(Long reserveId) {
		this.reserveId = reserveId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}
