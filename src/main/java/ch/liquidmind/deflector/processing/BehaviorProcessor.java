package ch.liquidmind.deflector.processing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import ch.liquidmind.deflector.parsing.ContainsProtectedTypeProcessor;
import ch.liquidmind.deflector.parsing.FindTypeVariablesTypeProcessor;
import ch.liquidmind.deflector.parsing.TypeTraverser;
import ch.liquidmind.deflector.parsing.ToStringTypeProcessor.TypeVariableOutputVariant;
import ch.liquidmind.deflector.reflection.Behavior;
import ch.liquidmind.deflector.reflection.ConstructorBehavior;

public abstract class BehaviorProcessor extends Processor
{
	private Behavior behavior;
	
	public BehaviorProcessor( Behavior behavior, DeflectorWriter writer )
	{
		super( writer );
		this.behavior = behavior;
	}

	@Override
	public void process()
	{
		if ( !isPublic( behavior.getModifiers() ) )
			return;
		
		if ( !handleBehaviorDeclaration() )
			return;
		
		handleBehaviorBody();
	}
	
	private boolean handleBehaviorDeclaration()
	{
		// If there are no checked exceptions --> skip behavior.
		if ( getCheckedExceptions().isEmpty() )
			return false;
		
		List< Type > parameterTypes = getParameterTypes();
		Type returnType = getGenericReturnType();
		List< Type > paramterAndReturnTypes = getParameterAndReturnTypes( parameterTypes, returnType );
		
		// Skip any behaviors that contain protected types.
		if ( containsProtectedType( paramterAndReturnTypes ) )
			return false;
		
		List< Type > typeParameters = new ArrayList< Type >( findTypeVariables( getParameterAndReturnTypes( parameterTypes, returnType ) ) );
		
		String paramTypesAsString = typesToString( parameterTypes, getShowTypeVariableReference(), true, true, false, behavior.isVarArgs() );
		String returnTypeAsString = typeToString( getGenericReturnType(), getShowTypeVariableReference(), false, true, false );
		String typeParametersAsString = typesToString( typeParameters, getShowTypeVariableDefinition(), false, true, false );
		
		paramTypesAsString = ( paramTypesAsString.isEmpty() ? "" : " " + paramTypesAsString + " " );
		typeParametersAsString = ( typeParametersAsString.isEmpty() ? "" : "< " + typeParametersAsString + " > " );
		
		String behaviorDeclaration = "public static final " + typeParametersAsString + returnTypeAsString + " " + getName() + "(" + paramTypesAsString + ")";

		getWriter().println( behaviorDeclaration );
		
		return true;
	}

	private boolean containsProtectedType( List< Type > types )
	{
		ContainsProtectedTypeProcessor processor = new ContainsProtectedTypeProcessor();
		TypeTraverser traverser = new TypeTraverser( processor );
		traverser.traverseTypesWithConversion( types );
		
		return processor.getContainsProtectedType();
	}
	
	private List< Type > getParameterAndReturnTypes( List< Type > parameterTypes, Type returnType )
	{
		List< Type > paramAndReturnTypes = new ArrayList< Type >();
		paramAndReturnTypes.addAll( parameterTypes );
		paramAndReturnTypes.add( getGenericReturnType() );
		
		return paramAndReturnTypes;
	}
	
	private List< Type > getParameterTypes()
	{
		List< Type > paramTypes = new ArrayList< Type >();
		
		if ( !(behavior instanceof ConstructorBehavior) )
			paramTypes.add( getBehavior().getDeclaringClass() );

		paramTypes.addAll( Arrays.asList( getBehavior().getGenericParameterTypes() ) );
		
		return paramTypes;
	}
	
	private List< Type > getCheckedExceptions()
	{
		List< Type > checkedExceptions = new ArrayList< Type >();
		
		for ( Type exceptionType : behavior.getExceptionTypes() )
		{
			if ( ClassProcessor.isCheckedExceptionClass( (Class< ? >)exceptionType ) )
				checkedExceptions.add( exceptionType );
		}
		
		return checkedExceptions;
	}
	
