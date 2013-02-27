package org.rosuda.util.db;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class CryptUtil {

    private StandardPBEStringEncryptor encryptor;

    protected CryptUtil() {
	this.encryptor = new StandardPBEStringEncryptor();
	this.encryptor.setPassword("default");
    }

    public String encrypt(final String string) {
	return encryptor.encrypt(string);
    }

}
