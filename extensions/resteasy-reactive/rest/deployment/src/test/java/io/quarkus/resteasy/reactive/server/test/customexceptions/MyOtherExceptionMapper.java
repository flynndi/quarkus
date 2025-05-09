package io.quarkus.resteasy.reactive.server.test.customexceptions;

import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.server.SimpleResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;

import io.vertx.ext.web.RoutingContext;

public class MyOtherExceptionMapper {

    private final SomeBean someBean;

    public MyOtherExceptionMapper(SomeBean someBean) {
        this.someBean = someBean;
    }

    @ServerExceptionMapper
    public Response handleMyOtherException(RoutingContext routingContext, MyOtherException myOtherException,
            SimpleResourceInfo simplifiedResourceInfo, ServerRequestContext serverRequestContext) {
        serverRequestContext.getResteasyReactiveResourceInfo();
        simplifiedResourceInfo.getMethodName();
        return Response.status(411).build();
    }
}
