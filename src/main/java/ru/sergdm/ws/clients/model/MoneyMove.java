package ru.sergdm.ws.clients.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "money_moves")
public class MoneyMove {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long moveId;
	@Column(nullable = false)
	Long userId;
	@Column(nullable = false)
	Date moveDt;
	@Column(nullable = false)
	int direction;
	@Column(nullable = false)
	String operation;
	@Column(nullable = false)
	BigDecimal amount;
	@Column
	Long orderId;

	public Long getMoveId() {
		return moveId;
	}

	public void setMoveId(Long moveId) {
		this.moveId = moveId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getMoveDt() {
		return moveDt;
	}

	public void setMoveDt(Date moveDt) {
		this.moveDt = moveDt;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}
