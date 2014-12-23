package ch.liquidmind.deflector;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import ch.liquidmind.deflector.processing.DeflectorWriter;

public class BootstrapWrapper
{
	public static ZipFile ZipFile_new( String name )
	{
		try
		{
			return new ZipFile( name );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static Class< ? > Class_forName( String className )
	{
		try
		{
			return Class.forName( className );
		}
		catch ( ClassNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static URL URI_toURL( URI uri )
	{
		try
		{
			return uri.toURL();
		}
		catch ( MalformedURLException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static URL URL_new( String s )
	{
		try
		{
			return new URL( s );
		}
		catch ( MalformedURLException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static Class< ? > ClassLoader_loadClass( ClassLoader loader, String name )
	{
		try
		{
			return loader.loadClass( name );
		}
		catch ( ClassNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static Method Class_getMethod( Class< ? > aClass, String name, Class< ? >... parameterTypes )
	{
		try
		{
			return aClass.getMethod( name, parameterTypes );
		}
		catch ( NoSuchMethodException | SecurityException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static Method Class_getDeclaredMethod( Class< ? > aClass, String name, Class< ? >... parameterTypes )
	{
		try
		{
			return aClass.getDeclaredMethod( name, parameterTypes );
		}
		catch ( NoSuchMethodException | SecurityException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static Object Method_invoke( Method method, Object obj, Object... args )
	{
		try
		{
			return method.invoke( obj, args );
		}
		catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static void FileUtils_forceMkdir( File dir )
	{
		try
		{
			FileUtils.forceMkdir( dir );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static DeflectorWriter DeflectorWriter_new( String fileName )
	{
		try
		{
			return new DeflectorWriter( fileName );
		}
		catch ( FileNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static FileReader FileReader_new( File file )
	{
		try
		{
			return new FileReader( file );
		}
		catch ( FileNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static String BufferedReader_readLine( BufferedReader br )
	{
		try
		{
			return br.readLine();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static void FileUtils_forceDelete( File file )
	{
		try
		{
			FileUtils.forceDelete( file );
		}
		catch ( IOException e )
		{
			// TODO: Can't delete some temp files under Windows.
			//throw new RuntimeException( e );
		}
	}
	
	public static FileOutputStream FileOutputStream_new( String name )
	{
		try
		{
			return new FileOutputStream( name );
		}
		catch ( FileNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static JarOutputStream JarOutputStream_new( FileOutputStream fos )
	{
		try
		{
			return new JarOutputStream( fos );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static FileInputStream FileInputStream_new( String name )
	{
		try
		{
			return new FileInputStream( name );
		}
		catch ( FileNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static void JarOutputStream_putNextEntry( JarOutputStream jos, ZipEntry ze )
	{
		try
		{
			jos.putNextEntry( ze );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static int IOUtils_copy( InputStream input, OutputStream output )
	{
		try
		{
			return IOUtils.copy( input, output );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static void Closeable_close( Closeable closeable )
	{
		try
		{
			closeable.close();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static CommandLine CommandLineParser_parse( CommandLineParser parser, Options options, String[] arguments )
	{
		try
		{
			return parser.parse( options, arguments );
		}
		catch ( ParseException e )
		{
			throw new RuntimeException( e );
		}
	}
}
