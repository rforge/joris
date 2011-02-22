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
	
	public static final void main(final String[] args) {
		final CryptUtil util = new CryptUtil();
		System.out.println(util.encrypt("user"));
		System.out.println(util.encrypt("password"));
	}
}
