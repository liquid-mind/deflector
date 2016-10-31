Deflector
=========

Java checked exception to runtime exception converter.

# Requirements

* JDK 8 (may work with earlier JDKs)
* Gradle (optional)

Note that the Java runtime environment alone is not enough, as Deflector requires access to the Java compiler.

# Setup

Open a terminal and go to the location where you wish to clone Deflector. Enter `git clone https://github.com/liquid-mind/deflector.git`:

	Cloning into 'deflector'...
	remote: Counting objects: 160, done.
	remote: Total 160 (delta 0), reused 0 (delta 0), pack-reused 160
	Receiving objects: 100% (160/160), 846.10 KiB | 183.00 KiB/s, done.
	Resolving deltas: 100% (51/51), done.
	Checking connectivity... done.

Then, build the project and install it with `gradlew installDist` (or, if you have gradle installed, you can use `gradle install` instead):

	:compileJava
	:processResources UP-TO-DATE
	:classes
	:jar
	:startScripts
	:installDist

You should now have deflector installed under `build/install/deflector`. Now, try invoking deflector from the project root directory with `./build/install/deflector/bin/deflector`; you should see some usage information:

	usage: deflector
	    --classpath <arg>            deflector classpath
	    --debug                      start in debug mode
	    --excludes <arg>             packages to exclude
	    --help                       show this help message
	    --includes <arg>             packages to include
	    --intermediateSrc <arg>      intermediate source output location
	    --intermediateTarget <arg>   intermediate target output location
	    --jar                        jar to deflect
	    --javaVersion <arg>          java version of source jar
	    --keepIntermediate <arg>     keep intermediate output (true|false)
	    --output <arg>               output location

Next, try deflecting the Java runtime libraries of your JRE/JDK using this command: `./build/install/deflector/bin/deflector --jar /Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/rt.jar --output build --includes java.* org.xml.* javax.* org.omg.*` (where `/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre` should be replaced by the root directory of your JRE):

	Generating deflector classes...
	Compiling deflector classes...
	Creating jar: /Users/john/Downloads/test2/deflector/build/__rt.jar
	Done.

Take a look at the contents of the generated jar with `jar tf build/__rt.jar`:

	__java/applet/__Applet.class
	__java/applet/__AppletContext.class
	__java/applet/__AppletStub.class
	__java/applet/__AudioClip.class
	__java/awt/__ActiveEvent.class
	__java/awt/__Adjustable.class
	__java/awt/__AlphaComposite.class
	__java/awt/__AWTError.class
	...

For every class that defines at least one method with a checked exception you should have a corresponding "deflected" class: the default naming convention is to add a double-underscore to the root package name and the simple name. You may now use `__rt.jar` in your projects to invoke standard Java libaries without needing to deal with checked exceptions. For example:

			try
			{
				ClassLoader.getSystemClassLoader().loadClass( "java.lang.String" );
			}
			catch ( ClassNotFoundException e )
			{
				// handle exception
			}

...becomes simply:

	__ClassLoader.loadClass( ClassLoader.getSystemClassLoader(), "java.lang.String" );
  
The following rules apply to deflected methods:

* Deflected methods are always static.
* Deflected methods have one additional argument of the owning class type (e.g., ClassLoader above).
* If the orignal method (e.g. ClassLoader.loadClass() ) is an *instance* method then you should pass the instance ( e.g., ClassLoader.getSystemClassLoader() ) as the first argument.
* If the orignal method is static, then you should pass *null* as the first argument.


