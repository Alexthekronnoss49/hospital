package com.alexander.servicio.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "servicio-b")
public interface MensajeClient {

	@GetMapping("/servicio-b/mensaje")
	String obtenerMensaje();
	
}
