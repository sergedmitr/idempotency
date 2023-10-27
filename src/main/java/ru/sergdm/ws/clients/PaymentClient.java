package ru.sergdm.ws.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sergdm.ws.clients.model.MoneyMove;
import ru.sergdm.ws.clients.model.ReturnRequest;

@FeignClient(name = "PaymentClient", url="${PAYMENTS_URL}:8010")
public interface PaymentClient {
	@RequestMapping(method = RequestMethod.POST, value = "/pay/{accountId}", consumes = "application/json")
	MoneyMove doPayment(@PathVariable("accountId") Long userId, MoneyMove moneyMove);

	@RequestMapping(method = RequestMethod.POST, value = "/pay-return/{accountId}", consumes = "application/json")
	String returnPayment(@PathVariable("accountId") Long userId, ReturnRequest returnRequest);
}
