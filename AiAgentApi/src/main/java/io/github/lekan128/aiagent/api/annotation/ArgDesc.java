package io.github.lekan128.aiagent.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the descriptive name and purpose for a method parameter within a tool-enabled method.
 * It should be applied to method parameters
 *
 * <p>This annotation is essential for Tool Calling (or Function Calling) functionality.</p>
 *
 * <p>When applied to a parameter of a method annotated with {@link AiToolMethod},
 * the {@code value} serves as the detailed, human-readable description. This description is provided to the
 * Language Model (LLM) to help it accurately determine when and how to invoke the tool,
 * including which arguments to pass.</p>
 *
 * Note: each parameter can also be annotated with jakarta.annotation.Nullable to tell the LLM it can be null
 *
 * <p>The description should be clear, concise, and explain the **meaning** or **data type**
 * of the parameter in a way the LLM can understand.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see AiToolMethod
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ArgDesc {
    /**
     * The required description of the parameter, used by the LLM for tool argument generation.
     *
     * @return A descriptive string detailing the parameter's purpose.
     */
    String value(); // required description
}
