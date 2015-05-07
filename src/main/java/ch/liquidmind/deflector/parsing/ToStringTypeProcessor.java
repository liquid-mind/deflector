package ch.liquidmind.deflector.parsing;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ch.liquidmind.deflector.DeflectorConfig;

@SuppressWarnings( "unchecked" )
public class ToStringTypeProcessor extends AbstractTypeProcessor
{
	public enum TypeVariableOutputVariant
	{
		SHOW_TYPE_VARIABLE_DEFINITION,
		SHOW_TYPE_VARIABLE_REFERENCE,
		NO_OUTPUT
	}
	
	private static final String TYPES = "types";
	private static final String ACTUAL_ARGUMENTS = "actualArguments";
	private static final String TYPE_VARIABLE_BOUNDS = "typeVariableBounds";
	private static final String WILDCARD_TYPE_UPPER_BOUNDS = "wildcardTypeUpperBounds";
	private static final String WILDCARD_TYPE_LOWER_BOUNDS = "wildcardTypeLowerBounds";
	private static final String OUTPUT_EXTENDS_CLAUSE = "outputExtendsClause";
	
	private TypeVariableOutputVariant typeVariableOutputVariant;
	private boolean includeArgs;
	private boolean includeEnclosingClass;
	private String packagePrefix;
	private String classPrefix;
	private String result;
	
	public ToStringTypeProcessor( TypeVariableOutputVariant typeVariableOutputVariant, boolean includeArgs, boolean includeEnclosingClass, boolean usePrefixes )
	{
		super();
		this.typeVariableOutputVariant = typeVariableOutputVariant;
		this.includeArgs = includeArgs;
		this.includeEnclosingClass = includeEnclosingClass;
		
		packagePrefix = ( usePrefixes ? DeflectorConfig.getPackagePrefix() : "" );
		classPrefix = ( usePrefixes ? DeflectorConfig.getClassPrefix() : "" );
	}

	public String getResult()
	{
		return result;
	}
	
	@Override
	public void startTypes( List< Type > types )
	{
		setLocal( TYPES, new ArrayList< String >() );
		setParam( TYPES, getLocal( TYPES ) );
	}

	@Override
	public void endTypes( List< Type > types )
	{
		List< String > typesAsString = (List< String >)getLocal( TYPES );
		
		if ( includeArgs )
			for ( int i = 0 ; i < typesAsString.size() ; ++i )
				typesAsString.set( i, typesAsString.get( i ) + " arg" + i );
		
		result = StringUtils.join( typesAsString, ", " );
	}

	@Override
	public void endType( Type type )
	{
		List< String > typesAsString = getParam( TYPES );
		
		if ( typesAsString != null )
			typesAsString.add( (String)getReturn() );
		else
			setReturn( (String)getReturn() );
	}
	
	@Override
	public void endGenericArrayType( GenericArrayType genericArrayType )
	{
		setReturn( getReturn() + "[]" );
	}

	@Override
	public void endGenericComponentType( Type genericComponentType )
	{
		setReturn( getReturn() );
	}
	
	@Override
	public void endParameterizedType( ParameterizedType parameterizedType )
	{
		String rawType = getReturn();
		String actualTypeArguments = getReturn( ACTUAL_ARGUMENTS );
		String typeParameters = "";
		
		if ( actualTypeArguments != null )
			typeParameters = "< " + actualTypeArguments + " >";
		
		setReturn( rawType + typeParameters );
	}

	@Override
	public void endRawType( Type rawType )
	{
		setReturn( getReturn() );
	}
	
	@Override
	public void startActualTypeArguments( Type[] actualTypeArguments )
	{
		if ( typeVariableOutputVariant.equals( TypeVariableOutputVariant.NO_OUTPUT ) )
		{
			getTypeTraverser().stopRecursion();
			return;
		}
		
		setLocal( ACTUAL_ARGUMENTS, new ArrayList< String >() );
		setParam( ACTUAL_ARGUMENTS, getLocal( ACTUAL_ARGUMENTS ) );
	}

	@Override
	public void endActualTypeArguments( Type[] actualTypeArguments )
	{
		if ( typeVariableOutputVariant.equals( TypeVariableOutputVariant.NO_OUTPUT ) )
			return;
		
		setReturn( ACTUAL_ARGUMENTS, StringUtils.join( (List< String >)getLocal( ACTUAL_ARGUMENTS ), ", " ) );
	}

	@Override
	public void endActualTypeArgument( Type actualTypeArgument )
	{
		List< String > actualArguments = (List< String >)getParam( ACTUAL_ARGUMENTS );
		actualArguments.add( (String)getReturn() );
	}

