package ch.liquidmind.deflector.parsing;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

import ch.liquidmind.deflector.processing.Processor;
import ch.liquidmind.deflector.reflection.ParameterizedTypeImpl;

public class TypeTraverser
{
	private TypeProcessor processor;
	private boolean stopRecursion;
	private boolean staticClassFlag = false;
	
	public TypeTraverser( TypeProcessor processor )
	{
		super();
		this.processor = processor;
		
		processor.setTypeTraverser( this );
		
		stopRecursion = false;
	}
	
	public void stopRecursion()
	{
		this.stopRecursion = true;
	}
	
	private boolean continueRecursion()
	{
		boolean continueRecursion = !stopRecursion;
		stopRecursion = false;
		
		return continueRecursion;
	}
	
	public void traverseTypesWithConversion( List< Type > types )
	{
		traverseTypes( ParameterizedTypeImpl.convertToParameterizedTypesIfNecessary( types ) );
	}

	public void traverseTypeWithConversion( Type type )
	{
		traverseType( ParameterizedTypeImpl.convertToParameterizedTypeIfNecessary( type ) );
	}

	public void traverseTypes( List< Type > types )
	{
		processor.push();
		processor.startTypes( types );
		
		if ( continueRecursion() )
			for ( Type type : types )
				traverseType( type );

		processor.endTypes( types );
		processor.pop();
	}
	
	public void traverseType( Type type )
	{
		processor.push();
		processor.startType( type );
		
		if ( continueRecursion() )
		{
			if ( type instanceof GenericArrayType )
				traverseGenericArrayType( (GenericArrayType)type );
			else if ( type instanceof ParameterizedType )
				traverseParameterizedType( (ParameterizedType)type );
			else if ( type instanceof TypeVariable )
				traverseTypeVariable( (TypeVariable< ? >)type );
			else if ( type instanceof WildcardType )
				traverseWildcardType( (WildcardType)type );
			else if ( type instanceof Class )
				traverseClass( (Class< ? >)type );
			else
				throw new IllegalStateException( "Unexpected type for type: " + type );
		}
		
		processor.endType( type );
		processor.pop();
	}
	
	public void traverseGenericArrayType( GenericArrayType genericArrayType )
	{
		processor.push();
		processor.startGenericArrayType( genericArrayType );

		if ( continueRecursion() )
		{
			Type genericComponentType = genericArrayType.getGenericComponentType();
			
			if ( genericComponentType != null )
				traverseGenericComponentType( genericComponentType );
			
			traverseType( genericArrayType.getGenericComponentType() );
		}
		
		processor.endGenericArrayType( genericArrayType );
		processor.pop();
	}

	public void traverseGenericComponentType( Type genericComponentType )
	{
		processor.push();
		processor.startGenericComponentType( genericComponentType );
		
		if ( continueRecursion() )
			traverseType( genericComponentType );
		
		processor.endGenericComponentType( genericComponentType );
		processor.pop();
	}
	
	public void traverseParameterizedType( ParameterizedType parameterizedType )
	{
		processor.push();
		processor.startParameterizedType( parameterizedType );
		
		if ( continueRecursion() )
		{
			traverseRawType( parameterizedType.getRawType() );

			// Note that type parameters of enclosing classes should not be traversed 
			// for static inner classes.
			if ( !staticClassFlag )
			{
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				
				if ( actualTypeArguments.length > 0 )
					traverseActualTypeArguments( actualTypeArguments );
			}
		}
		
		processor.endParameterizedType( parameterizedType );
		processor.pop();
	}
	
	public void traverseRawType( Type rawType )
	{
		processor.push();
		processor.startRawType( rawType );
		
		traverseType( rawType );
		
		processor.endRawType( rawType );
		processor.pop();
	}

	public void traverseActualTypeArguments( Type[] actualTypeArguments )
	{
		processor.push();
		processor.startActualTypeArguments( actualTypeArguments );
		
		if ( continueRecursion() )
			for ( Type actualTypeArgument : actualTypeArguments )
				traverseActualTypeArgument( actualTypeArgument );
		
		processor.endActualTypeArguments( actualTypeArguments );
		processor.pop();
	}
	
	public void traverseActualTypeArgument( Type actualTypeArgument )
	{
		processor.push();
		processor.startActualTypeArgument( actualTypeArgument );
		
		if ( continueRecursion() )
			traverseType( actualTypeArgument );
		
		processor.endActualTypeArgument( actualTypeArgument );
		processor.pop();
	}
	
