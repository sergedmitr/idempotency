package ru.sergdm.ws.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sergdm.ws.clients.model.ConfirmRequest;
import ru.sergdm.ws.clients.model.Delivery;

import java.util.List;

@FeignClient(name = "DeliveryClient", url="${DELIVERY_URL}:8030")
public interface DeliveryClient {
	@GetMapping(value = "/deliveries", consumes = MediaType.APPLICATION_JSON_VALUE)
	List<Delivery> getDeliveries();

	@RequestMapping(method = RequestMethod.POST, value = "/deliveries", consumes = "application/json")
	Delivery scheduleDelivery(Delivery delivery);

	@RequestMapping(method = RequestMethod.POST, value = "/confirm", consumes = "application/json")
	void confirmDelivery(ConfirmRequest confirmRequest);
}
