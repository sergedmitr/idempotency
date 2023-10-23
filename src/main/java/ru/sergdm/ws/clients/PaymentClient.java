package ru.sergdm.ws.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sergdm.ws.clients.model.MoneyMove;
import ru.sergdm.ws.clients.model.ReturnRequest;

@FeignClient(name = "PaymentClient", url="${PAYMENT_URL}:8010")
public interface PaymentClient {
	@RequestMapping(method = RequestMethod.POST, value = "/pay/{userId}", consumes = "application/json")
	MoneyMove doPayment(@PathVariable("userId") Long userId, MoneyMove moneyMove);

	@RequestMapping(method = RequestMethod.POST, value = "/pay-return/{userId}", consumes = "application/json")
	String returnPayment(@PathVariable("userId") Long userId, ReturnRequest returnRequest);
}