package com.jpcenz.projects.springwebluxrestcrud.clienteproductos.service.impl;

import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.jpcenz.projects.springwebluxrestcrud.clienteproductos.service.ProductoService;
import com.jpcenz.projects.springwebluxrestcrud.clienteproductos.webclient.model.Producto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class ProductoServiceImpl implements ProductoService {
    @Autowired
    WebClient webClient;
    @Override
    public Flux<Producto> findAll() {
      log.info("findAll");
        return webClient.get()
                .headers(h -> h.set("Content-Type", "application/json"))

                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToFlux(Producto.class)
                .doOnError(e -> log.error(e.getMessage()))
                .log();
    }

    @Override
    public Mono<Producto> findById(String id) {
        Map<String,Object> params = Map.of("id",id);
        return webClient.get()
                .uri("/{id}",params)
                .headers(h -> h.set("Content-Type", "application/json"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToMono(Producto.class)
                .doOnError(e -> log.error(e.getMessage()))
                .log();
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(producto),Producto.class)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToMono(Producto.class)
                .doOnError(e -> log.error(e.getMessage()))
                .log();
    }

    @Override
    public Mono<Producto> edit(Producto producto) {

        return webClient.put()
                .uri("/{id}", Collections.singletonMap("id",producto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(producto),Producto.class)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToMono(Producto.class)
                .doOnError(e -> log.error(e.getMessage()))
                .log();
    }

    @Override
    public Mono<Void> delete(Producto producto) {
        return webClient.delete()
                .uri("/{id}", Collections.singletonMap("id",producto.getId()))
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()){
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(Void.class);
                })
                .doOnError(e -> log.error(e.getMessage()))
                .log();
    }

    @Override
    public Mono<Producto> upload(FilePart file, String id) {
        //upload a file to the server multipart
        return webClient.post()
                .uri("/upload/{id}",Collections.singletonMap("id",id))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(file)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        ClientResponse::createException)
                .bodyToMono(Producto.class)
                .doOnError(e -> log.error(e.getMessage()))
                .log();

    }
}
