package edu.pitt.sis.exp.colfusion.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides functionality to manage Col*Fusion properties.
 * <p>
 * It is intended to be used as Singleton with lazy loading instance. 
 * When an instance is created, it loads properties from {@value #CONFIG_DEFAULT_FILE_NAME}
 * file that is required to be in the class path. Then it loads properties from {@value #CONFIG_FILE_NAME}
 * file if one found (Note: this file is not required, but should be used if any default properties need to be 
 * overridden). Then it loads properties from file provided via {@value #CONFIG_FILE_NAME_SYSTEM_PROPERTY}
 * system property (Note: not required and can be used to override previously loaded properties).
 * </p>
 * <p>
 * The class provides public methods {@link #getProperty(String)} and {@link #getProperty(String, String)} 
 * that allow to get value of a property by property name (key). If the property was provided as a system 
 * property to JVM then that value will be used, otherwise the value from the config giles will be used.
 * If they key is not found, then null or default value, if provided, will be returned.
 * </p>
 * @author Evgeny
 *
 */
public final class ConfigManager {
	
	private static final Logger logger = LogManager.getLogger(ConfigManager.class.getName());
	
	private static ConfigManager instance = null;
	
	private Properties properties = null;
	
	/**
	 * The name of the properties file that is expected to be found on the class path.
	 * This file is REQUIRED because it provides the default values.
	 * If some properties need to be changed create/update the 
	 * {@value #CONFIG_FILE_NAME} properties file.
	 */
	public final static String CONFIG_DEFAULT_FILE_NAME = "config_default.properties";
	
	/**
	 * The name of the properties file that is expected to be found on the class path.
	 * This file is only required if any of the default properties need to be overridden.
	 * This file's properties will override {@value #CONFIG_DEFAULT_FILE_NAME} properties. 
	 */
	public final static String CONFIG_FILE_NAME = "config.properties";
	
	//TODO: might need to do more work on this
	public final static String CONFIG_TEST_DEFAULT_FILE_NAME = "config_test_default.properties";
	public final static String CONFIG_TEST_FILE_NAME = "config_test.properties";
	
	/**
	 * The system property that can be passed to JVM that provides absolute path to the
	 * properties file that need to be loaded. 
	 * This file's properties will override {@value #CONFIG_FILE_NAME} properties. 
	 */
	public final static String CONFIG_FILE_NAME_SYSTEM_PROPERTY = "colfusion.config.properties";
	
	
	/**
	 * This class is not intended to be initialize. Use {@link #getInstance()}.
	 */
	private ConfigManager() {}
	
	/**
	 * Get the instance of the class.
	 * @return
	 */
	public static ConfigManager getInstance() {
		if(instance == null) {
	         instance = new ConfigManager();
	         instance.loadProperties();
	    }
		
		return instance;
	}
	
	/**
	 * Loads properties from all {@value #CONFIG_DEFAULT_FILE_NAME} and {@value #CONFIG_FILE_NAME} 
	 * properties files found in the class path as well as from 
	 * {@value #CONFIG_FILE_NAME_SYSTEM_PROPERTY} system property if provided.
	 */
	void loadProperties() {
		Properties prop = new Properties();
		
		loadDefaultPropertiesFoundInResource(prop); // throws exception if file not found
		loadCustomPropertiesFoundInResource(prop);
		loadCustomPropertiesProvidedViaSystemProperty(prop); // throws exception if file doesn't exist
		
		instance.properties = prop;
	}

	/**
	 * Load the default required properties from the {@value #CONFIG_DEFAULT_FILE_NAME} file that must 
	 * be in the resources of this class's class path.
	 *  
	 * @param prop
	 * 			the properties object where to load new properties.
	 * @throws RuntimeException if {@link #CONFIG_DEFAULT_FILE_NAME} file is not found in the class resources.
	 */
	void loadDefaultPropertiesFoundInResource(final Properties prop)
			throws RuntimeException {		
		loadPropertiesFromResoruceFile(prop, CONFIG_DEFAULT_FILE_NAME, false);
	}
	
	/**
	 * Load the custom not required properties from the {@value #CONFIG_FILE_NAME} file if 
	 * the file found in the resources of this class's class path.
	 *  
	 * @param prop
	 * 			the properties object where to load new properties.
	 */
	void loadCustomPropertiesFoundInResource(final Properties prop) {
		loadPropertiesFromResoruceFile(prop, CONFIG_FILE_NAME, false);
	}

	/**
	 * @param prop
	 */
	private void loadPropertiesFromResoruceFile(final Properties prop, final String resourceName, final boolean isRequired) {
		String propertiesLocation = ResourceUtils.getResourceAsFileLocation(resourceName);
		InputStream propertyFile = ResourceUtils.getResourceAsStream(resourceName);
		if (propertyFile == null) {
			if (isRequired) {
				String message = String.format("Was trying to load required properties from "
						+ "%s resource file, but got null.", resourceName);
				logger.error(message);
				throw new RuntimeException(message);
			}
			else {
				String message = String.format("Didn't find property file %s in resource",
						CONFIG_FILE_NAME);
				logger.info(message);
			}			
		}
		else {
			loadConfigFile(prop, propertyFile, resourceName, propertiesLocation);
		}
	}
	
	/**
	 * Load the custom not required properties from the file provided
	 * via {@value #CONFIG_FILE_NAME_SYSTEM_PROPERTY} system property.
	 *  
	 * @param prop
	 * 			the properties object where to load new properties.
	 * @throws RuntimeException if {@value #CONFIG_FILE_NAME_SYSTEM_PROPERTY} system property was provided
	 * but the file doesn't exist.
	 */
	void loadCustomPropertiesProvidedViaSystemProperty(final Properties prop)
			throws RuntimeException {
		String propertyFilePath = getSystemProperty(CONFIG_FILE_NAME_SYSTEM_PROPERTY);
		if (propertyFilePath == null) {
			logger.info(String.format("System property '%s' was not provided, so no properties to load", 
					CONFIG_FILE_NAME_SYSTEM_PROPERTY));
		}
		else {
			logger.info(String.format("System property '%s' was provided, "
					+ "so goin load properties from '%s' file", CONFIG_FILE_NAME_SYSTEM_PROPERTY, propertyFilePath));
			File propertyFile = new File(propertyFilePath);
			try (InputStream propertiesFileStream = new FileInputStream(propertyFile)) {
				loadConfigFile(prop, propertiesFileStream, propertyFilePath, propertyFilePath);
			}
			catch (IOException e) {				
				String message = String.format("The '%s' file provided in the '%s' system property doesn't exist. "
						+ "Thus cannot load custom properties.", propertyFilePath, CONFIG_FILE_NAME_SYSTEM_PROPERTY);
				logger.error(message);
				throw new RuntimeException(message);				
			}
		}
	}
	
	/**
	 * Load properties form specified properties file stream.
	 * 
	 * At the end the propertiesFileStream is always closed.
	 * 
	 * @param prop
	 * 			the {@link Properties} object in which to load new properties.
	 * @param propertiesFileStream
	 * 			properties file stream that needs to be loaded.
	 * @param propertiesFilename
	 * 			the name of the properties file (used only for logging).
	 * @param propertiesLocation 
	 * @throws RuntimeException if cannot read the file.
	 */
	private void loadConfigFile(final Properties prop, final InputStream propertiesFileStream, 
			final String propertiesFilename, final String propertiesFileLocation) {

		try {
			prop.load(propertiesFileStream);
			logger.info(String.format("Loaded properties from '%s' located at '%s'", propertiesFilename, propertiesFileLocation));
		} catch (IOException e) {
			String message = String.format("Could not load properties.", propertiesFilename.toString());
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		finally {
			try {
				propertiesFileStream.close();
			} catch (IOException ignore) {}
		}
	}

	/**
	 * Get all properties.
	 * @return
	 */
	public Properties getProperies() {
		return properties;
	}
	
	/**
	 * Get the configuration property value by the property name. 
	 * If the key is not found, then null will be returned.
	 * 
	 * @param propertyName 
	 * 			the name of the property for which to get the value.
	 * @return the value of the property or null if the key is not found.
	 */
	public String getProperty(final IPropertyKeys propertyKey) {
		return getProperty(propertyKey.getKey());
	}
	
	/**
	 * Get the configuration property value by the property name. 
	 * If not found, returns the default value.
	 * 
	 * @param propertyName
	 * 			the name of the property for which to get the value.
	 * @param defaultValue
	 * 			the default value that should be returned if the property name (key) is not found.
	 * @return the value of the property or null if the key is not found.
	 */
	public String getProperty(final IPropertyKeys propertyKey, final String defaultValue) {
		return getProperty(propertyKey.getKey(), defaultValue);
	}
	
	/**
	 * Get the configuration property value by the property name. 
	 * If the key is not found, then null will be returned.
	 * 
	 * @param propertyName 
	 * 			the name of the property for which to get the value.
	 * @return the value of the property or null if the key is not found.
	 */
	public String getProperty(final String propertyKey) {
		String systemProperty = getSystemProperty(propertyKey);
		
		return systemProperty != null ? systemProperty: properties.getProperty(propertyKey);
	}
	
	/**
	 * Get the configuration property value by the property name. 
	 * If not found, returns the default value.
	 * 
	 * @param propertyName
	 * 			the name of the property for which to get the value.
	 * @param defaultValue
	 * 			the default value that should be returned if the property name (key) is not found.
	 * @return the value of the property or null if the key is not found.
	 */
	public String getProperty(final String propertyKey, final String defaultValue) {
		String systemProperty = getSystemProperty(propertyKey);
		
		return systemProperty != null ? systemProperty : properties.getProperty(propertyKey, defaultValue);
	}
	
	/**
	 * Get system property by property name. 
	 * @param propertyName
	 * 			the name of the property.
	 * @return the value of the property or null if the property not provided.
	 */
	private String getSystemProperty(final IPropertyKeys propertyKey) {
		return getSystemProperty(propertyKey.getKey());
	}
	
	/**
	 * Get system property by property name. 
	 * @param propertyName
	 * 			the name of the property.
	 * @return the value of the property or null if the property not provided.
	 */
	private String getSystemProperty(final String propertyKey) {
		return System.getProperty(propertyKey);
	}

	//TODO find a better way to do that
	/**
	 * Load properties from {@value #CONFIG_TEST_DEFAULT_FILE_NAME} and {@value #CONFIG_TEST_FILE_NAME} files.
	 */
	void loadTestProperties() {
		loadPropertiesFromResoruceFile(this.properties, CONFIG_TEST_DEFAULT_FILE_NAME, false);
		loadPropertiesFromResoruceFile(this.properties, CONFIG_TEST_FILE_NAME, false);
	}
}
