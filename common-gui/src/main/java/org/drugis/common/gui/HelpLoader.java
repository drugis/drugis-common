package org.drugis.common.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HelpLoader {
	private static final String TEXT_NOT_FOUND_ERROR = "<p><b>Error: </b>Help text not found.</p>";

	private Properties d_properties;

	public HelpLoader(InputStream is) {
		Properties properties = new Properties();
		try {
			properties.load(is);
			is.close();
		} catch (IOException e) {
			d_properties = properties;
			throw new RuntimeException("Could not initialize help text.", e);
		}
		d_properties = properties;
	}

	public String getHelpText(String key) {
		return d_properties.getProperty(key, TEXT_NOT_FOUND_ERROR);
	}
}