# AiAgent
### AI Agent Library
Welcome to the AI Agent Core Library! This library provides a robust, reflection-based framework for integrating Language Models (LLMs) with custom Java methods (Tools), allowing the LLM to execute code to fulfill complex user reque
The library uses a three-stage process: 
Planning (LLM determines needed tools), 
Execution (Java reflects and runs tools), and 
Response (LLM uses tool results to generate final structured output).

## Prerequisites
Java 17+

## To use
Add to your pom.xml
```xml

```

Add to your .env file
```
# If you plan to use the default gemini llm added to the library
GEMINI_API_KEY=YOUR_GEMINI_API_KEY 

# Your package name that contains all the methods annotated with @AiToolMethod 
AI_TOOLS_PACKAGE=com.example
```

## Core Concepts
# A. The Agent
The central component is the Agent, which orchestrates the workflow. You should always retrieve the singleton instance via the provider:
```java
import io.github.lekan128.aiagent.api.Agent;
import com.github.lekan128.aigent.core.AgentProvider;
import com.github.lekan128.aigaent.api.Agent;
import com.github.lekan128.aigaent.api.ObjectMapperSingleton;
import com.github.lekan128.aigaent.api.llm.Gemini;

//This is a quick test way.
public class TestMain {
    public static void main(String[] args) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Gemini gemini = new Gemini();
        
        String query = "Give me a good description of my top product with the available information for my websites homepage. userid: usr@124523";
        String aiPersona = "A product describer.";
        
        Agent agent = AgentProvider.get();
        
        Response response = agent.useAgent(query, aiPersona, gemini, Response.class);

        System.out.println(response);
    }
}
```

# B. The LLM Abstraction
You can use the default LLM (Gemini) that comes with the Library or you can create your own LLM
All custom Language Models must implement the {@code LLM} abstract class.
To use a new LLM (e.g., a custom local model or Azure OpenAI), you must subclass {@code LLM}.

```java
// Example of a custom LLM implementation
public class CustomLLM extends LLM {

    @Override
    public String getModelName() {
        return "Custom-Local-Model-V1";
    }

    @Override
    public String call(String prompt) {
        // Implement API call logic here (e.g., HTTP request)
        String rawLLMResponse = sendApiRequest(prompt);
        return rawLLMResponse;
    }
}
```

## Tool Definition and Usage (Function Calling)
The library uses custom annotations for defining Java methods that the LLM can "call."

# A. Defining the Tool Method
Annotate any static or instance method you want the LLM to access using `@AiToolMethod`. The value must be a detailed description of what the method does and what it returns.

```java
import com.yourpackage.AiToolMethod;
import com.yourpackage.ArgDesc;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import jakarta.annotation.Nullable;

public class InventoryTools {

    /**
     * Finds and returns a list of product objects that match the specified search query.
     * Returns a JSON list of Product objects, each containing ID, name, and price.
     */
    @AiToolMethod("Searches the product inventory using a query and returns a list of matching Product details.")
    public static List<Product> searchInventory(
        @ArgDesc("The short search query or product name to look up.")
        String query,

        @ArgDesc("The maximum number of results to return (e.g., 5, 10).")
        @Nullable Integer limit
    ) {
        if (limit = null) limit = 5;
        // Implementation logic here
        return findProducts(query, limit);
    }
}
```

# B. Defining Tool Method Arguments
Each parameter in an @AiToolMethod must be annotated with @ArgDesc. This description is crucial as the LLM uses it to determine what value to generate for that argument.

# Executing the Agent
To execute a request, you need to provide the query, a persona, the LLM instance, and the class you want the response to be mapped into.

```java
import com.yourpackage.AgentProvider;
import com.yourpackage.LLM;

// 1. Define your desired response structure (must be a Java class)
public class FinalSummary {
    public String analysis;
    public List<String> sourcesUsed;


public void runAgent() throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException  {
    Agent agent = AgentProvider.get();
    LLM geminiLLM = new Gemini(); // Or your CustomLLM instance

    String userQuery = "I need a summary of all inventory items found for 'laptop' and their combined price.";
    String aiPersona = "A meticulous inventory analyst who prioritizes accuracy.";

    // 2. Execute the agent call
    FinalSummary result = agent.useAgent(
        userQuery,
        aiPersona,
        geminiLLM,
        FinalSummary.class // Target structured class
    );

    System.out.println("Analysis: " + result.analysis);
} 
}
```

## Internal Components (For Developers)
The following classes are for internal library use only and are subject to change. They handle the mechanics of reflection and data serialization:

`ObjectMapperSingleton`: Provides the configured Jackson mapper used for serializing tool schemas and deserializing structured responses.

