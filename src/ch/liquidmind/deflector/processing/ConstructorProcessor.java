package ch.liquidmind.deflector.processing;

import java.lang.reflect.Type;

import ch.liquidmind.deflector.reflection.ConstructorBehavior;

public class ConstructorProcessor extends BehaviorProcessor
{
	public ConstructorProcessor( ConstructorBehavior behavior, DeflectorWriter writer )
	{
		super( behavior, writer );
	}

	@Override
	protected String getName()
	{
		return "__new";
	}

	@Override
	protected ConstructorBehavior getBehavior()
	{
		return (ConstructorBehavior)super.getBehavior();
	}

	@Override
	protected Type getGenericReturnType()
	{
		return getBehavior().getDeclaringClass();
	}
}
