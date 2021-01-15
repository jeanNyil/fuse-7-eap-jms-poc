# fuse-7-eap-jms-poc

## Pre-requisites

- AMQ 7 Broker instance configured and running
- EAP 7.3 with Fuse 7.8/EAP installed
- Adjust the configuration of the [wildfly-maven-plugin](./pom.xml) accoring to your Fuse 7.8/EAP 7.3 environment
    ```
    <plugin>
        <groupId>org.wildfly.plugins</groupId>
        <artifactId>wildfly-maven-plugin</artifactId>
        <configuration>
            <skip>${deploy.skip}</skip>
            <hostname>127.0.0.1</hostname>
            <port>9990</port>
            <username>admin</username>
            <password>P@ssw0rd</password>
            <name>${project.build.finalName}.${project.packaging}</name>
            <filename>${project.build.finalName}.${project.packaging}</filename>
        </configuration>
        <executions>
            <execution>
                <id>wildfly-deploy</id>
                <phase>install</phase>
                <goals>
                    <goal>deploy-only</goal>
                </goals>
            </execution>
            <execution>
                <id>wildfly-undeploy</id>
                <phase>clean</phase>
                <goals>
                    <goal>undeploy</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```

## Deploy on EAP 7.3 using the wildfly-maven-plugin

```
mvn clean install -Pdeploy
```

## Undeploy from EAP 7.3 using the wildfly-maven-plugin

```
mvn clean -Pdeploy
```
