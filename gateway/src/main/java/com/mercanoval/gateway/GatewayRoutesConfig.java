package com.mercanoval.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("clientes")
                .route(path("/api/clientes/**"), http())
                .before(uri("http://clientes:8081"))
                .build()
            .and(route("productos")
                .route(path("/api/productos/**"), http())
                .before(uri("http://productos:8082"))
                .build())
            .and(route("categorias")
                .route(path("/api/categorias/**"), http())
                .before(uri("http://categorias:8083"))
                .build())
            .and(route("proveedores")
                .route(path("/api/proveedores/**"), http())
                .before(uri("http://proveedores:8084"))
                .build())
            .and(route("descuentos")
                .route(path("/api/descuentos/**"), http())
                .before(uri("http://descuentos:8085"))
                .build())
            .and(route("pedidos")
                .route(path("/api/pedidos/**"), http())
                .before(uri("http://pedidos:8086"))
                .build())
            .and(route("envios")
                .route(path("/api/envios/**"), http())
                .before(uri("http://envios:8087"))
                .build())
            .and(route("carrito")
                .route(path("/api/carrito/**"), http())
                .before(uri("http://carrito:8088"))
                .build())
            .and(route("pago")
                .route(path("/api/pagos/**"), http())
                .before(uri("http://pago:8089"))
                .build())
            .and(route("inventario")
                .route(path("/api/inventario/**"), http())
                .before(uri("http://inventario:8090"))
                .build());
    }
}
