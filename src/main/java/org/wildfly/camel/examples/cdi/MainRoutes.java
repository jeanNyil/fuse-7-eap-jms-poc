package org.wildfly.camel.examples.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;

@ApplicationScoped
@ContextName("jms-poc-context")
public class MainRoutes extends RouteBuilder {

    @Inject
	private CamelContext camelCtx;

    @Override
    public void configure() throws Exception {

        // Enable Stream caching
        camelCtx.setStreamCaching(true);
        // Enable MDC logging
        camelCtx.setUseMDCLogging(true);
        // Enable use of breadcrumbId
        camelCtx.setUseBreadcrumb(true);

        from("timer://scheduler?fixedRate=true&period=1000&delay=5000&repeatCount=30")
            .routeId("jms-producer-route")
            .setBody(simple("[${exchangeProperty.CamelTimerCounter}] - Hello from Camel!"))
            .log(LoggingLevel.INFO, ">>> Sending message [${exchangeProperty.CamelTimerCounter}] to TEST.DURABLE.SUB")
            .to("jms-out:topic:TEST.DURABLE.SUB");
        ;
        
        from("jms-in:topic:TEST.DURABLE.SUB?clientId=fuse78-eap-consumer" +
             "&transacted=true" + 
             "&concurrentConsumers=5" +
             "&subscriptionDurable=true" +
             "&subscriptionShared=true" +
             "&subscriptionName=jms-consumer-route")
            .routeId("jms-consumer-route")
            .log(LoggingLevel.INFO, ">>> Received message: ${body}")
        ;
    }
}
