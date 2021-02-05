package org.wildfly.camel.examples.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import javax.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

import org.apache.camel.component.jms.JmsComponent;
import org.springframework.jms.connection.JmsTransactionManager;

/**
 * Creates instances of the Camel JmsComponent
 */
public class JmsComponentProducer {

    @Produces
    @Named("jms-in")
    public JmsComponent createJmsInComponent() {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("(tcp://localhost:61616,tcp://localhost:62616)?ha=true&reconnectAttempts=10","admin","admin");

        // Set transaction manager
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager(connectionFactory);
        
        // create JmsConfiguration for JmsComponent
        JmsConfiguration jmsConfiguration = new JmsConfiguration(connectionFactory);
        jmsConfiguration.setTransactionManager(jmsTransactionManager);
        jmsConfiguration.setCacheLevelName("CACHE_CONSUMER");
        jmsConfiguration.setTransacted(true);

        return JmsComponent.jmsComponent(jmsConfiguration);
    }

    @Produces
    @Named("jms-out")
    public JmsComponent createJmsOutComponent() {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("(tcp://localhost:61616,tcp://localhost:62616)?ha=true&reconnectAttempts=10","admin","admin");

        // create pool transaction
        JmsPoolConnectionFactory poolingFactory = new JmsPoolConnectionFactory();
        poolingFactory.setConnectionFactory(connectionFactory);
        // The maximum number of connections for a single pool. The default is 1.
        poolingFactory.setMaxConnections(1);
        // The maximum number of sessions for each connection. The default is 500. A negative value removes any limit.
        poolingFactory.setMaxSessionsPerConnection(500);
        // The time in milliseconds between periodic checks for expired connections. 
        // The default is 0, meaning the check is disabled. /!\ Can cause memory leaks if enabled
        // poolingFactory.setConnectionCheckInterval(5000); 
        // The time in milliseconds before a connection not currently on loan can be evicted from the pool. 
        // The default is 30 seconds. A value of 0 disables the timeout.
        poolingFactory.setConnectionIdleTimeout(30000); 
        // If enabled, use a single anonymous JMS MessageProducer for all calls to createProducer(). 
        // It is enabled by default.
        poolingFactory.setUseAnonymousProducers(false); 
        
        // create JmsConfiguration for JmsComponent
        JmsConfiguration jmsConfiguration = new JmsConfiguration(poolingFactory);
        jmsConfiguration.setIncludeSentJMSMessageID(true);
        jmsConfiguration.setCacheLevelName("CACHE_AUTO");

        return JmsComponent.jmsComponent(jmsConfiguration);
    }
}