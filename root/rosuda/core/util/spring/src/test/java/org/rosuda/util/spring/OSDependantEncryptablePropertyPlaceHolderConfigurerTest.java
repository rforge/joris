package org.rosuda.util.spring;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.StringUtils;

public class OSDependantEncryptablePropertyPlaceHolderConfigurerTest {

    private static final String OS_NAME = OSDependantEncryptablePropertyPlaceholderConfigurer.determineOperatingSystemName();
    private OSDependantEncryptablePropertyPlaceholderConfigurer configurer;
    private ApplicationContext beanFactory;

    @Before
    public void setUp() {
	final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setAlgorithm("PBEWithMD5AndDES");
	encryptor.setPassword("default");
	configurer = new OSDependantEncryptablePropertyPlaceholderConfigurer(encryptor);
    }

    @Test
    public void convertPropertyValueSupportsEncryption() {
	assertThat(configurer.convertPropertyValue("ENC(zRSVEnHcqwP57PqbEnSqIw==)"), equalTo("user"));
    }

    @Test
    public void configuredValuesAreLookedUpFirstByOSSpecificType() {
	setupMock();
	final DemoBean demoBean = beanFactory.getBean("demoBean", DemoBean.class);
	assertThat(demoBean, notNullValue());

	assertThat(demoBean.getEncValue(), equalTo("user"));
	assertThat(demoBean.getPlainValue(), containsString("user"));
	assertThat(demoBean.getPlainValue(), containsString(OS_NAME));
    }

    @Test
    public void testXMLConfigurationLookup() {
	final ApplicationContext xmlContext = new ClassPathXmlApplicationContext("/test-context.xml");
	final DemoBean demoBean = xmlContext.getBean(DemoBean.class);
	assertThat(demoBean, notNullValue());
	assertThat(demoBean.getEncValue(), equalTo("user"));
	assertThat(demoBean.getPlainValue(), containsString("user"));
	assertThat(demoBean.getPlainValue(), containsString(OS_NAME));
    }

    // -- helper
    public void setupMock() {
	configurer.setLocation(new InputStreamResource(OSDependantEncryptablePropertyPlaceHolderConfigurerTest.class
		.getResourceAsStream("/resources.properties")));
	final GenericApplicationContext context = new GenericApplicationContext();
	context.getBeanFactory().registerSingleton("propertyPlaceholderConfigurer", configurer);
	final BeanDefinitionBuilder demoBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(DemoBean.class);
	setPropertyValue(demoBeanDefinition, "encValue", "${encValue}");
	setPropertyValue(demoBeanDefinition, "plainValue", "${plainValue}");
	context.registerBeanDefinition("demoBean", demoBeanDefinition.getBeanDefinition());
	//important - the propertyPlaceholderConfigurer is processed as a BeanPost processor. This not automatically invoked in the GenericApplicationContext
	context.refresh();
	this.beanFactory = context;
    }

    public static void setPropertyValue(BeanDefinitionBuilder builder, String propertyName, String propertyValue) {
	if (StringUtils.hasText(propertyValue)) {
	    builder.addPropertyValue(propertyName, propertyValue);
	}
    }

    public static class DemoBean {
	private String plainValue;
	private String encValue;

	public String getPlainValue() {
	    return plainValue;
	}

	public void setPlainValue(String plainValue) {
	    this.plainValue = plainValue;
	}

	public String getEncValue() {
	    return encValue;
	}

	public void setEncValue(String encValue) {
	    this.encValue = encValue;
	}

    }
}
