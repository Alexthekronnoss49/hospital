package com.alexander.servicio.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.servicio.clients.MensajeClient;

@RestController
@RequestMapping("/servicio-a")
public class SaludoController {
	
	private final MensajeClient mensajeClient;
	
	public SaludoController(MensajeClient mensajeClient) {
		this.mensajeClient = mensajeClient;
	}

	@GetMapping
	public ResponseEntity<String> saludo(){
		return ResponseEntity.ok("Hola mundo desde servcio a");
	}

	@GetMapping("/mensaje")
	public ResponseEntity<String> mensajeServicioB(){
		return ResponseEntity.ok(mensajeClient.obtenerMensaje());
	}

}
