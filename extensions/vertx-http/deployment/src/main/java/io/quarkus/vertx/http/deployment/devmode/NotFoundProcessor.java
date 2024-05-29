package io.quarkus.vertx.http.deployment.devmode;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.dev.spi.DevModeType;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.VertxWebRouterBuildItem;
import io.quarkus.vertx.http.runtime.devmode.AdditionalRouteDescription;
import io.quarkus.vertx.http.runtime.devmode.ResourceNotFoundRecorder;
import io.quarkus.vertx.http.runtime.devmode.RouteDescription;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class NotFoundProcessor {

    private static final String META_INF_RESOURCES = "META-INF/resources";

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(RUNTIME_INIT)
    void routeNotFound(ResourceNotFoundRecorder recorder,
            VertxWebRouterBuildItem router,
            HttpRootPathBuildItem httpRoot,
            LaunchModeBuildItem launchMode,
            ApplicationArchivesBuildItem applicationArchivesBuildItem,
            List<RouteDescriptionBuildItem> routeDescriptions,
            List<NotFoundPageDisplayableEndpointBuildItem> additionalEndpoints) {

        // Route Endpoints
        List<RouteDescription> routes = new ArrayList<>();
        for (RouteDescriptionBuildItem description : routeDescriptions) {
            routes.add(description.getDescription());
        }

        // Static files
        Set<String> staticRoots = applicationArchivesBuildItem.getAllApplicationArchives().stream()
                .map(i -> i.apply(t -> {
                    var p = t.getPath(META_INF_RESOURCES);
                    return p == null ? null : p.toAbsolutePath().toString();
                }))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Additional endpoints
        List<AdditionalRouteDescription> endpoints = additionalEndpoints
                .stream()
                .map(v -> new AdditionalRouteDescription(v.getEndpoint(httpRoot), v.getDescription()))
                .sorted()
                .collect(Collectors.toList());

        // Not found handler
        Handler<RoutingContext> notFoundHandler = recorder.registerNotFoundHandler(
                router.getHttpRouter(),
                router.getMainRouter(),
                router.getManagementRouter(),
                getBaseUrl(launchMode),
                httpRoot.getRootPath(),
                routes,
                staticRoots,
                endpoints);
    }

    private String getBaseUrl(LaunchModeBuildItem launchMode) {

        DevModeType type = launchMode.getDevModeType().orElse(DevModeType.LOCAL);
        if (!type.equals(DevModeType.REMOTE_SERVER_SIDE)) {
            Config config = ConfigProvider.getConfig();
            var host = config.getOptionalValue("quarkus.http.host", String.class).orElse("localhost");
            var port = config.getOptionalValue("quarkus.http.port", Integer.class).orElse(8080);
            return "http://" + host + ":" + port + "/";
        } else {
            return null;
        }
    }
}
