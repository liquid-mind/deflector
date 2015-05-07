package ch.liquidmind.deflector;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;

public class DeflectorConfig
{
	public static final boolean DEBUG_MODE = false;
	
	public static final String DEFAULT_CLASS_PREFIX = "__";
	public static final String DEFAULT_PACKAGE_PREFIX = "__";
	public static final String DEFAULT_SOURCE_DIR = "/src";
	public static final String DEFAULT_TARGET_DIR = "/target";
	public static final String DEFAULT_OUTPUT_DIR = Paths.get( "" ).toAbsolutePath().toString();
	public static final String DEFAULT_CLASSPATH = "";
	public static final String DEFAULT_INTERMEDIATE_DIR = new File( FileUtils.getTempDirectory(), UUID.randomUUID().toString() ).getAbsolutePath();
	public static final String DEFAULT_INTERMEDIATE_SRC_DIR = DEFAULT_INTERMEDIATE_DIR + DEFAULT_SOURCE_DIR;
	public static final String DEFAULT_INTERMEDIATE_TARGET_DIR = DEFAULT_INTERMEDIATE_DIR + DEFAULT_TARGET_DIR;
	public static final String DEFAULT_KEEP_INTERMEDIATE_DIR = Boolean.FALSE.toString();
	public static final String DEFAULT_JAVA_VERSION = getDefaultJavaVersion();
	
	public static final String JAR_OPTION = "jar";
	public static final String CLASSPATH_OPTION = "classpath";
	public static final String OUTPUT_OPTION = "output";
	public static final String INTERMEDIATE_SRC_OPTION = "intermediateSrc";
	public static final String INTERMEDIATE_TARGET_OPTION = "intermediateTarget";
	public static final String KEEP_INTERMEDIATE_OPTION = "keepIntermediate";
	public static final String INCLUDES_OPTION = "includes";
	public static final String EXCLUDES_OPTION = "excludes";
	public static final String HELP_OPTION = "help";
	public static final String DEBUG_OPTION = "debug";
	public static final String JAVA_VERSION_OPTION = "javaVersion";

	private static String classPrefix;
	private static String packagePrefix;
	private static String intermediateSrcDir;
	private static String intermediateTargetDir;
	private static boolean keepIntermediateDir;
	private static Set< String > includes = new HashSet< String >();
	private static Set< String > excludes = new HashSet< String >();
	private static String inputJarFileName;
	private static String outputDir;
	private static String classpath;
	private static int javaMajorVersion;
	private static int javaMinorVersion;
	
	public static boolean initialize( String ... cliArgs )
	{
		Options options = setupOptions();
		CommandLine commandLine = parseCommandLine( options, cliArgs );
		boolean success = setupConfig( commandLine, options );
		
		return success;
	}
	
