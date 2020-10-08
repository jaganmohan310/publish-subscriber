package com.evry.example.demo;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DurableProducer implements CommandLineRunner{
	
	private static final Logger logger = LoggerFactory.getLogger(DurableProducer.class);
	
	@Autowired
	ProducerTemplate producerTemplate;

    
    @Override
    public void run(String... args) throws Exception {
    	for(String arg:args) {
          
          producerTemplate.sendBody("jms:topic:EOM.D6.FROM.TC?explicitQosEnabled=true&timeToLive=36000", arg);
           producerTemplate.sendBody("jms:topic:CORERENEWALTOPIC?explicitQosEnabled=true&disableTimeToLive=true", arg);
        }
    	

      }

}
