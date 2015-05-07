package ch.liquidmind.deflector.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public class ConstructorBehavior extends Behavior
{
	private Constructor< ? > constructor;

	public ConstructorBehavior( Constructor< ? > constructor )
	{
		super();
		this.constructor = constructor;
	}

	@Override
	public Class< ? >[] getExceptionTypes()
	{
		return constructor.getExceptionTypes();
	}

	@Override
	public int getModifiers()
	{
		return constructor.getModifiers();
	}

	@Override
	public String getName()
	{
		return constructor.getName();
	}

	@Override
	public Class< ? >[] getParameterTypes()
	{
		return constructor.getParameterTypes();
	}

	@Override
	public Class< ? > getDeclaringClass()
	{
		return constructor.getDeclaringClass();
	}

	@Override
	public Type[] getGenericParameterTypes()
	{
		return constructor.getGenericParameterTypes();
	}

	@Override
	public Type[] getGenericExceptionTypes()
	{
		return constructor.getGenericExceptionTypes();
	}

	@Override
	public String toString()
	{
		return constructor.toString();
	}

	@Override
	public boolean isVarArgs()
	{
		return constructor.isVarArgs();
	}
}
