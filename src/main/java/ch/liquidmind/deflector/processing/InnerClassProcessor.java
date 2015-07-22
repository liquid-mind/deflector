package ch.liquidmind.deflector.processing;

public class InnerClassProcessor extends ClassProcessor
{
	public InnerClassProcessor( Class< ? > sourceClass, DeflectorWriter writer )
	{
		super( sourceClass, writer );
	}

	@Override
	protected boolean isTargetClassStatic()
	{
		return true;
	}

	@Override
	public void process()
	{
		createTargetClass();
	}
}
