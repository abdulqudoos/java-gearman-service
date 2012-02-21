/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Gearman properties used by the interface 
 * @author isaiah
 */
public class GearmanProperty implements Serializable {
	
	/* ---=== Static Properties ===--- */

	/** The property representing the classpath of the gearman implementation */
	public final static GearmanProperty GEARMAN_CLASSNAME = new GearmanProperty("gearman.classname", "org.gearman.impl.GearmanImpl"); 
	
	/** The default port number */
	public final static GearmanProperty PORT = new GearmanProperty("gearman.port", "4730");
	
	/* ---=== Static Fields ===--- */
	
	/** The path to properties file */
	private static final String PROPERTIES_PATH = ".gearman";
	
	/** Property values */
	private static final Properties properties = new Properties();
	
	/* ---=== Static Methods ===--- */

	/* Static Constructor */
	static {
		final File file = new File(PROPERTIES_PATH);
		if(file.isFile()) {
		
			try (FileInputStream in = new FileInputStream(file)) {
				properties.load(in);
			} catch (IOException e) {
				System.err.println("failed to load properties: " + PROPERTIES_PATH);
				System.exit(1);
			}
		
		}
	}
	
	/**
	 * Returns the property's value
	 * @param property
	 * 		The property
	 * @return
	 * 		The property value
	 */
	public final static String getPropertyValue(GearmanProperty property) {
		return properties.getProperty(property.name, property.defaultValue);
	}
	
	public final static String getPropertyValue(String property) {
		return properties.getProperty(property);
	}
	
	/**
	 * Sets a property value
	 * @param property
	 * 		The property to set
	 * @param value
	 * 		The new property value
	 */
	public final static void setPropertyValue(GearmanProperty property, String value) {
		properties.setProperty(property.name, value);
	}
	
	/**
	 * Saves the property values to the default path location
	 */
	public final static void save() throws IOException {
		save(PROPERTIES_PATH);
	}
	
	/**
	 * Saves the properties to the given path
	 * @param path
	 * 		The path to save the properties to
	 * @throws IOException
	 * 		If an IO exception occurs
	 */
	public final static void save(String path) throws IOException {
		final File file = new File(path);
		final FileOutputStream out = new FileOutputStream(file);
		
		properties.store(out, null);
		
		out.close();
	}
	
	/**
	 * Returns the path to properties file
	 * @return
	 * 		The path to properties file
	 */
	public final static String getPropertiesPath() {
		try {
			final File file = new File(PROPERTIES_PATH);
			if(file.isFile())
				return file.getCanonicalPath();
			else
				return PROPERTIES_PATH;
		} catch (IOException e) {
			return PROPERTIES_PATH;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	/* ---=== Fields ===--- */
	
	/** The property name */
	private final String name;
	
	/** The default property value */
	private final String defaultValue;
	
	/* ---=== Method ===--- */
	
	/**
	 * Constructor
	 * @param name
	 * 		The property name
	 * @param defaultValue
	 * 		The property's default value
	 */
	public GearmanProperty(String name, String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}
}
