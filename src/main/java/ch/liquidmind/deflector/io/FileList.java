package ch.liquidmind.deflector.io;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

import ch.liquidmind.deflector.BootstrapWrapper;

public class FileList extends ArrayList< String >
{
	private static final long serialVersionUID = 1L;
	
	public FileList()
	{
		super();
	}
	
	private static List< String > getFiles( String ... filesAsArray )
	{
		List< String > filesSet = new ArrayList< String >();
		
		for ( String file : filesAsArray )
			filesSet.add( file );
		
		return filesSet;
	}

	public FileList getAbsoluteFileList()
	{
		FileList absFileSet = new FileList();
		
		for ( String file : this )
			absFileSet.add( getAbsoluteFile( file ) );

		return absFileSet;
	}

	public List< File > getFiles()
	{
		List< File > files = new ArrayList< File >();
		
		for ( String file : this )
			files.add( Paths.get( file ).toFile() );

		return files;
	}

	public List< File > getAbsoluteFiles()
	{
		List< File > files = new ArrayList< File >();
		
		for ( String file : this )
			files.add( new File( getAbsoluteFile( file ) ) );

		return files;
	}
	
	protected String getAbsoluteFile( String file )
	{
		return Paths.get( file ).toAbsolutePath().toString();
	}

	@Override
	public String toString()
	{
		return objectIdentifierToString() + setToString();
	}
	
	protected String objectIdentifierToString()
	{
		return this.getClass().getName() + "@" + this.hashCode() + System.lineSeparator();
	}
	
	protected String setToString()
	{
		String setAsString = "{" + System.lineSeparator();
		
		for ( String value : this )
			setAsString += "\t" + value.toString() + System.lineSeparator();
		
		setAsString += "}" + System.lineSeparator();
		
		return setAsString;
	}
	
	public boolean isSetNewerThanFile( String file )
	{
		boolean isSetNewerThanFile = false;
		File targetFile = new File( file );
		
		for ( File fileFromSet : getAbsoluteFiles() )
		{
			if ( fileFromSet.lastModified() > targetFile.lastModified() )
			{
				isSetNewerThanFile = true;
				break;
			}
		}

		return isSetNewerThanFile;
	}
	
	public FileList addFiles( URL[] urls )
	{
		List< String > files = new ArrayList< String >();
		
		for ( URL url : urls )
			files.add( Paths.get( BootstrapWrapper.URL_toURI( url ) ).toFile().getAbsolutePath() );
		
		addAll( files );
		return this;
	}
	
	public FileList addFiles( List< String > files )
	{
		addAll( files );
		return this;
	}
	
	public FileList addFiles( String ... files )
	{
		addAll( getFiles( files ) );
		return this;
	}
	
	public FileList addFiles( String baseDir, IOFileFilter fileFilter )
	{
		addAll( DeflectorIO.getFiles( baseDir, fileFilter ) );
		
		return this;
	}
	
	public FileList addFiles( String baseDir, IOFileFilter fileFilter, IOFileFilter dirFilter )
	{
		addAll( DeflectorIO.getFiles( baseDir, fileFilter, dirFilter ) );
		
		return this;
	}
}
