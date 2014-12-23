package ch.liquidmind.deflector.processing;

import java.lang.reflect.Type;

import ch.liquidmind.deflector.reflection.MethodBehavior;

public class MethodProcessor extends BehaviorProcessor
{
	public MethodProcessor( MethodBehavior behavior, DeflectorWriter writer )
	{
		super( behavior, writer );
	}

	@Override
	protected String getName()
	{
		return getBehavior().getName();
	}

	@Override
	protected MethodBehavior getBehavior()
	{
		return (MethodBehavior)super.getBehavior();
	}

	@Override
	protected Type getGenericReturnType()
	{
		return getBehavior().getGenericReturnType();
	}
}
