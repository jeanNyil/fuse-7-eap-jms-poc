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
 * Creates an instance of the Camel JmsComponent and configures it to support JMS transactions.
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

        return JmsComponent.jmsComponent(jmsConfiguration);
    }

    @Produces
    @Named("jms-out")
    public JmsComponent createJmsOutComponent() {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("(tcp://localhost:61616,tcp://localhost:62616)?ha=true&reconnectAttempts=10","admin","admin");

        // create pool transaction
        JmsPoolConnectionFactory poolingFactory = new JmsPoolConnectionFactory();

        poolingFactory.setConnectionFactory(connectionFactory);
        poolingFactory.setMaxConnections(1);
        poolingFactory.setMaxSessionsPerConnection(500);
        // The time in milliseconds between periodic checks for expired connections. The default is 0, meaning the check is disabled.
        // /!\ Can cause memory leaks
        // poolingFactory.setConnectionCheckInterval(5000);
        // The time in milliseconds before a connection not currently on loan can be evicted from the pool. 
        // The default is 30 seconds. A value of 0 disables the timeout.
        poolingFactory.setConnectionIdleTimeout(30000);
        
        // create JmsConfiguration for JmsComponent
        JmsConfiguration jmsConfiguration = new JmsConfiguration(poolingFactory);
        jmsConfiguration.setIncludeSentJMSMessageID(true);
        jmsConfiguration.setCacheLevelName("CACHE_AUTO");

        return JmsComponent.jmsComponent(jmsConfiguration);
    }
}