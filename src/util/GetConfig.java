package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetConfig {
	
	public String getProperties(String name) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.properties");
		Properties propertie = new Properties();
		try {
			propertie.load(in);
			return propertie.getProperty(name);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
