package io.github.lekan128.aiagent.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a class method as an available "tool" that the {@code Agent} can utilize.
 *
 * <p>Methods annotated with {@code AiToolMethod} are exposed to the Language Model (LLM).
 * The LLM uses the description provided in the {@code value} to determine if and when
 * the method should be called to fulfill a user's request.</p>
 *
 * <p>The description provided in the {@code value} should be comprehensive, detailing
 * - what the method does and
 * - what specific data or format it returns.</p>
 *
 * <p>Each parameter of the annotated method should also be annotated with {@link ArgDesc}
 * to provide necessary context to the LLM about the required input arguments.
 * Each parameter can also be annotated with jakarta.annotation.Nullable to tell the LLM it can be null</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see ArgDesc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AiToolMethod {
    /**
     * The required description of the tool's purpose and return value, used by the LLM.
     *
     * @return A detailed string describing the method's functionality and its output.
     */
    String value(); // required description
}