`MethodDescriptor`: Utility used to convert annotated Java methods into {@code MethodDescription} DTOs.

`ReflectionCaller`: Executes the pipeline of LLM-planned method calls using reflection.

`MethodDescription`, `ReflectionInvocableMethod`, `MethodArgument`: Internal DTOs used for representing method metadata and execution requests


### NOT FOR LIBRARY USER
## 📝 NOTE TO FUTURE SELF (GPG for Maven Central)

For gpg key, you need gpg key to sign the library
Check parent pom.xml 
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-gpg-plugin</artifactId>

To generate GPG key
Step A — Install GPG if you dont already have it with `brew install gnupg` in your CLI for MAC OS
Step B — Generate a key pair, Run:`gpg --full-generate-key`
    Choose RSA and RSA (option 1).
    Choose key size: 4096 (secure, recommended).
    Expiration: You can set “0” (never expires) or “2y” (2 years).
    Enter your full name and email (use the same email as in your GitHub/Maven Central account).
    Add a comment if you like (e.g release key for maven)
    Add a passphrase (important, don’t forget it).
        If you come across any issue run(like not asking for pass phrase):
        ```
        gpgconf --kill gpg-agent
        gpgconf --launch gpg-agent
        ```
        And use a full screen terminal
    This generates:
    Private key → stays on your machine, used to sign. 
    Public key → uploaded to a server so Maven Central can verify.
Step C — List your keys (not required) `gpg --list-keys`
You’ll see something like, when you list or generate your key:
```BASH
pub   rsa4096 2025-10-04 [SC]
ABCD1234EF5678901234567890ABCDEF12345678
uid           [ultimate] Olalekan Amoo <youremail@example.com>
sub   rsa4096 2025-10-04 [E]
```
Step D — Publish your public key
Send it to a keyserver Maven Central can see (like keys.openpgp.org):
`gpg --keyserver hkps://keys.openpgp.org --send-keys ABCD1234EF5678901234567890ABCDEF12345678`

Step E — Configure Maven to use the key
For mac os, in your `~/.m2/settings.xml`:

```XML
<settings>
  <servers>
    <server>
      <id>gpg.passphrase</id>
      <passphrase>YOUR_PASSPHRASE</passphrase>
    </server>
  </servers>
</settings>
```
Then in your main(parent in my cae) pom.xml, under <profiles>:
```xml
<profiles>
  <profile>
    <id>release</id>
    <properties>
      <gpg.passphrase>${gpg.passphrase}</gpg.passphrase>
    </properties>
  </profile>
</profiles>
```
Then run `mvn clean deploy -Prelease`, or `mvn clean deploy -Prelease -Dgpg.passphrase="YOUR_PASSPHRASE"` to include your passphrase
Maven will sign (.asc files appear next to jars/poms).

- Only the public key is uploaded (safe). The private key stays on my machine.
- Maven uses this with maven-gpg-plugin to sign all JARs and POMs during:
  mvn clean deploy -Prelease
- This creates .asc files next to jars/poms (detached signatures).
- Maven Central requires these signatures to accept my artifacts.

# TL;DR GPG KEYS
I generated a GPG key for Maven Central on the 5th of Oct 2025.
Command used: `gpg --full-generate-key`
Type: RSA 4096
Expiration: Never
Email: mail@gmail.com
Keyserver: hkps://keys.openpgp.org
Published public key using: gpg --keyserver hkps://keys.openpgp.org --send-keys D607798E124541F905037CEB5DCCE6FEC886C4ED
Passphrase: [stored in password manager] ;) I don't use it
Usage: Signing Maven Java libraries (Sonatype releases)
This key is used by Maven during mvn deploy to sign artifacts for Central.
To export or share gpg with anothe computer: `gpg --export-secret-keys --armor <your@email.com> > private-key.asc`
    Then move that file (private-key.asc) to your new machine (safely — never share it publicly!), and import it there: `gpg --import private-key.asc`
    Verify it is imported: `gpg --list-secret-keys --keyid-format LONG`

Do you need one GPG key per email?
No — but it’s best practice to have one GPG key per identity (email)
So, as long as your GPG key’s email matches your GitHub/Maven Central account email, you’re good.

When you run `mvn clean deploy -Prelease` and you have the Maven GPG plugin configured, Maven runs: `gpg --sign`
By default, GPG looks for your default key, which you can check with:`gpg --list-secret-keys --keyid-format LONG`

## For future version change
create a new tag e.g for v1.0.1
```bash
git tag v1.0.1
git push origin v1.0.1
```

Remember to change the project.version.string in the parent's pom.xml