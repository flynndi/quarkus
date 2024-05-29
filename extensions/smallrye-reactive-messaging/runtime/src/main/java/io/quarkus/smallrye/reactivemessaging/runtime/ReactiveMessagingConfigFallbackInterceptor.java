package io.quarkus.smallrye.reactivemessaging.runtime;

import java.util.function.Function;

import io.quarkus.runtime.util.StringUtil;
import io.smallrye.config.FallbackConfigSourceInterceptor;

/**
 * @deprecated maps the old config to the new config, should be removed at some point
 */
@Deprecated(forRemoval = true, since = "3.9")
public class ReactiveMessagingConfigFallbackInterceptor extends FallbackConfigSourceInterceptor {

    private static final String OLD_PREFIX = "quarkus.reactive-messaging.";
    private static final String NEW_PREFIX = "quarkus.messaging.";
    private static final Function<String, String> RENAME_FUNCTION = new Function<String, String>() {
        @Override
        public String apply(String s) {
            return StringUtil.changePrefix(s, NEW_PREFIX, OLD_PREFIX);
        }
    };

    public ReactiveMessagingConfigFallbackInterceptor() {
        super(RENAME_FUNCTION);
    }
}