	public void traverseTypeVariable( TypeVariable< ? > typeVariable )
	{
		processor.push();
		processor.startTypeVariable( typeVariable );

		if ( continueRecursion() )
		{
			Type[] typeVariableBounds = typeVariable.getBounds();
			
			if ( typeVariableBounds.length > 0 )
				traverseTypeVariableBounds( typeVariable.getBounds() );
		}
				
		processor.endTypeVariable( typeVariable );
		processor.pop();
	}
	
	public void traverseTypeVariableBounds( Type[] typeVariableBounds )
	{
		processor.push();
		processor.startTypeVariableBounds( typeVariableBounds );
		
		if ( continueRecursion() )
			for ( Type typeVariableBoundary : typeVariableBounds )
				traverseTypeVariableBoundary( typeVariableBoundary );
		
		processor.endTypeVariableBounds( typeVariableBounds );
		processor.pop();
	}
	
	public void traverseTypeVariableBoundary( Type typeVariableBoundary )
	{
		processor.push();
		processor.startTypeVariableBoundary( typeVariableBoundary );

		if ( continueRecursion() )
			traverseType( typeVariableBoundary );
		
		processor.endTypeVariableBoundary( typeVariableBoundary );
		processor.pop();
	}
	
	public void traverseWildcardType( WildcardType wildcardType )
	{
		processor.push();
		processor.startWildcardType( wildcardType );

		if ( continueRecursion() )
		{
			Type[] wildcardTypeUpperBounds = wildcardType.getUpperBounds();
			Type[] wildcardTypeLowerBounds = wildcardType.getLowerBounds();
			
			if ( wildcardTypeUpperBounds.length > 0 )
				traverseWildcardTypeUpperBounds( wildcardTypeUpperBounds );
			
			if ( wildcardTypeLowerBounds.length > 0 )
				traverseWildcardTypeLowerBounds( wildcardTypeLowerBounds );
			
			if ( wildcardTypeUpperBounds.length == 0 && wildcardTypeLowerBounds.length == 0 )
				throw new IllegalStateException( "Upper and lower bounds are both empty." );
		}

		processor.endWildcardType( wildcardType );
		processor.pop();
	}

	public void traverseWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		processor.push();
		processor.startWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		
		if ( continueRecursion() )
			for ( Type wildcardTypeUpperBoundary : wildcardTypeUpperBounds )
				traverseWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
		
		processor.endWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		processor.pop();
	}
	
	public void traverseWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		processor.push();
		processor.startWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );

		if ( continueRecursion() )
			traverseType( wildcardTypeUpperBoundary );
		
		processor.endWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
		processor.pop();
	}

	public void traverseWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		processor.push();
		processor.startWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		
		if ( continueRecursion() )
			for ( Type wildcardTypeLowerBoundary : wildcardTypeLowerBounds )
				traverseWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
		
		processor.endWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		processor.pop();
	}
	
	public void traverseWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		processor.push();
		processor.startWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
		
		if ( continueRecursion() )
			traverseType( wildcardTypeLowerBoundary );
		
		processor.endWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
		processor.pop();
	}
	
	public void traverseClass( Class< ? > classType )
	{
		processor.push();
		processor.startClass( classType );
		
		if ( continueRecursion() )
		{
			boolean staticClassFlagTemp = staticClassFlag;
			
			if ( Processor.isStatic( classType.getModifiers() ) )
				staticClassFlag = true;
			
			Type enclosingType = ParameterizedTypeImpl.convertToParameterizedTypeIfNecessary( classType.getEnclosingClass() );
			
			if ( enclosingType != null )
				traverseEnclosingType( enclosingType );
			
			Type componentType = ParameterizedTypeImpl.convertToParameterizedTypeIfNecessary( classType.getComponentType() );
			
			if ( componentType != null )
				traverseComponentType( componentType );
			
				staticClassFlag = staticClassFlagTemp;
		}
		
		processor.endClass( classType );
		processor.pop();
	}

	public void traverseEnclosingType( Type enclosingType )
	{
		processor.push();
		processor.startEnclosingType( enclosingType );
		
		if ( continueRecursion() )
			traverseType( enclosingType );
		
		processor.endEnclosingType( enclosingType );
		processor.pop();
	}

	public void traverseComponentType( Type componentType )
	{
		processor.push();
		processor.startComponentType( componentType );
		
		if ( continueRecursion() )
			traverseType( componentType );
		
		processor.endComponentType( componentType );
		processor.pop();
	}
}
