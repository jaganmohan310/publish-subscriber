package com.evry.example.demo;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DurbaleSubscriberRoute extends RouteBuilder {
	
	 static final Logger log = LoggerFactory.getLogger(DurbaleSubscriberRoute.class);

	    @Override
	    public void configure() throws Exception {
	        from("{{inbound.endpoint}}")
	          .log(LoggingLevel.DEBUG, log, "New message received")
	          .process(exchange -> {
	              	System.out.println("enrich consumed messages");
	          })
	          .to("{{outbound.endpoint}}")
	          .log(LoggingLevel.DEBUG, log, "Message is successfully sent to the output queue or topic ")
	        .end();
	        
	       	    
	    }
}
