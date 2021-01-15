# fuse-7-eap-jms-poc

## Pre-requisites

- [**_Red Hat AMQ 7 Broker_**](https://access.redhat.com/documentation/en-us/red_hat_amq/2020.q4/html/getting_started_with_amq_broker/index) instance configured and running
- **_Red Hat JBoss EAP 7.3_** with [**_Red Hat Fuse 7.8/EAP_**](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.8/html/getting_started/getting-started-with-fuse-on-jboss-eap) installed and running
- Adjust the configuration of the [wildfly-maven-plugin](./pom.xml) according to your **_Red Hat Fuse 7.8/EAP_** environment:
    ```
    [...]
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
    [...]
    ```

## Deploy on EAP 7.3 using the wildfly-maven-plugin

```
mvn clean install -Pdeploy
```

## Undeploy from EAP 7.3 using the wildfly-maven-plugin

```
mvn clean -Pdeploy
```
