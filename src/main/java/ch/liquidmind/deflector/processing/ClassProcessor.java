package ch.liquidmind.deflector.processing;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.liquidmind.deflector.DeflectorConfig;
import ch.liquidmind.deflector.reflection.ConstructorBehavior;
import ch.liquidmind.deflector.reflection.MethodBehavior;

public abstract class ClassProcessor extends Processor
{
	private Class< ? > sourceClass;
	
	public ClassProcessor( Class< ? > sourceClass )
	{
		super( null );
		this.sourceClass = sourceClass;
	}

	public ClassProcessor( Class< ? > sourceClass, DeflectorWriter writer )
	{
		super( writer );
		this.sourceClass = sourceClass;
	}

	protected void createTargetClass()
	{
		writeClassDeclaration();
		
		getWriter().println( "{" );
		
		handleCheckedExceptionClass();
		handleBehaviors();
		handleInnerClasses();
		
		getWriter().println( "}" );
		getWriter().println();
	}
	
	private void handleCheckedExceptionClass()
	{
		if ( !isCheckedExceptionClass( getSourceClass() ) )
			return;
			
		getWriter().indent();
		
		getWriter().println( "private static final long serialVersionUID = 1L;" );
		getWriter().println();
		
		String sourceClass = typeToString( getSourceClass(), getShowTypeVariableReference(), false, true, false );
		
		getWriter().println( "public " + getTargetClassSimpleName() + "( " + sourceClass + " e )" );
		getWriter().println( "{" );
		
		getWriter().indent();
		
		getWriter().println( "super( e );" );
		
		getWriter().deindent();
		
		getWriter().println( "}" );
		
		getWriter().deindent();
	}
	
	private void handleBehaviors()
	{
		for ( Constructor< ? > constructor : getDeclaredConstructors() )
			handleBehavior( new ConstructorProcessor( new ConstructorBehavior( constructor ), getWriter() ) );

		for ( Method method : getDeclaredMethods() )
			handleBehavior( new MethodProcessor( new MethodBehavior( method ), getWriter() ) );
	}
	
	private List< Constructor< ? > > getDeclaredConstructors()
	{
		List< Constructor< ? > > declaredConstructors = new ArrayList< Constructor< ? > >();
		
		for ( Constructor< ? > constructor : sourceClass.getDeclaredConstructors() )
			if ( !constructor.isSynthetic() && !isAbstract( sourceClass.getModifiers() ) )
				declaredConstructors.add( constructor );
		
		return declaredConstructors;
	}
	
	private List< Method > getDeclaredMethods()
	{
		List< Method > declaredMethods = new ArrayList< Method >();
		
		for ( Method constructor : sourceClass.getDeclaredMethods() )
			if ( !constructor.isSynthetic() )
				declaredMethods.add( constructor );
		
		return declaredMethods;
	}
	
	private void handleBehavior( BehaviorProcessor processor )
	{
		getWriter().indent();
		
		processor.process();
		
		getWriter().deindent();
	}
	
	// TODO Introduce inheritance between deflector classes. This allows (static) super class methods
	// to be invoked on sub classes, e.g. org.eclipse.jetty.util.component.AbstractLifeCycle.start()
	// from org.eclipse.jetty.server.Server.start().
	private void writeClassDeclaration()
	{
		String staticModifer = ( isTargetClassStatic() ? "static " : "" );
		String extendsString = ( Throwable.class.isAssignableFrom( getSourceClass() ) ? " extends " + RuntimeException.class.getName() : "" );
		
		if ( DeflectorConfig.getJavaMajorVersion() >= 1 &&  DeflectorConfig.getJavaMinorVersion() >= 5 )
			getWriter().println( "@SuppressWarnings(\"deprecation\")" );
		
		getWriter().println( "public " + staticModifer + "final class " + getTargetClassSimpleName() + extendsString );
	}
	
	private String getTargetClassSimpleName()
	{
		return DeflectorConfig.getClassPrefix() + getSourceClass().getSimpleName();
	}
	
	protected boolean isTargetClassStatic()
	{
		return false;
	}
		
	private void handleInnerClasses()
	{
		Set< Class< ? > > innerClasses = getProcessableInnerClasses();
		
		for ( Class< ? > innerClass : innerClasses )
		{
			getWriter().indent();
			
			InnerClassProcessor innerClassProcessor = new InnerClassProcessor( innerClass, getWriter() );
			innerClassProcessor.process();

			getWriter().deindent();
		}
	}
	
	private Set< Class< ? > > getProcessableInnerClasses()
	{
		Set< Class< ? > > processableClasses = new HashSet< Class< ? > >();
		
		for ( Class< ? > declaredClass : sourceClass.getDeclaredClasses() )
			if ( isProcessableInnerClass( declaredClass ) )
				processableClasses.add( declaredClass );
		
		return processableClasses;
	}

	protected Class< ? > getSourceClass()
	{
		return sourceClass;
	}

	protected static boolean isProcessableRootClass( Class< ? > theClass )
	{
		boolean isProcessableClass = isPublic( theClass.getModifiers() );
		
		// If the class is an inner class --> not processable (non-local inner classes are
		// processed as part of non-inner classes).
		if ( theClass.getEnclosingClass() != null )
			isProcessableClass = false;
		
		return isProcessableClass;
	}
	
	protected static boolean isProcessableInnerClass( Class< ? > theClass )
	{
		return isPublic( theClass.getModifiers() );
	}
}
