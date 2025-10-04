package com.github.lekan128.aiagent.impl.method.description;


import com.github.lekan128.aigaent.api.annotation.AiToolMethod;
import com.github.lekan128.aigaent.api.annotation.ArgDesc;
import jakarta.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @apiNote Internal API. Not intended for external use.
 * &#064;Internal
 * Not part of the public API. Subject to change without notice.
 */
public class MethodDescriptor {

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
