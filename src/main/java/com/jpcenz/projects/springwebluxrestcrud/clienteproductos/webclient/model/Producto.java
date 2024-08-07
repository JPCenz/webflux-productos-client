package com.jpcenz.projects.springwebluxrestcrud.clienteproductos.webclient.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Builder
@Data
public class Producto {
    private String id;
    private String nombre;
    private Double precio;
    private Date createAt;
    private Categoria categoria;
    private String foto;


}
