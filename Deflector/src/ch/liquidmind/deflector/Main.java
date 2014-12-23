package ch.liquidmind.deflector;

import java.io.File;

import ch.liquidmind.deflector.io.ArchiveFileList;
import ch.liquidmind.deflector.io.DeflectorIO;
import ch.liquidmind.deflector.io.FileList;
import ch.liquidmind.deflector.processing.RootClassProcessor;

public class Main
{
	public static final String DEBUG_LEVEL = "lines,vars,source";
	public static final String ENCODING = "UTF8";

	public static void main( String[] args )
	{
		if ( DeflectorConfig.initialize( args ) )
			execute();
	}

	private static void execute()
	{
		try
		{
			createIntermediateBaseDir();
			executeWithoutFinally();
			System.out.println( "Done." );
		}
		catch ( DeflectorException e )
		{
			System.out.println( e.getMessage() );
		}
		finally
		{
			cleanupIntermediateBaseDir();
		}
	}

	private static void executeWithoutFinally()
	{
		generate();
		compile();
		jar();
	}
	
	private static void generate()
	{
		System.out.println( "Generating deflector classes..." );

		ClassIterator iter = new ClassIterator();
		
		if ( !iter.hasNext() )
			throw new DeflectorException( "No exceptions to deflect!" );
		
		while ( iter.hasNext() )
		{
			RootClassProcessor processor = new RootClassProcessor( iter.next() );
			processor.process();
			
			if ( DeflectorConfig.DEBUG_MODE )
				processor.displayClass();
		}
	}

	private static void compile()
	{
		System.out.println( "Compiling deflector classes..." );
		boolean success = true;

		try
		{
			new File( DeflectorConfig.getIntermediateTargetDir() ).mkdir();
			FileList classPath = DeflectorIO.fileList().addFiles( DeflectorConfig.getFullClasspath() );
			success = DeflectorIO.compile(
				DeflectorConfig.getIntermediateSourceDir(),
				DeflectorConfig.getIntermediateTargetDir(),
				classPath,
				DEBUG_LEVEL,
				ENCODING,
				DeflectorConfig.getJavaVersion(),
				DeflectorConfig.getJavaVersion() );
		}
		catch ( Throwable t )
		{
			success = false;
			t.printStackTrace();
		}
		
		if ( !success )
			throw new DeflectorException( "Error during compilation!" );
	}

	private static void jar()
	{
		String targetJarFileName = new File( DeflectorConfig.getOutputDir(), DeflectorConfig.getClassPrefix() + new File( DeflectorConfig.getInputJarFileName() ).getName() ).getAbsolutePath();
		
		System.out.println( "Creating jar: " + targetJarFileName );

		try
		{
			ArchiveFileList classFiles = (ArchiveFileList)DeflectorIO.archiveFileList().setBaseDir( DeflectorConfig.getIntermediateTargetDir() ).setArchivePrefix( "" ).addFiles( DeflectorIO.CLASS_FILE_FILTER );
			DeflectorIO.jar( targetJarFileName, classFiles );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
			throw new DeflectorException( "Error while jaring!" );
		}
	}
	
	private static void createIntermediateBaseDir()
	{
		BootstrapWrapper.FileUtils_forceMkdir( new File( DeflectorConfig.getIntermediateSourceDir() ) );
		BootstrapWrapper.FileUtils_forceMkdir( new File( DeflectorConfig.getIntermediateTargetDir() ) );
	}
	
	private static void cleanupIntermediateBaseDir()
	{
		if ( !DeflectorConfig.getKeepIntermediate() )
		{
			BootstrapWrapper.FileUtils_forceDelete( new File( DeflectorConfig.getIntermediateSourceDir() ) );
			BootstrapWrapper.FileUtils_forceDelete( new File( DeflectorConfig.getIntermediateTargetDir() ) );
		}
	}
}
