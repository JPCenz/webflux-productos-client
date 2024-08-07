package com.jpcenz.projects.springwebluxrestcrud.clienteproductos.service;

import com.jpcenz.projects.springwebluxrestcrud.clienteproductos.webclient.model.Producto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {
    public Flux<Producto> findAll();
    public Mono<Producto> findById(String id);
    public Mono<Producto> save(Producto producto);
    public Mono<Producto> edit(Producto producto);
    public Mono<Void> delete(Producto producto);
    public Mono<Producto> upload(FilePart file, String id);
}
