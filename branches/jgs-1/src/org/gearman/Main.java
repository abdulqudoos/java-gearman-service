package org.gearman;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			Logger logger = getLogger(main);
			
			Gearman gearman = new Gearman(logger);
			GearmanServer server = gearman.createGearmanServer();
			((ServerImpl)server).closeGearmanOnShutdown(true);
			
			server.openPort(main.getPort());
		} catch (Throwable th) {
			System.err.println(th.getMessage());
			System.err.println();
			printHelp(System.err);
		}
	}
	
	private static final Logger getLogger(Main main) {
		final Level level;
		
		switch(main.getLogger()) {
		case 0:
			level = Level.OFF;
			break;
		case 1:
			level = Level.SEVERE;
			break;
		case 2:
			level = Level.WARNING;
			break;
		default:
			level = Level.INFO;
		}
		
		final Logger logger = Logger.getAnonymousLogger();
		logger.setLevel(level);
		
		return logger;
	}
	
	private int port = 4730;
	private int logger = 0;
	
	private Main(final String[] args) {
		final ArgumentParser ap = new ArgumentParser();
		
		boolean t1, t2, t3, t4;
		t1 = ap.addOption('p', "port", true);
		t2 = ap.addOption('v', "version", false);
		t3 = ap.addOption('l', "logger", true);
		t4 = ap.addOption('?', "help", false);
		
		assert t1&&t2&&t3&&t4;
		
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
					this.logger = Integer.parseInt(op.getValue());
					
					if(logger<0) {
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
			case 'v':
				printVersion();
			case 'h':
				printHelp(System.out);
			}
		}
	}
	
	private int getPort() {
		return this.port;
	}
	
	private int getLogger() {
		return this.logger;
	}
}