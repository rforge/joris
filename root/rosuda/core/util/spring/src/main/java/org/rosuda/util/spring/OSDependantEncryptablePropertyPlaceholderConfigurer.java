package org.rosuda.util.spring;

import java.util.Enumeration;
import java.util.Properties;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.ObjectUtils;

public class OSDependantEncryptablePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    public static final String SUN = "sun";
    public static final String IX = "ix";
    public static final String MAC = "mac";
    public static final String WIN = "win";
    
    private static final String osName = determineOperatingSystemName();
    private final StringEncryptor stringEncryptor;
    private final TextEncryptor textEncryptor;

    public OSDependantEncryptablePropertyPlaceholderConfigurer(final StringEncryptor stringEncryptor) {
	this.stringEncryptor = stringEncryptor;
	this.textEncryptor = null;
    }

    public OSDependantEncryptablePropertyPlaceholderConfigurer(final TextEncryptor textEncryptor) {
	this.stringEncryptor = null;
	this.textEncryptor = textEncryptor;
    }

    @Override
    protected void convertProperties(Properties props) {
	Enumeration<?> propertyNames = props.propertyNames();
	while (propertyNames.hasMoreElements()) {
	    final String orig_propertyName = (String) propertyNames.nextElement();
	    String propertyName = orig_propertyName;
	    final String osPropertyKey = getOsPropertyKey(propertyName);
	    if (props.containsKey(osPropertyKey)) {
		propertyName = osPropertyKey;
	    }
	    final String propertyValue = props.getProperty(propertyName);
	    final String convertedValue = convertProperty(propertyName, propertyValue);
	    if (!ObjectUtils.nullSafeEquals(propertyValue, convertedValue) || !ObjectUtils.nullSafeEquals(orig_propertyName, propertyName)) {
		props.setProperty(orig_propertyName, convertedValue);
	    }
	}
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.config.PropertyResourceConfigurer#
     * convertPropertyValue(java.lang.String)
     */
    protected String convertPropertyValue(final String originalValue) {
	if (!PropertyValueEncryptionUtils.isEncryptedValue(originalValue)) {
	    return originalValue;
	}
	if (this.stringEncryptor != null) {
	    return PropertyValueEncryptionUtils.decrypt(originalValue, this.stringEncryptor);

	}
	return PropertyValueEncryptionUtils.decrypt(originalValue, this.textEncryptor);
    }

    // helper

    protected String getOsPropertyKey(final String propertyName) {
	return new StringBuilder(propertyName).append("_").append(osName).toString();
    }

    protected static String determineOperatingSystemName() {
	String os = System.getProperty("os.name").toLowerCase();
	if (os.indexOf(WIN) >= 0) {
	    return WIN;
	} else if (os.indexOf(MAC) >= 0) {
	    return MAC;
	} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
	    return IX;
	} else if (os.indexOf("sunos") >= 0) {
	    return SUN;
	}
	return "unknown";
    }

}
