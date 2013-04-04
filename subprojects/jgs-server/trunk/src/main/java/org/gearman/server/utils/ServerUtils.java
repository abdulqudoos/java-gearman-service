package org.gearman.server.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ServerUtils {
	private ServerUtils() {}
	
	public static final URL[] createUrlClasspath(String classpath) {
		String[] paths = classpath.split(File.pathSeparator);
		
		List<URL> urls = new LinkedList<URL>();
		for(String path : paths) {
			if(path.isEmpty()) continue;
			if(path.endsWith("*")) {
				// jar wildcard
				
				FileFilter jarFilter = new JarFileFilter();
				
				String newPath = path.substring(0, path.lastIndexOf(File.separatorChar));
				File file = new File(newPath);
				if(file.isDirectory()) {
					File[] jars = file.listFiles(jarFilter);
					if(jars==null) continue;
					
					for(File jar : file.listFiles(jarFilter)) {
						addURL(urls, jar);
					}
				}
			} else {
				addURL(urls, new File(path));
			}
		}
		
		return urls.toArray(new URL[urls.size()]);
	}
	
	private final static void addURL(List<URL> urls, File file) {

		try {
			file = new File(file.getCanonicalPath());
			urls.add(file.toURI().toURL());
		} catch (IOException e) {
			// skip this path
		}
	}
	
	private static final class JarFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory()) return false;
			
			String name = pathname.getName();
			int size = name.length();
			String extension = name.substring(size-4, size);
			
			return extension.equalsIgnoreCase(".jar");
		}
		
	}
}
