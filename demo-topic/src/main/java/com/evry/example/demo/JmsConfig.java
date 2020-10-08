package com.evry.example.demo;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import com.ibm.mq.jms.MQConnectionFactory;

@Configuration
@ConfigurationProperties("mq")
public class JmsConfig {
	

    private static final String AUTO_ACKNOWLEDGE = "AUTO_ACKNOWLEDGE";
    private static final String CACHE_CONSUMER   = "CACHE_CONSUMER";

    // this one is a value since: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-vs-value
    
    private int                 reconnectOptions;
    private boolean             reconnectOnException;
    private int                 sessionCacheSize;
    private String              username;
    private String              password;
    private String              connectionNameList;
    private String              queueManager;
    private String              channel;
    
    private int                 reconnectTimeout;
    
    
    public void setReconnectOnException(boolean reconnectOnException) {
        this.reconnectOnException = reconnectOnException;
    }

    public void setSessionCacheSize(int sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectionNameList(String connectionNameList) {
        this.connectionNameList = connectionNameList;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setReconnectTimeout(int reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }
    
	

	@Bean("cachedConnectionFactory")
    @Primary
    public ConnectionFactory connectionFactory(UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setReconnectOnException(true);
        cachingConnectionFactory.setSessionCacheSize(10);
        
        cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);

        return cachingConnectionFactory;
    }

    @Bean
    protected UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(MQConnectionFactory mqConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqConnectionFactory);

        return userCredentialsConnectionFactoryAdapter;
    }

    @Bean
    protected MQConnectionFactory mqConnectionFactory()
            throws JMSException {
        MQConnectionFactory mqConnectionFactory = new MQConnectionFactory();
        mqConnectionFactory.setTransportType(com.ibm.msg.client.wmq.WMQConstants.WMQ_CM_CLIENT);
        // see docs at:
        // https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_8.0.0/com.ibm.mq.javadoc.doc/WMQJMSClasses/com/ibm/mq/jms/MQConnectionFactory.html#setConnectionNameList_java.lang.String_
        mqConnectionFactory.setConnectionNameList("10.246.89.118 (1439)");
        mqConnectionFactory.setQueueManager("QMB");
        mqConnectionFactory.setChannel("CLUSTERTEST.CH");
        mqConnectionFactory.setClientReconnectTimeout(3);
        mqConnectionFactory.setClientReconnectOptions(com.ibm.msg.client.wmq.WMQConstants.WMQ_CLIENT_RECONNECT_Q_MGR);
        mqConnectionFactory.setClientID("durable1234");
        
        return mqConnectionFactory;
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager(final ConnectionFactory connectionFactory) {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(connectionFactory);
        return jmsTransactionManager;
    }

    @Bean("jms")
    public JmsComponent createjmsComponent(final ConnectionFactory connectionFactory, final JmsTransactionManager jmsTransactionManager) {
        JmsComponent jmsComponent = JmsComponent.jmsComponentTransacted(connectionFactory, jmsTransactionManager);
        jmsComponent.setTransacted(true);
        jmsComponent.setCacheLevelName(CACHE_CONSUMER);
       
        return jmsComponent;

    }	
    
}
