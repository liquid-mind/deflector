package ch.liquidmind.deflector.io;

import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

public class ArchiveFileList extends RelativeFileList
{
	private static final long serialVersionUID = 1L;
	
	private String archivePrefix;
	
	public ArchiveFileList()
	{
		super();
	}
	
	public String getArchivePrefix()
	{
		return archivePrefix;
	}
	
	@Override
	public String toString()
	{
		return objectIdentifierToString() + baseDirectoryToString() + archivePrefixToString() + setToString();
	}
	
	protected String archivePrefixToString()
	{
		return archivePrefix + System.lineSeparator();
	}

	public ArchiveFileList setArchivePrefix( String archivePrefix )
	{
		this.archivePrefix = archivePrefix;
		
		return this;
	}
	
	@Override
	public ArchiveFileList setBaseDir( String baseDirectory )
	{
		return (ArchiveFileList)super.setBaseDir( baseDirectory );
	}

	@Override
	public ArchiveFileList addFiles( IOFileFilter fileFilter )
	{
		return (ArchiveFileList)super.addFiles( fileFilter );
	}

	@Override
	public ArchiveFileList addFiles( IOFileFilter fileFilter, IOFileFilter dirFilter )
	{
		return (ArchiveFileList)super.addFiles( fileFilter, dirFilter );
	}

	@Override
	public ArchiveFileList addFiles( List< String > files )
	{
		return (ArchiveFileList)super.addFiles( files );
	}

	@Override
	public ArchiveFileList addFiles( String... files )
	{
		return (ArchiveFileList)super.addFiles( files );
	}
}
