package ch.liquidmind.deflector.parsing;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AbstractTypeProcessor implements TypeProcessor
{
	private CallStack callStack = new CallStack();
	private TypeTraverser typeTraverser;
	private Set< TypeVariable< ? > > typeVariables = new HashSet< TypeVariable< ? > >();
	
	private Frame getFrame()
	{
		return callStack.peek();
	}
	
	public < T > T getLocal( String name )
	{
		return getFrame().getLocal( name );
	}

	public < T > void setLocal( String name, T value )
	{
		getFrame().setLocal( name, value );
	}
	
	public < T > T getParam( String name )
	{
		return getFrame().getParam( name );
	}
	
	public < T > void setParam( String name, T value )
	{
		getFrame().setParam( name, value );
	}
	
	public < T > T getReturn( String name )
	{
		return getFrame().getReturn( name );
	}
	
	public < T > void setReturn( String name, T value )
	{
		getFrame().setReturn( name, value );
	}
	
	public < T > T getReturn()
	{
		return getFrame().getReturn();
	}
	
	public < T > void setReturn( T value)
	{
		getFrame().setReturn( value );
	}
	
	public Set< TypeVariable< ? > > getTypeVariables()
	{
		return typeVariables;
	}
	
	@Override
	public void setTypeTraverser( TypeTraverser typeTraverser )
	{
		this.typeTraverser = typeTraverser;
	}

	@Override
	public TypeTraverser getTypeTraverser()
	{
		return typeTraverser;
	}
	
	@Override
	public void push()
	{
		callStack.push();
	}
	
	@Override
	public void pop()
	{
		callStack.pop();
	}
	
	@Override public void startTypes( List< Type > types ) {}
	@Override public void endTypes( List< Type > types ) {}
	@Override public void startType( Type type ) {}
	@Override public void endType( Type type ) {}
	@Override public void startGenericArrayType( GenericArrayType genericArrayType ) {}
	@Override public void endGenericArrayType( GenericArrayType genericArrayType ) {}
	@Override public void startGenericComponentType( Type genericComponentType ) {}
	@Override public void endGenericComponentType( Type genericComponentType ) {}
	@Override public void startParameterizedType( ParameterizedType parameterizedType ) {}
	@Override public void endParameterizedType( ParameterizedType parameterizedType ) {}
	@Override public void startRawType( Type rawType ) {}
	@Override public void endRawType( Type rawType ) {}
	@Override public void startActualTypeArguments( Type[] actualTypeArguments ) {}
	@Override public void endActualTypeArguments( Type[] actualTypeArguments ) {}
	@Override public void startActualTypeArgument( Type actualTypeArgument ) {}
	@Override public void endActualTypeArgument( Type actualTypeArgument ) {}
	
	@Override public void startTypeVariable( TypeVariable< ? > typeVariable )
	{
		// Avoids infinite recursion in cases where the bounds of a type parameter are defined
		// in terms of the type parameter itself (e.g., K in java.util.EnumMap< K extends Enum< K >, V >)
		if ( typeVariables.contains( typeVariable ) )
			typeTraverser.stopRecursion();
		else
			typeVariables.add( typeVariable );
	}
	
	@Override public void endTypeVariable( TypeVariable< ? > typeVariable ) {}
	@Override public void startTypeVariableBounds( Type[] typeVariableBounds ) {}
	@Override public void endTypeVariableBounds( Type[] typeVariableBounds ) {}
	@Override public void startTypeVariableBoundary( Type typeVariableBoundary ) {}
	@Override public void endTypeVariableBoundary( Type typeVariableBoundary ) {}
	@Override public void startWildcardType( WildcardType wildcardType ) {}
	@Override public void endWildcardType( WildcardType wildcardType ) {}
	@Override public void startWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds ) {}
	@Override public void endWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds ) {}
	@Override public void startWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary ) {}
	@Override public void endWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary ) {}
	@Override public void startWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds ) {}
	@Override public void endWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds ) {}
	@Override public void startWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary ) {}
	@Override public void endWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary ) {}
	@Override public void startClass( Class< ? > classType ) {}
	@Override public void endClass( Class< ? > classType ) {}
	@Override public void startEnclosingType( Type enclosingType ) {}
	@Override public void endEnclosingType( Type enclosingType ) {}
	@Override public void startComponentType( Type componentType ) {}
	@Override public void endComponentType( Type componentType ) {}
}
