package ru.sergdm.ws.clients.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

public class Delivery {
	Long deliveryId;
	Long orderId;
	String courierId;
	Timeslot timeslot;
	DeliveryStatuses status;

	public Long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Long deliveryId) {
		this.deliveryId = deliveryId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Timeslot getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(Timeslot timeslot) {
		this.timeslot = timeslot;
	}

	public DeliveryStatuses getStatus() {
		return status;
	}

	public void setStatus(DeliveryStatuses status) {
		this.status = status;
	}

	public String getCourierId() {
		return courierId;
	}

	public void setCourierId(String courierId) {
		this.courierId = courierId;
	}

	@Override
	public String toString() {
		return "Delivery{" +
				"deliveryId=" + deliveryId +
				", orderId=" + orderId +
				", courierId='" + courierId + '\'' +
				", timeslot=" + timeslot +
				", status=" + status +
				'}';
	}
}
