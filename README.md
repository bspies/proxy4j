Introduction
============

Proxy4J is designed for creating proxies in Java with a high degree of flexibility, both in the implementation of the proxying itself as well as in the style of the proxy. Currently Proxy4J supports the following proxy styles:

+ _Virtual Proxies_. Also known as delegation proxies, these proxies simply pass the method invocation directly to the "real subject", which may have some indirection in how it is created or fetched (e.g. the classic lazy-loading proxy).
+ _Invocation Handlers_. These are a direct parallel to proxies in the JDK, with a slight twist to include type safety in the common case where only one interface is being proxied. Method calls on these proxies are passed to an invocation handler which decides how to handle the call.
+ _Interceptors_. These are proxies where one or more interceptors may get called prior to invoking the method on the "real subject". In Proxy4J interceptors are very granular and may be specified on a per-method basis.

Getting Started
===============

The centerpiece of the Proxy4J library is the [ProxyFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/ProxyFactory.java) interface for creating proxies. So the first step in creating a proxy is to create an instance of an implementation for this interface:

```java
    ProxyFactory proxyFactory = new CglibProxyFactory();
```

Of course, you may also use dependency injection to specify the desired implementation.

```java
    ProxyFactory proxyFactory = Injector.create(ProxyFactory.class);
```

Then, to create the desired proxy, simply invoke the desired createProxy() method on the [ProxyFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/ProxyFactory.java), or use the buildInterceptor() fluent API to create a custom method interceptor.

For example, creating a lazily-loaded proxy is as easy as creating a virtual proxy by passing in the appropriate [LazyProvider](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/init/LazyProvider.java):

```java
    Foo fooProxy = proxyFactory.createProxy(Foo.class, new LazyProvider<Foo>() {
      @Override protected Foo init() {
         return new FooImpl();
      }
    });
```

The LazyProvider also uses double-checked locking to provide thread safety.
 
Proxy Styles
============

Virtual Proxies
---------------

Virtual proxies in Proxy4J use the [javax.inject.Provider](https://docs.oracle.com/javaee/6/api/javax/inject/Provider.html) interface to provide a level of indirection in fetching or creating the "real subject" of the proxy. Use of this interface also has the added benefit of allowing the reuse of existing dependency injection bindings in Spring or Guice. As shown in the example above, the canonical use case for virtual proxies is lazy loading.

Invocation Handlers
-------------------

As stated above, invocation handlers in Proxy4J mirror the JDK proxy implementation. They provide the largest degree of freedom in deciding how to handle a method invocation, as well as the ability to have no "real subject" at all.

In most cases, you will only want to proxy a single class/interface so you can use the type-safe version:

```java
    Foo fooProxy = proxyFactory.createProxy(Foo.class, new FooProxyHandler());
``` 

...where FooProxyHandler is an implementation of the ProxyHandler interface:

```java
    public class FooProxyHandler implements ProxyHandler<Foo> {
      public Object handle(ProxyInvocation<Foo> invocation) throws Throwable {
        //...
      }
    }
```  

Interceptors
------------

Method interceptors in Proxy4J are both powerful and flexible, as they allow multiple interceptors to be defined on a method as well as the specification of different interceptors for different methods on a given class.

If we define the class Foo as follows,

```java
    public class AcceptDoMethodFilter implements MethodFilter {
        public boolean accept(Method method) {
          return method.getName().startsWith("do");
        }
    }
```

We can now intercept any method on Foo that starts with "do" by writing the following:

```java
    Foo fooProxy = proxyFactory.buildInterceptor(Foo.class)
                   .on(new FooImpl())
                   .using(new AcceptDoMethodFilter(), new MyInterceptor())
                   .create();
```
                   
Here the [MethodFilter](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/filter/MethodFilter.java) is defined as follows:

```java
    public class AcceptDoMethodFilter implements MethodFilter {
        public boolean accept(Method method) {
          return method.getName().startsWith("do");
        }
    } 
```
   
The second argument to using() here is a variable-length argument that can take multiple implementations of [MethodInterceptor](http://aopalliance.sourceforge.net/doc/org/aopalliance/intercept/MethodInterceptor.html). A variation of the using() method allows you to use a [InterceptorFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/InterceptorFactory.java) to generate the interceptors instead.

You can also use method annotations instead of the method filter to specify which methods should be proxied:

```java
    Foo fooProxy = proxyFactory.buildInterceptor(Foo.class)
                   .on(new FooImpl())
                   .using(MyAnnotation.class, new MyInterceptor())
                   .create();
```                   

Proxy Implementations
=====================

Proxy4J currently supports the following implementations:

+ [CGLIB](http://cglib.sourceforge.net/) is a byte-code manipulation library built on top of ASM. It is supported in Proxy4J through the [CglibProxyFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/cglib/CglibProxyFactory.java) implementation.
+ [Javassist](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/jdk/JdkProxyFactory.java) is a Java runtime compiler and bytecode manipulation library. It is supported in Proxy4J through the [JavassistProxyFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/javassist/JavassistProxyFactory.java) implementation.
+ The [JDK](http://www.oracle.com/technetwork/java/javase/documentation/). Though not as fast as byte code manipulation, the reflection-based JDK implementation of proxying has the advantage of no additional dependencies and compatibility with environments where byte code manipulation is either not desired or disallowed altogether. It is supported in Proxy4J through the [JdkProxyFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/jdk/JdkProxyFactory.java) implementation.

Custom Classloaders
===================

All implementations of ProxyFactory extend the abstract base class [BaseProxyFactory](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/BaseProxyFactory.java), which allows you to specify a preferred ClassLoader. This is specified as a constructor argument in each concrete implementation of ProxyFactory.

There is also a qualifying annotation, [@ProxyLoader](https://github.com/bspies/proxy4j/blob/master/core/src/main/java/org/proxy4j/core/ProxyLoader.java), that allows you to automatically inject this ClassLoader when using JSR-330 compliant dependency injection:

```java
    binder.bind(ClassLoader.class)
          .annotatedWith(ProxyLoader.class)
          .toInstance(myBridgeClassLoader);
```          

Dependencies
============

Proxy4J has a hard dependency on both the aopalliance and jsr-330 jars. Using other dependencies depends on which implementation of ProxyFactory you are using.

Your Maven pom.xml will look something like this:
```xml
    <dependencies>
      <dependency>
          <groupId>com.googlecode.proxy4j</groupId>
          <artifactId>proxy4j-${module}</artifactId>
          <version>${proxy4j.version}</version>
      </dependency>
      <!-- Only need if using CglibProxyFactory -->
      <dependency>
          <groupId>cglib</groupId>
          <artifactId>cglib</artifactId>
          <version>${cglib.version}</version>
          </dependency>
      <!-- Only need if using JavassistProxyFactory -->
      <dependency>
          <groupId>javassist</groupId>
          <artifactId>javassist</artifactId>
          <version>${javassist.version}</version>
      </dependency>
    </dependencies>
```  
Here _${cglib.version}_ and _${javassist.version}_ will generally be the most recent versions of those libraries.

Using With Maven
================

Proxy4J-Core is currently available from the [Sonatype OSS releases repository](https://oss.sonatype.org/content/repositories/releases/).

```xml
    <dependency>
      <groupId>com.googlecode.proxy4j</groupId>
      <artifactId>proxy4j-core</artifactId>
      <version>1.0.0</version>
    </dependency>
```