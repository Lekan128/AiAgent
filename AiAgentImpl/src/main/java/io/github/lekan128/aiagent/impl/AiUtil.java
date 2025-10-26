package io.github.lekan128.aiagent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.lekan128.aiagent.impl.method.description.MethodDescription;
import io.github.lekan128.aiagent.impl.method.description.MethodDescriptor;
import io.github.lekan128.aiagent.api.ObjectMapperSingleton;
import io.github.lekan128.aiagent.api.annotation.AiToolMethod;
import io.github.cdimascio.dotenv.Dotenv;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class AiUtil {
    static String getAiToolsAsJson(){
        Dotenv dotenv = Dotenv.load();
        String nameOfPackageWithTools = dotenv.get("AI_TOOLS_PACKAGE");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(nameOfPackageWithTools))
                .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated, Scanners.MethodsAnnotated));

        Set<Method> methods = reflections.getMethodsAnnotatedWith(AiToolMethod.class);
        List<MethodDescription> methodDescriptions = new ArrayList<>();


        for (Method m : methods) {
            MethodDescription desc = MethodDescriptor.describeMethod(m);
            if (desc != null) methodDescriptions.add(desc);
        }

        String json = null;
        try {
            json = ObjectMapperSingleton.getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(methodDescriptions);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return json;
    }
}
