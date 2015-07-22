package ch.liquidmind.deflector.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodBehavior extends Behavior
{
	private Method method;

	public MethodBehavior( Method method )
	{
		super();
		this.method = method;
	}

	@Override
	public Class< ? >[] getExceptionTypes()
	{
		return method.getExceptionTypes();
	}

	@Override
	public int getModifiers()
	{
		return method.getModifiers();
	}

	@Override
	public String getName()
	{
		return method.getName();
	}

	@Override
	public Class< ? >[] getParameterTypes()
	{
		return method.getParameterTypes();
	}
	
	public Type getGenericReturnType()
	{
		return method.getGenericReturnType();
	}
	
	@Override
	public Class< ? > getDeclaringClass()
	{
		return method.getDeclaringClass();
	}

	@Override
	public Type[] getGenericParameterTypes()
	{
		return method.getGenericParameterTypes();
	}

	@Override
	public Type[] getGenericExceptionTypes()
	{
		return method.getGenericExceptionTypes();
	}

	@Override
	public String toString()
	{
		return method.toString();
	}

	@Override
	public boolean isVarArgs()
	{
		return method.isVarArgs();
	}
}
