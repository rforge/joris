package org.rosuda.util.db;

public class DataSourceConfiguration {

	private String driverName;
	private String username;
	private String password;
	private String url;

	public String getDriverClassName() {
		return driverName;
	}

	public void setDriverClassName(String driverName) {
		this.driverName = driverName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

}