	private static boolean setupConfig( CommandLine commandLine, Options options )
	{
		boolean success = true;
		
		if ( commandLine == null || commandLine.hasOption( HELP_OPTION ) )
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "deflector", options );
			success = false;
		}
		else
		{
			if ( commandLine.hasOption( JAR_OPTION ) )
				inputJarFileName = commandLine.getOptionValue( JAR_OPTION );
			
			if ( commandLine.hasOption( CLASSPATH_OPTION ) )
				classpath = commandLine.getOptionValue( CLASSPATH_OPTION );
			else
				classpath = DEFAULT_CLASSPATH;
			
			if ( commandLine.hasOption( OUTPUT_OPTION ) )
				outputDir = commandLine.getOptionValue( OUTPUT_OPTION );
			else
				outputDir = DEFAULT_OUTPUT_DIR;
			
			if ( commandLine.hasOption( INTERMEDIATE_SRC_OPTION ) )
				intermediateSrcDir = commandLine.getOptionValue( INTERMEDIATE_SRC_OPTION );
			else
				intermediateSrcDir = DEFAULT_INTERMEDIATE_SRC_DIR;
			
			if ( commandLine.hasOption( INTERMEDIATE_TARGET_OPTION ) )
				intermediateTargetDir = commandLine.getOptionValue( INTERMEDIATE_TARGET_OPTION );
			else
				intermediateTargetDir = DEFAULT_INTERMEDIATE_TARGET_DIR;
			
			if ( commandLine.hasOption( KEEP_INTERMEDIATE_OPTION ) )
				keepIntermediateDir = Boolean.valueOf( commandLine.getOptionValue( KEEP_INTERMEDIATE_OPTION ) );
			else
				keepIntermediateDir = Boolean.valueOf( DEFAULT_KEEP_INTERMEDIATE_DIR );
			
			if ( commandLine.hasOption( INCLUDES_OPTION ) )
				includes.addAll( Arrays.asList( commandLine.getOptionValues( INCLUDES_OPTION ) ) );
			
			if ( commandLine.hasOption( EXCLUDES_OPTION ) )
				excludes.addAll( Arrays.asList( commandLine.getOptionValues( EXCLUDES_OPTION ) ) );
			
			String javaVersion = null;
			
			if ( commandLine.hasOption( JAVA_VERSION_OPTION ) )
				javaVersion = commandLine.getOptionValue( JAVA_VERSION_OPTION );
			else
				javaVersion = DEFAULT_JAVA_VERSION;

			javaMajorVersion = parseJavaMajorVersion( javaVersion );
			javaMinorVersion = parseJavaMinorVersion( javaVersion );
		}
		
		return success;
	}

	private static CommandLine parseCommandLine( Options options, String[] cliArgs )
	{
		CommandLineParser parser = new PosixParser();
		CommandLine commandLine = null;
		
		try
		{
			commandLine = parser.parse( options, cliArgs );
		}
		catch ( ParseException e )
		{
		}
		
		return commandLine;
	}
	
	@SuppressWarnings( "static-access" )
	private static Options setupOptions()
	{
		Option jarOption = OptionBuilder
			.withLongOpt( JAR_OPTION )
			.withDescription( "jar to deflect" )
			.hasArg()
			.isRequired()
			.create();
		Option classpathOption = OptionBuilder
			.withLongOpt( CLASSPATH_OPTION )
			.withDescription( "deflector classpath" )
			.hasArg()
			.create();
		Option outputOption = OptionBuilder
			.withLongOpt( OUTPUT_OPTION )
			.withDescription( "output location" )
			.hasArg()
			.isRequired()
			.create();
		Option intermediateSrcOption = OptionBuilder
			.withLongOpt( INTERMEDIATE_SRC_OPTION )
			.withDescription( "intermediate source output location" )
			.hasArg()
			.create();
		Option intermediateTargetOption = OptionBuilder
			.withLongOpt( INTERMEDIATE_TARGET_OPTION )
			.withDescription( "intermediate target output location" )
			.hasArg()
			.create();
		Option keepIntermediateOption = OptionBuilder
			.withLongOpt( KEEP_INTERMEDIATE_OPTION )
			.withDescription( "keep intermediate output (true|false)" )
			.hasArg()
			.create();
		Option includesOption = OptionBuilder
			.withLongOpt( INCLUDES_OPTION )
			.withDescription( "packages to include" )
			.hasArgs()
			.withValueSeparator( ' ' )
			.create();
		Option excludesOption = OptionBuilder
			.withLongOpt( EXCLUDES_OPTION )
			.withDescription( "packages to exclude" )
			.hasArgs()
			.withValueSeparator( ' ' )
			.create();
		Option helpOption = OptionBuilder
			.withLongOpt( HELP_OPTION )
			.withDescription( "show this help message" )
			.create();
		Option debugOption = OptionBuilder
			.withLongOpt( DEBUG_OPTION )
			.withDescription( "start in debug mode" )
			.create();
		Option javaVersionOption = OptionBuilder
			.withLongOpt( JAVA_VERSION_OPTION )
			.withDescription( "java version of source jar" )
			.hasArgs()
			.create();

		Options options = new Options();
		options.addOption( jarOption );
		options.addOption( classpathOption );
		options.addOption( outputOption );
		options.addOption( intermediateSrcOption );
		options.addOption( intermediateTargetOption );
		options.addOption( keepIntermediateOption );
		options.addOption( includesOption );
		options.addOption( excludesOption );
		options.addOption( helpOption );
		options.addOption( debugOption );
		options.addOption( javaVersionOption );
			
		return options;
	}
	
	// USER CONFIGURABLE ITEMS
	public static String getClassPrefix()
	{
		return ( classPrefix == null ? DEFAULT_CLASS_PREFIX : classPrefix );
	}	

	public static String getPackagePrefix()
	{
		return ( packagePrefix == null ? DEFAULT_PACKAGE_PREFIX : packagePrefix );
	}
	
	public static Set< String > getIncludes()
	{
		return includes;
	}
	
	public static Set< String > getExcludes()
	{
		return excludes;
	}
	
	public static String getInputJarFileName()
	{
		return inputJarFileName;
	}
	
	public static String getOutputDir()
	{
		return outputDir;
	}

	public static boolean getKeepIntermediate()
	{
		return keepIntermediateDir;
	}
	
	public static String getIntermediateSourceDir()
	{
		return intermediateSrcDir;
	}

	public static String getIntermediateTargetDir()
	{
		return intermediateTargetDir;
	}

	public static int getJavaMajorVersion()
	{
		return javaMajorVersion;
	}

	public static int getJavaMinorVersion()
	{
		return javaMinorVersion;
	}
	
	public static String getJavaVersion()
	{
		return javaMajorVersion + "." + javaMinorVersion;
	}
	
	public static boolean isJavaVersionPost1dot5()
	{
		boolean isJavaVersionPost1dot5 = false;
		
		if ( DeflectorConfig.getJavaMajorVersion() >= 1 &&  DeflectorConfig.getJavaMinorVersion() >= 5 )
			isJavaVersionPost1dot5 = true;
		
		return isJavaVersionPost1dot5;
	}
	
	public static URL[] getFullClasspath()
	{
		List< String > fullClasspathAsString = new ArrayList< String >();
		
		fullClasspathAsString.addAll( Arrays.asList( classpath.split( ":|;" ) ) );
		fullClasspathAsString.add( inputJarFileName );
	
		List< URL > fullClasspath = new ArrayList< URL >();
		
		for ( String classpathEntry : fullClasspathAsString )
			fullClasspath.add( BootstrapWrapper.URI_toURL( new File( classpathEntry ).toURI() ) );
		
		return fullClasspath.toArray( new URL[ fullClasspath.size() ] );
	}
	
	private static String getDefaultJavaVersion()
	{
		String javaVersion = System.getProperty( "java.version" );
		String defaultJavaVersion = javaVersion.substring( 0, javaVersion.lastIndexOf( "." ) );
		
		return defaultJavaVersion;
	}
	
	private static int parseJavaMajorVersion( String javaVersion )
	{
		return Integer.valueOf( javaVersion.substring( 0, javaVersion.indexOf( "." ) ) );
	}
	
	private static int parseJavaMinorVersion( String javaVersion )
	{
		return Integer.valueOf( javaVersion.substring( javaVersion.indexOf( "." ) + 1,  javaVersion.length() ) );
	}
}
