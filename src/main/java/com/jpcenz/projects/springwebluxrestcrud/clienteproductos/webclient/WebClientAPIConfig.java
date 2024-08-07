package com.jpcenz.projects.springwebluxrestcrud.clienteproductos.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientAPIConfig {
    @Value("${config.base.urlProducto}")
    private String url ;
   @Bean
    public WebClient productoWebClient(WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

}
