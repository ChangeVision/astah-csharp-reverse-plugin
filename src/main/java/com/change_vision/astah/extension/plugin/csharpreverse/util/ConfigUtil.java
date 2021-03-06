package com.change_vision.astah.extension.plugin.csharpreverse.util;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

public class ConfigUtil {
	public static final String FILE_NAME = "astah_plugin_csharpreverse.properties";
	public static final String DEFAULT_CSHARP_XML_DIR_KEY = "default_csharprevese_xml_directory";
	
	private static final String FILE_PATH;
	private static Properties config;
	
	static {
		config = new Properties();
		
		StringBuilder builder = new StringBuilder();
		builder.append(System.getProperty("user.home"));
		builder.append(File.separator);
		builder.append(".astah");
		builder.append(File.separator);
		builder.append("professional");
		builder.append(File.separator);
		builder.append(FILE_NAME);
		
		FILE_PATH = builder.toString();
		
		load();
	}

	public static String getDefaultCSharpXmlDirectoryPath() {
		String cSharpDoxygenXmlDirPath = config.getProperty(DEFAULT_CSHARP_XML_DIR_KEY);
		if (StringUtils.isBlank(cSharpDoxygenXmlDirPath)) {
			cSharpDoxygenXmlDirPath = SystemUtils.getUserHome().getAbsolutePath();
		}
		return cSharpDoxygenXmlDirPath;
	}

	public static void saveCSharpXmlPath(String path) {
		config.put(DEFAULT_CSHARP_XML_DIR_KEY, path);
		store();
	}

	public static void load() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(FILE_PATH);
			config.load(stream);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}
	
	public static void store() {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(FILE_PATH);
			config.store(stream, null);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}
}
