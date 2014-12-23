package ch.liquidmind.deflector.parsing;

import java.lang.reflect.TypeVariable;
import java.util.Set;

public class FindTypeVariablesTypeProcessor extends AbstractTypeProcessor
{
	public Set< TypeVariable< ? > > getResult()
	{
		return getTypeVariables();
	}
}
