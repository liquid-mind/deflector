package ch.liquidmind.deflector.processing;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import ch.liquidmind.deflector.DeflectorConfig;
import ch.liquidmind.deflector.parsing.ToStringTypeProcessor;
import ch.liquidmind.deflector.parsing.TypeTraverser;
import ch.liquidmind.deflector.parsing.ToStringTypeProcessor.TypeVariableOutputVariant;

public abstract class Processor
{
	private DeflectorWriter writer;

	public Processor( DeflectorWriter writer )
	{
		super();
		this.writer = writer;
	}

	public DeflectorWriter getWriter()
	{
		return writer;
	}

	public void setWriter( DeflectorWriter writer )
	{
		this.writer = writer;
	}
	
	public abstract void process();
	
	public static boolean isPublic( int modifiers )
	{
		return ( ( modifiers & Modifier.PUBLIC ) ) == Modifier.PUBLIC;
	}
	
	public static boolean isProtected( int modifiers )
	{
		return ( modifiers & Modifier.PROTECTED ) == Modifier.PROTECTED;
	}
	
	public static boolean isStatic( int modifiers )
	{
		return ( ( modifiers & Modifier.STATIC ) ) == Modifier.STATIC;
	}
	
	public static boolean isAbstract( int modifiers )
	{
		return ( ( modifiers & Modifier.ABSTRACT ) ) == Modifier.ABSTRACT;
	}
	
	public static boolean isCheckedExceptionClass( Class< ? > theClass )
	{
		return Throwable.class.isAssignableFrom( theClass ) && !RuntimeException.class.isAssignableFrom( theClass ) && !Error.class.isAssignableFrom( theClass );
	}
	
	public static boolean isNonStaticInnerClass( Class< ? > theClass )
	{
		return ( ( theClass.getModifiers() & Modifier.STATIC ) != Modifier.STATIC ) && theClass.getEnclosingClass() != null;
	}

	public static String typeToString( Type type, TypeVariableOutputVariant variant, boolean includeArgs, boolean includeEnclosingClass, boolean usePrefixes )
	{
		return typesToString( Arrays.asList( new Type[] { type } ), variant, includeArgs, includeEnclosingClass, usePrefixes );
	}

	public static String typesToString( List< Type > types, TypeVariableOutputVariant variant, boolean includeArgs, boolean includeEnclosingClass, boolean usePrefixes, boolean hasVarArgs )
	{
		String typesAsString = typesToString( types, variant, includeArgs, includeEnclosingClass, usePrefixes );
		
		// Unfortunately, there is no way for ToStringTypeProcessor to known which node in the object graph is
		// currently being traversed; consequently, there is no way to know when the array representing the vararg
		// has been reached. As a work-around we just look for the last array occurrence and turn it into a vararg.
		// The cleanest approach would be for the traverser to better inform the processor in regards to the current
		// location in the object graph, but since I plan on solving this problem more generally at some point I'm not
		// going to bother here for now.
		if ( hasVarArgs )
		{
			int varArgsIndex = typesAsString.lastIndexOf( "[]" );
			typesAsString = typesAsString.substring( 0, varArgsIndex ) + "..." + typesAsString.substring( varArgsIndex + 2, typesAsString.length() );
		}
		
		return typesAsString;
	}
	
	public static String typesToString( List< Type > types, TypeVariableOutputVariant variant, boolean includeArgs, boolean includeEnclosingClass, boolean usePrefixes )
	{
		ToStringTypeProcessor processor = new ToStringTypeProcessor( variant, includeArgs, includeEnclosingClass, usePrefixes );
		TypeTraverser traverser = new TypeTraverser( processor );
		traverser.traverseTypesWithConversion( types );
		
		return processor.getResult();
	}
	
	private static TypeVariableOutputVariant getTypeVariableOutputVariant( TypeVariableOutputVariant variant )
	{
		TypeVariableOutputVariant defaultVariant = TypeVariableOutputVariant.NO_OUTPUT;
		
		if ( DeflectorConfig.isJavaVersionPost1dot5() )
			defaultVariant = variant;
		
		return defaultVariant;
	}
	
	public static TypeVariableOutputVariant getShowTypeVariableDefinition()
	{
		return getTypeVariableOutputVariant( TypeVariableOutputVariant.SHOW_TYPE_VARIABLE_DEFINITION );
	}
	
	public static TypeVariableOutputVariant getShowTypeVariableReference()
	{
		return getTypeVariableOutputVariant( TypeVariableOutputVariant.SHOW_TYPE_VARIABLE_REFERENCE );
	}
}
