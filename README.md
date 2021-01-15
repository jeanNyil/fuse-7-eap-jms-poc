# fuse-7-eap-jms-poc

## Pre-requisites

- AMQ 7 Broker instance configured and running

## Deploy on EAP 7.3 using the wildfly-maven-plugin

```
mvn clean install -Pdeploy
```

## Undeploy from EAP 7.3 using the wildfly-maven-plugin

```
mvn clean -Pdeploy
```
