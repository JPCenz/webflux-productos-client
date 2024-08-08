package com.jpcenz.projects.springwebluxrestcrud.clienteproductos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ClienteProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClienteProductosApplication.class, args);
    }

}
