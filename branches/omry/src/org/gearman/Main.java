package org.gearman;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.gearman.core.GearmanVariables;
import org.gearman.util.ArgumentParser;
import org.gearman.util.ArgumentParser.Option;

/**
 * The class that starts the standalone server
 * @author isaiah.v
 */
class Main {
	
	private static String HELP = 
		GearmanVariables.VERSION + "\n" +
		"\n" +
		"usage:\n" +
		"java [jvm options] -jar "+GearmanVariables.VERSION+".jar [server options]\n" +
		"\n" +
		"Options:\n" +
		"   -p PORT   --port=PORT     Defines what port number the server will listen on (Default: 4730)\n" +
		"   -l LEVEL  --logger=LEVEL  Specifies the logging level (Default: 0)\n" +
		"   -v        --version       Display the version of java gearman service and exit\n" +
		"   -f        --log4j conf    Log4j configuration file, defaults to log4j.xml in working directory\n" +		
		"   -?        --help          Print this help menu and exit";
	
	/**
	 * Prints the current version and 
	 * @param out
	 */
	private static final void printHelp(PrintStream out) {
		out.println(HELP);
		System.exit(0);
	}
	
	private static final void printVersion() {
		System.out.println(GearmanVariables.VERSION);
		System.exit(0);
	}
	
	/**
	 * Starts the standalone gearman job server.
	 * @throws IOException 
	 */
	public static void main(final String[] args) {
		try {
			Main main = new Main(args);
			
			Gearman gearman = new Gearman();
			GearmanServer server = gearman.createGearmanServer();
			((ServerImpl)server).closeGearmanOnShutdown(true);
			
			server.openPort(main.getPort());
		} catch (Throwable th) {
			th.printStackTrace();
			System.err.println();
			printHelp(System.err);
		}
	}
	
	private static final void configureRootLogger(int logLevel) {
		Logger root = Logger.getRootLogger();
		Level level = root.getLevel();
		switch(logLevel) {
		case 0:
			level = Level.OFF;
			break;
		case 1:
			level = Level.ERROR;
			break;
		case 2:
			level = Level.WARN;
			break;
		case 3:
			level = Level.INFO;
			break;
		case 4:
			level = Level.DEBUG;
			break;
		case 5:
			level = Level.TRACE;
			break;
		}
		
		root.setLevel(level);
	}
	
	private int port = 4730;
	private int logLevel = -1;
	
	private Main(final String[] args) {
		final ArgumentParser ap = new ArgumentParser();
		
		ap.addOption('p', "port", true);
		ap.addOption('v', "version", false);
		ap.addOption('l', "logger", true);
		ap.addOption('f', "log4j", true);
		ap.addOption('?', "help", false);
		
		
		ArrayList<String> arguments = ap.parse(args);
		if(arguments==null) {
			System.err.println("argument parsing failed");
			System.err.println();
			printHelp(System.err);
		} else if(!arguments.isEmpty()) {
			System.err.print("received unexpected arguments:");
			for(String arg : arguments) {
				System.err.print(" "+arg);
			}
			System.err.println('\n');
			printHelp(System.err);
		}
		
		String log4j = null;
		for(Option op : ap) {
			switch(op.getShortName()) {
			case 'p':
				try {
					this.port = Integer.parseInt(op.getValue());
				} catch(NumberFormatException nfe) {
					System.err.println("failed to parse port to integer: "+op.getValue());
					System.err.println();
					printHelp(System.err);
				}
				break;
			case 'l':
				try {
					
					logLevel = Integer.parseInt(op.getValue());
					
					if(logLevel<0) {
						System.err.println("Illegal Logging Level");
						System.err.println();
						printHelp(System.err);
					}
				} catch(NumberFormatException nfe) {
					System.err.println("failed to parse logger level to integer: "+op.getValue());
					System.err.println();
					printHelp(System.err);
				}
				break;
			case 'f':
				log4j = op.getValue();
			case 'v':
				printVersion();
			case 'h':
				printHelp(System.out);
			}
		}
		
		DOMConfigurator.configure(log4j != null ? log4j : "log4j.xml");
		configureRootLogger(logLevel);
		
	}
	
	private int getPort() {
		return this.port;
	}
}
