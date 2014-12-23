package ch.liquidmind.deflector.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArrayTypeImpl implements GenericArrayType
{
	private Type genericComponentType;

	public GenericArrayTypeImpl( Type genericComponentType )
	{
		super();
		this.genericComponentType = genericComponentType;
	}

	@Override
	public Type getGenericComponentType()
	{
		return genericComponentType;
	}

	@Override
	public String toString()
	{
		return genericComponentType.toString() + "[]";
	}
}
