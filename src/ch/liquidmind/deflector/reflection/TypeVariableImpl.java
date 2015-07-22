package ch.liquidmind.deflector.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVariableImpl< T extends GenericDeclaration > implements TypeVariable< T >
{
	private Type[] bounds;
	private T genericDeclaration;
	private String name;

	public TypeVariableImpl( Type[] bounds, T genericDeclaration, String getName )
	{
		super();
		this.bounds = bounds;
		this.genericDeclaration = genericDeclaration;
		this.name = getName;
	}

	@Override
	public Type[] getBounds()
	{
		return bounds;
	}

	@Override
	public T getGenericDeclaration()
	{
		return genericDeclaration;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public < U extends Annotation > U getAnnotation( Class< U > annotationClass )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Annotation[] getAnnotations()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Annotation[] getDeclaredAnnotations()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public AnnotatedType[] getAnnotatedBounds()
	{
		throw new UnsupportedOperationException();
	}
}
