package org.rosuda.irconnect.output;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AbstractObjectFormatter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractObjectFormatter.class);

    protected static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";

    private final Map<Class<?>, NumberFormat> numberFormats = new HashMap<Class<?>, NumberFormat>();

    private Properties patternProperties = new Properties();

    protected AbstractObjectFormatter() {
        try {
            patternProperties.load(AbstractObjectFormatter.class.getResourceAsStream("/rsc/NumberFormats.properties"));
        } catch (final IOException e) {
            LOGGER.error("could not load NumberFormats", e);
        }
    }

    public NumberFormat getFormat(final Class<?> formatterClass) {
        if (numberFormats.containsKey(formatterClass)) {
            return numberFormats.get(formatterClass);
        }
        if (patternProperties.containsKey(formatterClass.getSimpleName())) {
            final String pattern = patternProperties.getProperty(formatterClass.getSimpleName());
            final NumberFormat numberFormat = new DecimalFormat(pattern);
            numberFormats.put(formatterClass, numberFormat);
            return numberFormat;
        }
        return null;
    }

    public String getReplacement(String aString) {
        if (aString == null) {
            aString = "null";
        }
        if (patternProperties.containsKey(aString)) {
            return patternProperties.getProperty(aString);
        }
        return aString;
    }
}
