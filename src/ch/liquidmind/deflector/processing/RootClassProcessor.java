package ch.liquidmind.deflector.processing;

import java.io.BufferedReader;
import java.io.File;

import ch.liquidmind.deflector.BootstrapWrapper;
import ch.liquidmind.deflector.DeflectorConfig;

public class RootClassProcessor extends ClassProcessor
{
	public static final String JAVA_SUFFIX = ".java";

	public RootClassProcessor( Class< ? > sourceClass )
	{
		super( sourceClass );
	}
	
	@Override
	public void process()
	{
		if ( !isProcessableRootClass( getSourceClass() ) )
			return;
		
		String targetPackageName = getTargetPackageName();
		setWriter( getRootClassWriter() );
		
		try
		{
			getWriter().println( "package " + targetPackageName + ";" );
			getWriter().println();

			createTargetClass();
		}
		finally
		{
			getWriter().close();
		}
	}
	
	public void displayClass()
	{
		File targetClassFile = new File( getTargetClassFileName() );
		
		if ( !targetClassFile.exists() )
			return;
		
		BufferedReader br = new BufferedReader( BootstrapWrapper.FileReader_new( targetClassFile ) );
		String line = null;
		
		while ( ( line = BootstrapWrapper.BufferedReader_readLine( br ) ) != null )
			System.out.println( line );
		
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	private String getTargetPackageName()
	{
		Package aPackage = getSourceClass().getPackage();
		String targetPackageName = ( aPackage != null ? DeflectorConfig.getPackagePrefix() + aPackage.getName() : "" );
		
		return targetPackageName;
	}
	
	private String getTargetClassName()
	{
		return getTargetPackageName() + "." + DeflectorConfig.getClassPrefix() + getSourceClass().getSimpleName();
	}
	
	private String getTargetClassFileName()
	{
		String fileName = getTargetClassName().replace( ".", "/" ) + JAVA_SUFFIX;
		String fqFileName = DeflectorConfig.getIntermediateSourceDir() + "/" + fileName;
		
		return fqFileName;
	}

	private DeflectorWriter getRootClassWriter()
	{
		String fqFileName = getTargetClassFileName();
		File parentDir = new File( fqFileName ).getParentFile();
		
		BootstrapWrapper.FileUtils_forceMkdir( parentDir );
		DeflectorWriter dw = BootstrapWrapper.DeflectorWriter_new( fqFileName );
		
		return dw;
	}
}
