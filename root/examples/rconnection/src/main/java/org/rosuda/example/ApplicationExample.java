package org.rosuda.example;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationExample {

    public static void main(String[] args) {
	//load the configuration
	final ApplicationContext springContext = new AnnotationConfigApplicationContext(SpringConfiguration.class);
	final IRConnection connection = springContext.getBean(IRConnection.class);
	
	// do something (more) useful
	// ..
	
	final IREXP aModel = connection.eval("lm(time~dist*climb,data=hills)");
	System.out.println(">>>>>>>>> a model : "+aModel);
	// close connection resource
	connection.shutdown();
    }
}
