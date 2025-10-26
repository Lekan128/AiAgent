package io.github.lekan128.aiagent.api;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides a singleton instance of the Jackson {@code ObjectMapper} configured for the library.
 *
 * <p>This singleton ensures that the {@code ObjectMapper} is initialized only once,
 * promoting efficient resource usage across the application. It is configured to allow
 * automatic detection of **private fields** during serialization/deserialization.</p>
 *
 * <p>It is generally safe to use this instance throughout your code instead of
 * creating new {@code ObjectMapper} instances, as it is lazily initialized and
 * intended for global use. **Note:** While the instance itself is thread-safe
 * after initialization, **configuration changes should be avoided** to maintain
 * consistent behavior across concurrent threads.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 */
public class ObjectMapperSingleton {
    private static ObjectMapper objectMapper;

    /**
     * Retrieves the singleton instance of the configured {@code ObjectMapper}.
     *
     * <p>If the instance has not yet been initialized, this method performs a lazy
     * initialization, setting the visibility to {@link com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility#ANY}
     * for fields before returning the instance.</p>
     *
     * @return The single, application-wide instance of the {@code ObjectMapper}.
     */
    public static ObjectMapper getObjectMapper() {
        if (objectMapper != null){
            return objectMapper;
        }
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(
                com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
        );
        return objectMapper;
    }
}
