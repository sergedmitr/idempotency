package ru.sergdm.ws.clients.model;

public class ReturnRequest {
	private Long returnMoveId;
	private Long orderId;

	public Long getReturnMoveId() {
		return returnMoveId;
	}

	public void setReturnMoveId(Long returnMoveId) {
		this.returnMoveId = returnMoveId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}
