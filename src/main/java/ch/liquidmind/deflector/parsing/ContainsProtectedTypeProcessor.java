package ch.liquidmind.deflector.parsing;

import ch.liquidmind.deflector.processing.Processor;

public class ContainsProtectedTypeProcessor extends AbstractTypeProcessor
{
	private boolean containsProtectedType = false;

	@Override
	public void startClass( Class< ? > classType )
	{
		if ( Processor.isProtected( classType.getModifiers() ) )
		{
			containsProtectedType = true;
			
			// Actually, we could simply stop traversing at this point,
			// but I haven't built that ability into the traverser yet...
			getTypeTraverser().stopRecursion();
		}
	}

	public boolean getContainsProtectedType()
	{
		return containsProtectedType;
	}
}
