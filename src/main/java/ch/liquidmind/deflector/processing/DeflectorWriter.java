package ch.liquidmind.deflector.processing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DeflectorWriter extends PrintWriter
{
	private int indentLevel;
	
	public DeflectorWriter( String fileName ) throws FileNotFoundException
	{
		super( fileName );
	}
	
	public void indent()
	{
		++indentLevel;
	}
	
	public void deindent()
	{
		--indentLevel;
	}

	@Override
	public void println( String x )
	{
		String indent = "";
		
		for ( int i = 0 ; i < indentLevel ; ++i )
			indent += "\t";
		
		super.println( indent + x );
	}
}
