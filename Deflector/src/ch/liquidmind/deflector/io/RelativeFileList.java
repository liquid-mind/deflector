package ch.liquidmind.deflector.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

public class RelativeFileList extends FileList
{
	private static final long serialVersionUID = 1L;
	
	private String baseDirectory;

	public RelativeFileList()
	{
		super();
	}

	public String getBaseDirectory()
	{
		return baseDirectory;
	}

	@Override
	protected String getAbsoluteFile( String file )
	{
		return Paths.get( baseDirectory, file ).toString();
	}

	@Override
	public String toString()
	{
		return objectIdentifierToString() + baseDirectoryToString() + setToString();
	}
	
	protected String baseDirectoryToString()
	{
		return baseDirectory + System.lineSeparator();
	}

	private List< String > relativizeFiles( Collection< ? extends String > files )
	{
		List< String > relativizedFiles = new ArrayList< String >();
		
		for ( String file : files )
			relativizedFiles.add( relativizeFile( file ) );
		
		return relativizedFiles;
	}
	
	private String relativizeFile( String file )
	{
		Path filePath = Paths.get( file );
		Path baseDirectoryPath = Paths.get( baseDirectory );
		Path relFilePath = null;
		
		if ( filePath.startsWith( baseDirectoryPath ) )
			relFilePath = baseDirectoryPath.relativize( filePath );
		else
			throw new RuntimeException( "File path not relative to base directory: filePath=" + file + " baseDirectory=" + baseDirectory );

		return relFilePath.toString();
	}

	@Override
	public String set( int index, String element )
	{
		return super.set( index, relativizeFile( element ) );
	}

	@Override
	public boolean add( String e )
	{
		return super.add( relativizeFile( e ) );
	}

	@Override
	public void add( int index, String element )
	{
		super.add( index, relativizeFile( element ) );
	}

	@Override
	public boolean addAll( Collection< ? extends String > c )
	{
		return super.addAll( relativizeFiles( c ) );
	}

	@Override
	public boolean addAll( int index, Collection< ? extends String > c )
	{
		return super.addAll( index, relativizeFiles( c ) );
	}
	
	public RelativeFileList setBaseDir( String baseDirectory )
	{
		this.baseDirectory = baseDirectory;

		FileList absoluteFiles = getAbsoluteFileList();
		clear();
		addAll( absoluteFiles );
		
		return this;
	}

	public RelativeFileList addFiles( IOFileFilter fileFilter )
	{
		return (RelativeFileList)super.addFiles( baseDirectory, fileFilter );
	}

	public RelativeFileList addFiles( IOFileFilter fileFilter, IOFileFilter dirFilter )
	{
		return (RelativeFileList)super.addFiles( baseDirectory, fileFilter, dirFilter );
	}
	
	@Override
	public RelativeFileList addFiles( List< String > files )
	{
		return (RelativeFileList)super.addFiles( files );
	}

	@Override
	public RelativeFileList addFiles( String... files )
	{
		return (RelativeFileList)super.addFiles( files );
	}
	
	@Override
	public RelativeFileList addFiles( String baseDir, IOFileFilter fileFilter )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public FileList addFiles( String baseDir, IOFileFilter fileFilter, IOFileFilter dirFilter )
	{
		throw new UnsupportedOperationException();
	}
}
