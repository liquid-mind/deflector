package ch.liquidmind.deflector.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ParameterizedTypeImpl implements ParameterizedType
{
	private Type rawType;
	private Type[] actualTypeArguments;

	public ParameterizedTypeImpl( Type rawType, List< Type > actualTypeArguments )
	{
		this( rawType, actualTypeArguments.toArray( new Type[ actualTypeArguments.size() ] ) );
	}

	public ParameterizedTypeImpl( Type rawType, Type[] actualTypeArguments )
	{
		super();
		this.rawType = rawType;
		this.actualTypeArguments = actualTypeArguments;
	}

	@Override
	public Type[] getActualTypeArguments()
	{
		return actualTypeArguments;
	}

	@Override
	public Type getRawType()
	{
		return rawType;
	}

	@Override
	public Type getOwnerType()
	{
		return null;
	}

	public static List< Type > convertToParameterizedTypesIfNecessary( List< Type > sourceTypes )
	{
		List< Type > targetTypes = new ArrayList< Type >();
		
		for ( Type sourceType : sourceTypes )
			targetTypes.add( convertToParameterizedTypeIfNecessary( sourceType ) );
		
		return targetTypes;
	}
	
	public static Type convertToParameterizedTypeIfNecessary( Type sourceType )
	{
		Type targetType = sourceType;
		
		if ( sourceType instanceof Class )
		{
			Class< ? > sourceClass = (Class< ? >)sourceType;
			
			TypeVariable< ? >[] typeParams = sourceClass.getTypeParameters();
			
			if ( typeParams.length > 0 )
				targetType = new ParameterizedTypeImpl( sourceClass, typeParams );
		}
		
		return targetType;
	}
	
	@Override
	public String toString()
	{
		String name = null;
		
		if ( rawType instanceof TypeVariable )
			name = ((TypeVariable< ? >)rawType).getName();
		else if ( rawType instanceof Class )
			name = ((Class< ? >)rawType).getName();
		else
			throw new IllegalStateException( "Unexpected type for rawType: " + rawType );
		
		return name + "<" + StringUtils.join( actualTypeArguments, "," ) + ">";
	}
}