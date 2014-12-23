package ch.liquidmind.deflector.reflection;

import java.lang.reflect.Type;


public abstract class Behavior
{
	public abstract Class< ? >[] getExceptionTypes();
	public abstract int getModifiers();
	public abstract String getName();
	public abstract Class< ? >[] getParameterTypes();
	public abstract Type[] getGenericParameterTypes();
	public abstract Type[] getGenericExceptionTypes();
	public abstract Class< ? > getDeclaringClass();
	@Override
	public abstract String toString();
	public abstract boolean isVarArgs();
}