	@Override public void startTypeVariable( TypeVariable< ? > typeVariable )
	{
		if ( typeVariableOutputVariant.equals( TypeVariableOutputVariant.NO_OUTPUT ) )
		{
			getTypeTraverser().stopRecursion();
			return;
		}
		
		setLocal( OUTPUT_EXTENDS_CLAUSE, !getTypeVariables().contains( typeVariable ) );
		super.startTypeVariable( typeVariable );
	}
	
	@Override
	public void endTypeVariable( TypeVariable< ? > typeVariable )
	{
		if ( typeVariableOutputVariant.equals( TypeVariableOutputVariant.NO_OUTPUT ) )
			return;
		
		String extendsClause = "";
		
		if ( getLocal( OUTPUT_EXTENDS_CLAUSE ) )
		{
			if ( typeVariableOutputVariant.equals( TypeVariableOutputVariant.SHOW_TYPE_VARIABLE_DEFINITION ) )
				extendsClause = " extends " + getReturn();
			else if ( typeVariableOutputVariant.equals( TypeVariableOutputVariant.SHOW_TYPE_VARIABLE_REFERENCE ) )
				extendsClause = "";
			else
				throw new IllegalStateException( "Unexpected value for typeVariableOutputVariant: " + typeVariableOutputVariant );
		}
		
		setReturn( typeVariable.getName() + extendsClause ); 
	}

	@Override
	public void startTypeVariableBounds( Type[] typeVariableBounds )
	{
		setLocal( TYPE_VARIABLE_BOUNDS, new ArrayList< String >() );
		setParam( TYPE_VARIABLE_BOUNDS, getLocal( TYPE_VARIABLE_BOUNDS ) );	
	}

	@Override
	public void endTypeVariableBounds( Type[] typeVariableBounds )
	{
		setReturn( StringUtils.join( (List< String >)getLocal( TYPE_VARIABLE_BOUNDS ), " & " ) );
	}

	@Override
	public void endTypeVariableBoundary( Type typeVariableBoundary )
	{
		List< String > typeVariableBounds = (List< String >)getParam( TYPE_VARIABLE_BOUNDS );
		typeVariableBounds.add( (String)getReturn() );
	}
	
	@Override
	public void endWildcardType( WildcardType wildcardType )
	{
		setReturn( "? " + getReturn() );
	}

	@Override
	public void startWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		setLocal( WILDCARD_TYPE_UPPER_BOUNDS, new ArrayList< String >() );
		setParam( WILDCARD_TYPE_UPPER_BOUNDS, getLocal( WILDCARD_TYPE_UPPER_BOUNDS ) );	
	}

	@Override
	public void endWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		setReturn( "extends " + StringUtils.join( (List< String >)getLocal( WILDCARD_TYPE_UPPER_BOUNDS ), " & " ) );
	}

	@Override
	public void endWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		List< String > wildcardTypeUpperBounds = (List< String >)getParam( WILDCARD_TYPE_UPPER_BOUNDS );
		wildcardTypeUpperBounds.add( (String)getReturn() );
	}

	@Override
	public void startWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		setLocal( WILDCARD_TYPE_LOWER_BOUNDS, new ArrayList< String >() );
		setParam( WILDCARD_TYPE_LOWER_BOUNDS, getLocal( WILDCARD_TYPE_LOWER_BOUNDS ) );	
	}

	@Override
	public void endWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		setReturn( "super " + StringUtils.join( (List< String >)getLocal( WILDCARD_TYPE_LOWER_BOUNDS ), " & " ) );
	}

	@Override
	public void endWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		List< String > wildcardTypeLowerBounds = (List< String >)getParam( WILDCARD_TYPE_LOWER_BOUNDS );
		wildcardTypeLowerBounds.add( (String)getReturn() );
	}

	@Override
	public void endClass( Class< ? > classType )
	{
		if ( classType.isArray() )
		{
			setReturn( getReturn() + "[]" );
		}
		else if ( classType.getEnclosingClass() != null )
		{
			String enclosingClass = ( includeEnclosingClass ? getReturn() + "." : "" );
			setReturn( enclosingClass + classPrefix + classType.getSimpleName() );
		}
		else
		{
			String thePackage = ( classType.getPackage() != null ? packagePrefix + classType.getPackage().getName() + "." : "");
			setReturn( thePackage + classPrefix + classType.getSimpleName() );
		}
	}

	@Override
	public void endEnclosingType( Type enclosingType )
	{
		setReturn( getReturn() );
	}
	
	@Override
	public void endComponentType( Type componentType )
	{
		setReturn( getReturn() );
	}
}
