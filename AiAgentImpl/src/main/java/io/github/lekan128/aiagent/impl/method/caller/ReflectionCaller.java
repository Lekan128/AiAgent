package io.github.lekan128.aiagent.impl.method.caller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lekan128.aiagent.impl.method.MethodExecutionResult;
import io.github.lekan128.aiagent.api.ObjectMapperSingleton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core utility class responsible for executing a sequence of method calls
 * based on LLM-generated reflection requests, managing method context between calls.
 *
 * <p>This class enables the sequential execution of tool calls (using the output of one method as the argument for the next), allowing the output
 * of one method to be used as the input (via the shared context map) for a subsequent method.</p>
 *
 * <p><strong>Note:</strong> This class is internal-facing and manages complex reflection and
 * argument matching, making its methods prone to throwing numerous checked exceptions.</p>
 *
 * <p><strong>Internal API:</strong> This class is strictly for internal library use
 * and is not intended for external consumption. Its methods and structure are subject
 * to change without notice.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 */
public class ReflectionCaller {
    private static Object invokeMethodFromJson(String json) throws Exception {
        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        ReflectionInvocableMethod request = mapper.readValue(json, ReflectionInvocableMethod.class);

        return invokeMethod(request);
    }

    private static Object invokeMethod(ReflectionInvocableMethod request) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object result = ReflectionCaller.callMethod(
                request.getClassName(),
                request.getMethodName(),
                request.getMethodArguments(),
                null
        );

        System.out.println("Result = " + result);
        return result;
    }

    private static Object callMethod(
            String className,
            String methodName,
            List<MethodArgument> args,
            Map<String,Object> methodArgumentPlaceHolders
    ) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(className);
        Object instance = null;

        // Convert argument type names to Class objects
        Class<?>[] paramTypes = new Class<?>[args.size()];
        Object[] paramValues = new Object[args.size()];

        for (int i = 0; i < args.size(); i++) {
            String typeName = args.get(i).getType();
            Object rawValue = args.get(i).getValue();

            // Resolve placeholder if present
            Object resolvedValue =
                    methodArgumentPlaceHolders == null ? rawValue : resolvePlaceholders(rawValue, methodArgumentPlaceHolders);

            Class<?> paramType = getClassFromName(typeName);
            paramTypes[i] = paramType;
            paramValues[i] = convertValue(resolvedValue, paramType);

        }

        Method method = clazz.getMethod(methodName, paramTypes);

        // Check if static
        if (!Modifier.isStatic(method.getModifiers())) {
            instance = clazz.getDeclaredConstructor().newInstance();
        }

        return method.invoke(instance, paramValues);
    }

    private static List<MethodExecutionResult> executePipelineFromJsonList(String jsonList) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
        List<ReflectionInvocableMethod> reflectionInvocableMethods = objectMapper.readValue(jsonList, new TypeReference<List<ReflectionInvocableMethod>>() {});
        return executePipeline(reflectionInvocableMethods);
    }

    /**
     * Executes a pipeline of method calls sequentially, maintaining an execution context
     * to pass results between stages.
     *
     * <p>Imp. details</p>
     * <p>For each request:
     * <ol>
     * <li>The specified method is invoked using reflection.</li>
     * <li>If {@link ReflectionInvocableMethod#getReturnObjectKey()} is not null, the result is
     * stored in the context map using that key.</li>
     * <li>The execution result is added to the final result list.</li>
     * </ol>
     * <p>The process assumes the existence of the internal {@code callMethodWithContext} method
     * for argument matching and invocation.</p>
     *
     * @param requests A list of {@link ReflectionInvocableMethod} objects defining the pipeline steps.
     * @return A list of {@link MethodExecutionResult} objects, detailing the outcome of each step.
     * @throws ClassNotFoundException If the class specified in a request cannot be found.
     * @throws InvocationTargetException If the invoked method throws an exception.
     * @throws NoSuchMethodException If the method specified in a request cannot be found with matching arguments.
     * @throws InstantiationException If the target class is abstract or an interface and cannot be instantiated.
     * @throws IllegalAccessException If the method or class is not accessible.
     */
    public static List<MethodExecutionResult> executePipeline(List<ReflectionInvocableMethod> requests) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map<String, Object> context = new HashMap<>();
        List<MethodExecutionResult> results = new ArrayList<>();

        for (ReflectionInvocableMethod req : requests) {
            Object result = callMethodWithContext(req, context);
            if (req.getReturnObjectKey() != null) {
                context.put(req.getReturnObjectKey(), result);
            }

            results.add(new MethodExecutionResult(req, result));
        }
        return results;
    }

    //context is the possible result substitution
    private static Object callMethodWithContext(ReflectionInvocableMethod request, Map<String, Object> context) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return callMethod(
                request.getClassName(),
                request.getMethodName(),
                request.getMethodArguments(),
                context
        );
    }

    private static Class<?> getClassFromName(String typeName) throws ClassNotFoundException {
        switch (typeName) {
            case "int": return int.class;
            case "long": return long.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            default: return Class.forName(typeName);
        }
    }
    private static Object resolvePlaceholders(Object rawValue, Map<String, Object> context) {
        if (rawValue instanceof String && ((String) rawValue).startsWith("{{")) {
            Object resolved = context.get(rawValue);
            if (resolved == null) {
                throw new IllegalArgumentException("Unresolved placeholder: " + rawValue);
            }
            return resolved;
        }
        return rawValue;
    }
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) value).intValue();
        }
        if (targetType == long.class || targetType == Long.class) {
            return ((Number) value).longValue();
        }
        if (targetType == double.class || targetType == Double.class) {
            return ((Number) value).doubleValue();
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }
        if (targetType == String.class) {
            return value.toString();
        }
        if (value instanceof Map) { //it gets automatically converted into a map
            // Re-use Jackson to map Map -> targetType
            return ObjectMapperSingleton.getObjectMapper().convertValue(value, targetType);
        }
        return value; // let Java handle Strings, objects, etc.
    }

}

