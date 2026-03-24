package com.alexander.servicio_b.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicio-b")
public class MensajeController {
	
	@GetMapping("/mensaje")
	public String saludo(){
		return"Hola mundo desde servcio b";
	}

}
