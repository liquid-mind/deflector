package ch.liquidmind.deflector.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

import ch.liquidmind.deflector.BootstrapWrapper;

public class DeflectorIO
{
	public static final String SOURCE_SUFFIX = "java";
	public static final String CLASS_SUFFIX = "class";
	public static final String JAR_SUFFIX = "jar";
	
	public static final IOFileFilter SOURCE_FILE_FILTER = FileFilterUtils.suffixFileFilter( SOURCE_SUFFIX );
	public static final IOFileFilter CLASS_FILE_FILTER = FileFilterUtils.suffixFileFilter( CLASS_SUFFIX );
	public static final IOFileFilter JAR_FILE_FILTER = FileFilterUtils.suffixFileFilter( JAR_SUFFIX );
	public static final IOFileFilter ANY_FILE_FILTER = FileFilterUtils.trueFileFilter();
	public static final IOFileFilter HIDDEN_FILE_FILTER = HiddenFileFilter.HIDDEN;
	
	public static boolean compile( String srcDir, String destDir, FileList classpath, String debugLevel, String encoding, String sourceVersion, String targetVersion )
	{
		return compile( fileList().addFiles( srcDir ), destDir, classpath, debugLevel, encoding, sourceVersion, targetVersion );
	}
	
	public static boolean compile( FileList srcDirs, String destDir, FileList classpath, String debugLevel, String encoding, String sourceVersion, String targetVersion )
	{
		FileList compilableFiles = getCompilableFiles( srcDirs, destDir );
		classpath.add( destDir );
		
		String classpathAsString = StringUtils.join( classpath, System.getProperty( "path.separator" ) );
		Iterable< String > compilationOptions = Arrays.asList( new String[] {
			"-d", destDir,
			"-classpath", classpathAsString,
			"-encoding", encoding,
			"-g:" + debugLevel,
			"-source", sourceVersion,
			"-target", targetVersion } );
		
		JavaCompiler compiler = getJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, Locale.getDefault(), null );
		DiagnosticCollector< ? super JavaFileObject > diagnostics = new DiagnosticCollector< JavaFileObject >();
		List< File > compilableFilesAbsolute = compilableFiles.getAbsoluteFiles();
	
		Iterable< ? extends JavaFileObject > compilationUnits = fileManager.getJavaFileObjectsFromFiles( compilableFilesAbsolute );
		CompilationTask compilerTask = compiler.getTask( null, fileManager, diagnostics, compilationOptions, null, compilationUnits );
		boolean status = compilerTask.call();

		if ( !status )
		{
			for ( Diagnostic< ? > diagnostic : diagnostics.getDiagnostics() )
				System.out.format( "Error on line %d in %s", diagnostic.getLineNumber(), diagnostic );
		}
		
		return status;
	}
	
	private static JavaCompiler getJavaCompiler()
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		if ( compiler == null )
			throw new RuntimeException( "No Java compiler found; tools.jar not on classpath. Make sure that the 'java' command is from a JDK and not from a JRE." );
		
		return compiler;
	}
	
	private static FileList getCompilableFiles( FileList srcDirs, String destDir )
	{
		FileList files = new FileList();
		
		for ( String srcDir : srcDirs )
			files.addAll( getCompilableFiles( srcDir, destDir ) );

		return files;
	}
	
	private static FileList getCompilableFiles( String srcDir, String destDir )
	{
		FileList sourceFiles = fileList().addFiles( srcDir, SOURCE_FILE_FILTER );
		FileList targetFiles = fileList().addFiles( destDir, CLASS_FILE_FILTER );
		FileList compilableFiles = new FileList();
		
		for ( String sourceFileAsString : sourceFiles )
		{
			File sourceFile = new File( sourceFileAsString );
			String relSrcFileName = Paths.get( srcDir ).relativize( Paths.get( sourceFile.getAbsolutePath() ) ).toString();
			String relTargetFileName = relSrcFileName.substring( 0, relSrcFileName.length() - SOURCE_SUFFIX.length() ) + CLASS_SUFFIX;
			File targetFile = new File( destDir, relTargetFileName );
			
			if ( targetFiles.contains( targetFile.toString() ) && ( targetFile.lastModified() > sourceFile.lastModified() ) )
				continue;
			
			compilableFiles.add( sourceFile.toString() );
		}
		
		return compilableFiles;
	}

	public static void jar( String destinationFile, ArchiveFileList ... archiveFileSets )
	{
		jar( destinationFile, new Manifest(), archiveFileSets );
	}
	
	public static void jar( String destinationFile, Manifest manifest, ArchiveFileList ... archiveFileSets )
	{
		FileOutputStream fileOutputStream = BootstrapWrapper.FileOutputStream_new( destinationFile );
		JarOutputStream jarOutputStream = BootstrapWrapper.JarOutputStream_new( fileOutputStream );
		
		try
		{
			for ( ArchiveFileList archiveFileSet : archiveFileSets )
			{
				for ( String includedFile : archiveFileSet.getAbsoluteFileList() )
				{
					// Determine entry name
					Path relFileName = Paths.get( archiveFileSet.getBaseDirectory() ).relativize( Paths.get( includedFile ) );
					String jarFileName = Paths.get( archiveFileSet.getArchivePrefix(), relFileName.toString() ).toString();
					
					// Get the input stream.
					FileInputStream fileInputStream = BootstrapWrapper.FileInputStream_new( includedFile );
					
					// Create the entry
                    try
                    {
                        BootstrapWrapper.JarOutputStream_putNextEntry( jarOutputStream, new ZipEntry( jarFileName ) );
                        BootstrapWrapper.IOUtils_copy( fileInputStream, jarOutputStream );
                    }
                    finally
                    {
                        BootstrapWrapper.Closeable_close(fileInputStream);
                    }
                }
			}
		}
		finally
		{
			BootstrapWrapper.Closeable_close( jarOutputStream );
			BootstrapWrapper.Closeable_close( fileOutputStream );
		}
	}
	
	public static List< String > getFiles( String baseDir, IOFileFilter fileFilter )
	{
		return getFiles( baseDir, fileFilter, DirectoryFileFilter.DIRECTORY );
	}
	
	public static List< String > getFiles( String baseDir, IOFileFilter fileFilter, IOFileFilter dirFilter )
	{
		Collection< File > filesAsCollection = FileUtils.listFiles( new File( baseDir ), fileFilter, dirFilter );
		List< String > files = new ArrayList< String >();
		
		for ( File file : filesAsCollection )
			files.add( file.getAbsolutePath() );
		
		return files;
	}
	
	public static ArchiveFileList archiveFileList()
	{
		return new ArchiveFileList();
	}

	public static RelativeFileList relativeFileList()
	{
		return new RelativeFileList();
	}
	
	public static FileList fileList()
	{
		return new FileList();
	}
	
	public static IOFileFilter notFileFilter( IOFileFilter filter )
	{
		return new NotFileFilter( filter );
	}
	
	public static IOFileFilter wildcardFileFilter( String wildcard )
	{
		return new WildcardFileFilter( wildcard );
	}
	
	public static IOFileFilter orFileFilter( IOFileFilter ... filters )
	{
		return new OrFileFilter( Arrays.asList( filters ) );
	}
}
