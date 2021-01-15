/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2014 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.camel.examples.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;

@ApplicationScoped
@ContextName("camel-cdi-context")
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

        from("timer://scheduler?fixedRate=true&period=1000&delay=5000&repeatCount=10")
            .routeId("jms-producer-route")
            .setBody(simple("[${exchangeProperty.CamelTimerCounter}] - Hello from Camel!"))
            .log(LoggingLevel.INFO, "Sending message [${exchangeProperty.CamelTimerCounter}] to TEST.DURABLE.SUB")
            .to("jms-out:topic:TEST.DURABLE.SUB");
        ;
        
        from("jms-in:topic:TEST.DURABLE.SUB?clientId=fuse78-eap-consumer" +
             "&acknowledgementModeName=CLIENT_ACKNOWLEDGE" + 
             "&concurrentConsumers=5" +
             "&subscriptionDurable=true" +
             "&subscriptionShared=true" +
             "&subscriptionName=jms-consumer-route")
            .routeId("jms-consumer-route")
            .to("bean:mdcHelper?method=addToMDC(accountId, '${header.JMSMessageID}')")
            .log(LoggingLevel.INFO, "${body}")
        ;
    }
}
