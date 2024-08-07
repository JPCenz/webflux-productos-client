package com.jpcenz.projects.springwebluxrestcrud.clienteproductos.handler;

import com.jpcenz.projects.springwebluxrestcrud.clienteproductos.service.ProductoService;
import com.jpcenz.projects.springwebluxrestcrud.clienteproductos.webclient.model.Producto;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Component
@Slf4j
public class ProductoHandler {

    @Autowired
    private ProductoService service;
    public Mono<ServerResponse> listar(ServerRequest request){
        log.error("listar");
        return ServerResponse.ok().body(service.findAll(), Producto.class).onErrorResume(error -> ServerResponse.badRequest().build()).log();
    }

    public Mono<ServerResponse> ver(ServerRequest request) {
        var id = request.pathVariable("id");
        return ServerResponse.ok()
                .body(service.findById(id), Producto.class)
                .onErrorResume(error -> ServerResponse.badRequest()
                        .build())
                .log();
    }

    public Mono<ServerResponse> crear(ServerRequest request) {

        return request.bodyToMono(Producto.class)
                .flatMap(p -> service.save(p))
                .flatMap(p -> ServerResponse.created(URI.create("/api/client/" + p.getId()))
                        .bodyValue(p))
                .onErrorResume(error -> {
                    log.error("Error: ", error);
                    return handleException(error);
                })
                .log();
    }

    public Mono<ServerResponse> editar (ServerRequest request){
        var id = request.pathVariable("id");
        return request.bodyToMono(Producto.class)
                .flatMap(p -> {

                    p.setId(id);
                    return service.edit(p);
                })
                .flatMap(p -> ServerResponse.created(URI.create("/api/client/" + p.getId()))
                        .bodyValue(p))
                .onErrorResume(error -> {
                    log.error("Error: {}", error.getMessage());
                    return handleException(error);
                })
                .log();
    }

    private Mono<ServerResponse> handleException(Throwable error) {
        if (error instanceof WebClientResponseException webClientException) {
            log.error("WebClientResponseException: {}", webClientException.getResponseBodyAsString());
            return ServerResponse.status(webClientException.getStatusCode())
                    .body(Mono.just(webClientException.getResponseBodyAsString()), String.class);
        } else if (error instanceof IllegalArgumentException) {
            log.error("IllegalArgumentException: {}", error.getMessage());
            return ServerResponse.badRequest().body(Mono.just(error.getMessage()), String.class);
        } else {
            log.error("Unexpected error: ", error);
            return ServerResponse.status(500).body(Mono.just("Internal Server Error"), String.class);
        }
    }

    public Mono<ServerResponse> eliminar(ServerRequest request) {
        var id = request.pathVariable("id");
        return service.findById(id)
                .flatMap(p -> service.delete(p))
                .then(ServerResponse.noContent().build())
                .onErrorResume(error -> {
                    log.error("Error: "+ error.getMessage());
                    return handleException(error);
                })
                .log();
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        var id = request.pathVariable("id");
        //obtain the file from the request
        return request.body(BodyExtractors.toMultipartData()).flatMap(parts -> {
            var part = parts.toSingleValueMap().get("file");
            if (part == null) {
                return Mono.error(new Exception("No se encontró el archivo file"));
            }
            var filePart = (FilePart) part;
            return service.upload(filePart, id)
                    .flatMap(p -> ServerResponse.created(URI.create("/api/client/" + p.getId()))
                            .bodyValue(p))
                    .onErrorResume(error -> {
                        log.error("Error: {}", error.getMessage());
                        return handleException(error);
                    });

        });
    }

}
