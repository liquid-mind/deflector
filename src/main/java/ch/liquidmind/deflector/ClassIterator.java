package ch.liquidmind.deflector;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassIterator implements Iterator< Class< ? > >
{
	private static final Logger logger = Logger.getLogger( ClassIterator.class.getName() );

	private static final String CLASS_SUFFIX = ".class";
	private static final int LOADED_CLASSES_MAX = 1000;
	
	private Set< String > includes;
	private Set< String > excludes;
	private Class< ? > nextClass;
	private DeflectorLoader loader;
	private int loadedClassesCount;
	private Enumeration< ZipEntry > jarEntries;
	private boolean hasNext;


	public ClassIterator()
	{
		super();
		this.includes = initializeSet( DeflectorConfig.getIncludes() );
		this.excludes = initializeSet( DeflectorConfig.getExcludes() );
		hasNext = true;
	}
	
	private Set< String > initializeSet( Set< String > set )
	{
		Set< String > initializedSet = set;
		
		if ( initializedSet == null )
			initializedSet = new HashSet< String >();
		
		return initializedSet;
	}

	@Override
	public boolean hasNext()
	{
		if ( nextClass == null )
			nextClass = getNextClass();

		if ( nextClass == null )
			hasNext = false;
		
		return hasNext;
	}

	@Override
	public Class< ? > next()
	{
		if ( nextClass == null )
			nextClass = getNextClass();
		
		if ( nextClass == null )
			throw new NoSuchElementException();
		
		Class< ? > theClass = nextClass;
		nextClass = null;
		
		return theClass;
	}
	
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private Class< ? > getNextClass()
	{
		Class< ? > theClass = null;
		Enumeration< ZipEntry > entries = getJarEntries();
		
		while ( entries.hasMoreElements() )
		{
			ZipEntry entry = entries.nextElement();
			String entryName = entry.getName();
			
			if ( entryName.endsWith( CLASS_SUFFIX ) )
			{
				String className = getClassName( entryName );
				
				if ( classNameMatches( className ) )
				{
					theClass = tryLoadClass( className );
					
					if ( theClass == null )
						logger.warning( "Unable to load class: " + className );
					else
						break;
				}
			}
		}
		
		return theClass;
	}
	
	private boolean classNameMatches( String className )
	{
		boolean classNameMatches = classNameMatches( includes, className, true );
		
		if ( classNameMatches( excludes, className, false ) )
			classNameMatches = false;
		
		return classNameMatches;
	}
	
	private boolean classNameMatches( Set< String > matchers, String className, boolean defaultMatch )
	{
		boolean classNameMatches = false;
		
		if ( matchers.isEmpty() )
		{
			classNameMatches = defaultMatch;
		}
		else
		{
			for ( String matcher : matchers )
			{
				if ( className.matches( matcher ) )
				{
					classNameMatches = true;
					break;
				}
			}
		}
		
		return classNameMatches;
	}
	
	private Class< ? > tryLoadClass( String name )
	{
		Class< ? > theClass = null;
		
		try
		{
			theClass = getLoader().loadClass( name );
		}
		catch ( ClassNotFoundException e )
		{
		}
		
		return theClass;
	}
	
	private static String getClassName( String fileName )
	{
		String className = fileName.replace( "/", "." ).replace( ".class", "" );
		return className;
	}

	@SuppressWarnings( "unchecked" )
	private Enumeration< ZipEntry > getJarEntries()
	{
		if ( jarEntries == null )
		{
			ZipFile zip = BootstrapWrapper.ZipFile_new( DeflectorConfig.getInputJarFileName() );
			jarEntries = (Enumeration< ZipEntry >)zip.entries();
		}
		
		return jarEntries;
	}
	
	private DeflectorLoader getLoader()
	{
		if ( loadedClassesCount++ > LOADED_CLASSES_MAX )
			loader = null;
		
		if ( loader == null )
			loader = new DeflectorLoader();
		
		return loader;
	}
}
