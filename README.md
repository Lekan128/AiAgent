# AiAgent



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