package io.quarkus.smallrye.health.test;

import static io.restassured.RestAssured.when;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.inject.Stereotype;
import jakarta.inject.Named;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;

class HealthCheckDefaultScopeTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(NoScopeCheck.class, NoScopeStereotypeWithoutScopeCheck.class, MyStereotype.class));

    @Test
    void testHealth() {
        // the health check does not set a content type, so we need to force the parser
        try {
            RestAssured.defaultParser = Parser.JSON;
            when().get("/q/health/live").then()
                    .body("status", is("UP"),
                            "checks.status", hasItems("UP", "UP"),
                            "checks.name", hasItems("noScope", "noScopeStereotype"));
            when().get("/q/health/live").then()
                    .body("status", is("DOWN"),
                            "checks.status", hasItems("DOWN", "DOWN"),
                            "checks.name", hasItems("noScope", "noScopeStereotype"));
        } finally {
            RestAssured.reset();
        }
    }

    // No scope - @Singleton is used by default
    @Liveness
    static class NoScopeCheck implements HealthCheck {

        final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public HealthCheckResponse call() {
            if (counter.incrementAndGet() > 1) {
                return HealthCheckResponse.builder().down().name("noScope").build();
            }
            return HealthCheckResponse.builder().up().name("noScope").build();
        }
    }

    // No scope and stereotype without scope - @Singleton is used by default
    @MyStereotype
    @Liveness
    static class NoScopeStereotypeWithoutScopeCheck implements HealthCheck {

        final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public HealthCheckResponse call() {
            if (counter.incrementAndGet() > 1) {
                return HealthCheckResponse.builder().down().name("noScopeStereotype").build();
            }
            return HealthCheckResponse.builder().up().name("noScopeStereotype").build();
        }
    }

    @Named
    @Stereotype
    @Target({ TYPE, METHOD, FIELD })
    @Retention(RUNTIME)
    public @interface MyStereotype {
    }

}
