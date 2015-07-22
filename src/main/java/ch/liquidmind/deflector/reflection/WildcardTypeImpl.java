package ch.liquidmind.deflector.reflection;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import org.apache.commons.lang.StringUtils;

public class WildcardTypeImpl implements WildcardType
{
	private Type[] upperBounds;
	private Type[] lowerBounds;

	public WildcardTypeImpl( Type[] upperBounds, Type[] lowerBounds )
	{
		super();
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
	}

	@Override
	public Type[] getUpperBounds()
	{
		return upperBounds;
	}

	@Override
	public Type[] getLowerBounds()
	{
		return lowerBounds;
	}
	
	@Override
	public String toString()
	{
		String expression = null;
		
		if ( upperBounds != null )
			expression = "extends " + StringUtils.join( upperBounds, " & " );
		else if ( lowerBounds != null )
			expression = "super " + StringUtils.join( lowerBounds, " & " );
		else
			throw new IllegalStateException( "Unexpected conditions: neither upper nor lower bounds set." );

		return expression;
	}
}
