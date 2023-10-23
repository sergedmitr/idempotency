package ru.sergdm.ws.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sergdm.ws.clients.model.Reserve;
import ru.sergdm.ws.clients.model.ShipmentRequest;

@FeignClient(name = "WarehouseClient", url="${WAREHOUSE_URL}:8020")
public interface WarehouseClient {
	@RequestMapping(method = RequestMethod.POST, value = "/reserves", consumes = "application/json")
	Reserve createReserve(Reserve reserve);

	@RequestMapping(method = RequestMethod.POST, value = "/cancel", consumes = "application/json")
	String cancelRequest(ShipmentRequest shipmentRequest);
}
