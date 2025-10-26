package io.github.lekan128.aiagent.impl.method.description;


import io.github.lekan128.aiagent.api.annotation.AiToolMethod;
import io.github.lekan128.aiagent.api.annotation.ArgDesc;
import jakarta.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for performing reflection and metadata extraction on
 * methods annotated with {@code @AiToolMethod} and {@code @ArgDesc}.
 *
 * <p>It transforms the Java method signature and annotations into the structured
 * {@link MethodDescription} format, which is then used internally to generate
 * the necessary Tool Calling schemas for the Language Model (LLM).</p>
 *
 * <p><strong>Internal API:</strong> This class is strictly for internal library use
 * and is not intended for external consumption. Its methods and structure are subject
 * to change without notice.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 */
public class MethodDescriptor {

    /**
     * Private constructor to prevent instantiation of this static utility class.
     */
    private MethodDescriptor() {
        // Utility class
    }

    /**
     * Reflectively examines a Java method and extracts all relevant tool metadata
     * if the method is annotated with {@code @AiToolMethod}.
     *
     * @param method The {@code Method} object to be analyzed.
     * @return A populated {@link MethodDescription} object containing tool metadata,
     * or {@code null} if the method does not have the {@code @AiToolMethod} annotation.
     */
    public static MethodDescription describeMethod(Method method) {
        AiToolMethod ann = method.getAnnotation(AiToolMethod.class);
        if (ann == null) return null; // only process annotated methods

        MethodDescription description = new MethodDescription();
        description.setDescription(ann.value());
        description.setClassName(method.getDeclaringClass().getName());
        description.setMethodName(method.getName());

        List<MethodDescription.Parameter> parameters = new ArrayList<>();

        for (Parameter p : method.getParameters()) {
            MethodDescription.Parameter parameter = new MethodDescription.Parameter();
            parameter.setName(p.getName());
            parameter.setType(p.getType().getName());
            parameter.setRequired(!p.isAnnotationPresent(Nullable.class));

            if (p.isAnnotationPresent(ArgDesc.class)){
                parameter.setDescription(p.getAnnotation(ArgDesc.class).value());
            }

            Map<String, Object> arg = new LinkedHashMap<>();

            if (!p.getType().isPrimitive() && !p.getType().getName().startsWith("java.")) {
                arg.put("fields", describeFields(p.getType()));
            }
            parameter.setFields(arg);

            parameters.add(parameter);
        }

        description.setMethodArguments(parameters);
        description.setReturnType(method.getReturnType().getName());
        return description;
    }

    private static Map<String, Object> describeFields(Class<?> clazz) {
        Map<String, Object> fields = new LinkedHashMap<>();
        for (Field f : clazz.getDeclaredFields()) {
            Map<String, Object> fieldDesc = new LinkedHashMap<>();
            fieldDesc.put("type", f.getType().getName());
            fieldDesc.put("required", !f.isAnnotationPresent(Nullable.class));
            fields.put(f.getName(), fieldDesc);
        }
        return fields;
    }
}
