package ch.liquidmind.deflector;

import java.net.URLClassLoader;

public class DeflectorLoader extends URLClassLoader
{
	public DeflectorLoader()
	{
		super( DeflectorConfig.getFullClasspath() );
	}
}