	private List< Type > orderClassesByHierarchy( List< Type > classes )
	{
		DirectedGraph< Type, DefaultEdge > classHierarchyGraph = new DefaultDirectedGraph< Type, DefaultEdge >( DefaultEdge.class );

		// Pass 1: add vertexes.
		for ( Type aClass : classes )
			classHierarchyGraph.addVertex( aClass );
		
		// Pass 2: add edges.
		for ( Type subClass : classes )
		{
			Type closestSuperClass = null;
			
			for ( Type potentialSuperClass : classes )
			{
				if ( !potentialSuperClass.equals( subClass ) && isSubclass( potentialSuperClass, subClass ) )
				{
					if ( closestSuperClass == null || !isSubclass( potentialSuperClass, closestSuperClass ) )
						closestSuperClass = potentialSuperClass;
				}
			}
			
			if ( closestSuperClass != null )
				classHierarchyGraph.addEdge( closestSuperClass, subClass );
		}
		
		TopologicalOrderIterator< Type, DefaultEdge > iter = new TopologicalOrderIterator< Type, DefaultEdge >( classHierarchyGraph );
		List< Type > orderedClasses = new ArrayList< Type >();

		while ( iter.hasNext() )
			orderedClasses.add( iter.next() );
		
		Collections.reverse( orderedClasses );

		return orderedClasses;
	}
	
	private boolean isSubclass( Type superclass, Type subclass )
	{
		Class< ? > superclassAsClass = (Class< ? >)superclass;
		Class< ? > subclassAsClass = (Class< ? >)subclass;
		
		return superclassAsClass.isAssignableFrom( subclassAsClass );
	}
	
	private void handleBehaviorBody()
	{
		getWriter().println( "{" );
		getWriter().indent();
		
		getWriter().println( "try" );
		getWriter().println( "{" );
		getWriter().indent();
		getWriter().println( getBehaviorInvocation() );
		getWriter().deindent();
		getWriter().println( "}" );
		
		for ( Type checkedException : orderClassesByHierarchy( getCheckedExceptions() ) )
		{
			String deflectedExceptionAsString = typeToString( checkedException, getShowTypeVariableReference(), false, true, false );
			String deflectingExceptionAsString = typeToString( checkedException, getShowTypeVariableReference(), false, true, true );
			
			getWriter().println( "catch ( " + deflectedExceptionAsString + " e )" );
			getWriter().println( "{" );
			getWriter().indent();
			getWriter().println( "throw new " + deflectingExceptionAsString + "( e );" );
			getWriter().deindent();
			getWriter().println( "}" );
		}
		
		getWriter().deindent();
		getWriter().println( "}" );
		getWriter().println();
	}
	
	private String getBehaviorInvocation()
	{
		List< String > args = new ArrayList< String >();
		
		for ( int i = 0 ; i < getParameterTypes().size() ; ++i )
			args.add( "arg" + i );

		String methodSpecifier = null;
		String behaviorName = behavior.getName();
		
		if ( isStatic( behavior.getModifiers() ) )
		{
			String declaringClassName = typeToString( behavior.getDeclaringClass(), TypeVariableOutputVariant.NO_OUTPUT, false, true, false );
			methodSpecifier = declaringClassName + "." + behaviorName;
			args.remove( 0 );
		}
		else
		{
			if ( behavior instanceof ConstructorBehavior )
			{
				if ( isNonStaticInnerClass( behavior.getDeclaringClass() ) )
				{
					String declaringClassNameSimple = typeToString( behavior.getDeclaringClass(), getShowTypeVariableReference(), false, false, false );
					methodSpecifier = args.remove( 0 ) + ".new " + declaringClassNameSimple;
				}
				else
				{
					String declaringClassName = typeToString( behavior.getDeclaringClass(), getShowTypeVariableReference(), false, true, false );
					methodSpecifier = "new " + declaringClassName;
				}
			}
			else
			{
				methodSpecifier = args.remove( 0 ) + "." + behaviorName;
			}
		}
		
		String argsAsString = StringUtils.join( args, ", " );
		argsAsString = ( argsAsString.isEmpty() ? "" : " " + argsAsString + " " );
		
		String methodInvocation = ( getGenericReturnType().equals( void.class ) ? "" : "return " ) + methodSpecifier + "(" + argsAsString + ");";
		
		return methodInvocation;
	}
	
	@SuppressWarnings( "unchecked" )
	private Set< Type > findTypeVariables( List< Type > types )
	{
		FindTypeVariablesTypeProcessor processor = new FindTypeVariablesTypeProcessor();
		TypeTraverser traverser = new TypeTraverser( processor );
		traverser.traverseTypesWithConversion( types );
		
		return (Set< Type >)(Object)processor.getResult();
	}
	
	protected abstract String getName();
	protected abstract Type getGenericReturnType();
	
	protected Behavior getBehavior()
	{
		return behavior;
	}
}
