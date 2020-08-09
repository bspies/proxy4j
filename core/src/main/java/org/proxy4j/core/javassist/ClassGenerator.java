package org.proxy4j.core.javassist;

import javassist.*;
import org.aopalliance.intercept.MethodInvocation;
import org.proxy4j.core.InterceptorChain;
import org.proxy4j.core.ProxyHandler;
import org.proxy4j.core.ProxyInvocation;
import org.proxy4j.core.reflect.BasicMethodExtractor;
import org.proxy4j.core.reflect.MethodExtractor;
import org.proxy4j.core.reflect.MultitypeMethodExtractor;
import org.proxy4j.core.util.*;

import javax.inject.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

/**
 * Generates proxy classes using Javassist.
 * @author Brennan Spies
 * @since 1.0.0
 */
class ClassGenerator
{
    private final NamingPolicy namingPolicy;
    private final ClassCache cache = new ClassCache();
    Properties templates = new Properties();
    private final Map<ClassLoader, ClassPool> classPoolMap =
            new WeakHashMap<>();

    /**
     * Creates a {@code ClassGenerator} with the given naming policy.
     * @param namingPolicy The naming policy for the proxy class
     */
    ClassGenerator(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
        try {
            templates.load(this.getClass().getResourceAsStream("/template.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Javassist template properties", e);
        }
    }

    /**
     * Creates a {@code Provider}-based proxy class.
     * @param loader The loader of the proxy class
     * @param proxyType The proxy type
     * @return The Provider-based proxy
     * @throws NotFoundException If an error occurs loading a class
     * @throws CannotCompileException If an error occurs during code generation
     */
    @SuppressWarnings("unchecked")
    <T> Class<T> getProviderProxyClass(ClassLoader loader, Class<T> proxyType) throws NotFoundException, CannotCompileException
    {
        Key key = new ClassHashKey(loader, Provider.class, proxyType);
        //check cache for class
        Class<T> cl;
        if((cl=cache.getClass(key))!=null) {
            return cl;
        }
        //else create new class
        ClassPool pool = getClassPool(loader);
        CtClass newCtClass = getSkeleton(pool, namingPolicy.getProxyName(proxyType.getName(), key), proxyType);
        CtClass providerCtClass = fetch(pool, Provider.class);
        addField(newCtClass, "provider", providerCtClass);
        addConstructor(newCtClass, "{this.provider = $1;}", providerCtClass);
        
        MethodExtractor extractor = new BasicMethodExtractor(proxyType);
        for(Method m : extractor.getProxyableMethods()) {
            String body = getDelegateBody("((" + proxyType.getName() + ")provider.get())", m);
            addMethod(pool, newCtClass, m, body);
        }
        cl = (Class<T>) newCtClass.toClass(loader, null);
        cache.cache(key, cl);
        return cl;
    }

    /**
     * Creates a {@code ProxyHandler}-based proxy class.
     * @param loader The loader of the proxy class
     * @param proxyType The proxy type
     * @return The proxy class
     * @throws NotFoundException If a class is not found
     * @throws CannotCompileException If the generated code does not compile
     */
    @SuppressWarnings("unchecked")
    <T> Class<T> getHandlerProxyClass(ClassLoader loader, Class<T> proxyType) throws NotFoundException, CannotCompileException
    {
        Key key = new ClassHashKey(loader, ProxyHandler.class, proxyType);
        //check cache for class
        Class<T> cl;
        if((cl=cache.getClass(key))!=null) {
            return cl;
        }
        //else create new class
        ClassPool pool = getClassPool(loader);
        MethodExtractor extractor = new BasicMethodExtractor(proxyType);
        Collection<Method> proxyableMethods = extractor.getProxyableMethods();
        CtClass newCtClass = getSkeleton(pool, namingPolicy.getProxyName(proxyType.getName(), key), proxyType);
        //add static field for methods and initializer method
        createMethod(pool, newCtClass,
                new MethodSignature(Method[].class, "getProxyableMethods"), //TODO convert to WeakArrayList
                new StringBuilder("return ($r) new ")
                    .append(extractor.getClass().getName())
                    .append("(")
                    .append(proxyType.getName())
                    .append(".class")
                    .append(").getProxyableMethods().toArray(new java.lang.reflect.Method[")
                    .append(proxyableMethods.size())
                    .append("]);")
                    .toString(),
                Modifier.PRIVATE | Modifier.STATIC);
        addStaticField(newCtClass, "methods", fetch(pool, Method[].class),
          CtField.Initializer.byCall(newCtClass, "getProxyableMethods"));

        createHandlers(pool, extractor, newCtClass);
        cl = (Class<T>) newCtClass.toClass(loader, null);
        cache.cache(key, cl);
        return cl;
    }

    /**
     * Creates a {@code ProxyHandler}-based proxy class that implements multiple
     * interfaces.
     * @param loader The loader for the proxy class
     * @param proxyInterfaces The interfaces to be proxied
     * @return The proxy class
     * @throws NotFoundException If a class is not found
     * @throws CannotCompileException If an error occurs compiling the generated code
     */
    Class<?> getHandlerProxyClass(ClassLoader loader, Class<?>[] proxyInterfaces) throws NotFoundException, CannotCompileException
    {
        Key key = new ClassHashKey(loader, proxyInterfaces);
        //check cache for class
        Class<?> cl;
        if((cl=cache.getClass(key))!=null) {
            return cl;
        }
        //else create new class
        ClassPool pool = getClassPool(loader);
        CtClass newCtClass = getSkeleton(pool,
                namingPolicy.getProxyName("", key),    //TODO base name?
                Object.class, proxyInterfaces);
        MethodExtractor extractor = new MultitypeMethodExtractor(proxyInterfaces);
        Collection<Method> proxyableMethods = extractor.getProxyableMethods();
        //add static field for methods and initializer method
        createMethod(pool, newCtClass,
                new MethodSignature(Method[].class, "getProxyableMethods"),    //TODO convert to WeakArrayList
                new StringBuilder("return ($r) new ")
                    .append(extractor.getClass().getName())
                    .append("(new Class[]{")
                    .append(classListAsString(proxyInterfaces))
                    .append("}).getProxyableMethods().toArray(new java.lang.reflect.Method[")
                    .append(proxyableMethods.size())
                    .append("]);")
                    .toString(),
                Modifier.PRIVATE | Modifier.STATIC);
        addStaticField(newCtClass, "methods", fetch(pool, Method[].class),
          CtField.Initializer.byCall(newCtClass, "getProxyableMethods"));
        createHandlers(pool, extractor, newCtClass);
        cl = newCtClass.toClass(loader, null);
        cache.cache(key, cl);
        return cl;
    }

    //creates handler field and methods
    private void createHandlers(ClassPool pool, MethodExtractor extractor, CtClass newCtClass) throws CannotCompileException, NotFoundException {
        CtClass invocationHandlerClass = fetch(pool, ProxyHandler.class);
        addField(newCtClass, "handler", invocationHandlerClass);
        addConstructor(newCtClass, "{this.handler = $1;}", invocationHandlerClass);
        Iterator<Method> methodIter = extractor.getProxyableMethods().iterator();
        for(int i=0; methodIter.hasNext(); i++) {
          Method m = methodIter.next();
          StringBuilder body = new StringBuilder(ProxyInvocation.class.getName())
            .append(" i = new ")
            .append(JavassistProxyInvocation.class.getName())
            .append("(this, methods[").append(i)
            .append("], $args);")
            .append("return ($r) handler.handle(i);");
          addMethod(pool, newCtClass, m, wrapBody(body));
        }
    }

    /**
     * Creates an interceptor-based proxy class.
     * @param loader The loader for the proxy class
     * @param target The target of the interceptor(s)
     * @param methodMap A map of methods to interceptor chains
     * @return The proxy class
     * @throws NotFoundException If a class cannot be loaded
     * @throws CannotCompileException If the generated code does not compile
     */
    @SuppressWarnings("unchecked")
    <T> Class<T> getInterceptorProxyClass(ClassLoader loader, T target, Map<Method,InterceptorChain> methodMap) throws NotFoundException, CannotCompileException
    {
        Key key = new MethodHashKey(loader, methodMap.keySet());
        //check cache for class
        Class<T> cl;
        if((cl=cache.getClass(key))!=null) {
            return cl;
        }
        //else create proxy class
        String className = namingPolicy.getProxyName(target.getClass().getName(), key);
        ClassPool pool = getClassPool(loader);
        CtClass newCtClass = getSkeleton(pool, className, target.getClass());
        CtClass targetCtClass = fetch(pool, target.getClass());
        CtClass chainArrayCtClass = fetch(pool, InterceptorChain[].class);
        addField(newCtClass, "target", targetCtClass);
        addField(newCtClass, "chains", chainArrayCtClass);
        addConstructor(newCtClass,
                "{this.target = $1; this.chains = $2;}",
                targetCtClass, chainArrayCtClass);
        //add the method interceptors
        MethodExtractor extractor = new BasicMethodExtractor(target.getClass());
        //add delegate calls for non-proxied methods
        for(Method m : extractor.getProxyableMethods()) {
           if(!methodMap.containsKey(m)) {
             addMethod(pool, newCtClass, m, getDelegateBody("target", m));
           }
        }
        //add interceptors
        Method[] proxiedMethods = methodMap.keySet().toArray(new Method[methodMap.size()]);
        for(int i=0; i<proxiedMethods.length; i++) {
           CtClass invocationClass = getMethodInvocationClass(pool, target.getClass(), className, i, proxiedMethods[i]);
           invocationClass.toClass(loader, null);
           String body = new StringBuilder(MethodInvocation.class.getName())
                .append(" i = new ")
                .append(invocationClass.getName())
                .append("(target,\"")
                .append(proxiedMethods[i].getName()).append("\", $args);")
                .append("return ($r) chains[").append(i).append("].invoke(i);")
                .toString();
           addMethod(pool, newCtClass, proxiedMethods[i], wrapBody(body));
        }
        cl = (Class<T>) newCtClass.toClass(loader, null);
        cache.cache(key, cl);
        return cl;
    }

    /**
     * Creates a skeletal {@code CtClass}.
     * @param pool The class pool to use
     * @param name The name of the created class (fully qualified)
     * @param superClass The super class of the created class
     * @param interfaces Any additional interfaces the created class should implement
     * @return The new class skeleton
     * @throws CannotCompileException If the generated code cannot compile
     * @throws NotFoundException If a class is not found
     */
    CtClass getSkeleton(ClassPool pool, String name, Class<?> superClass, Class<?>... interfaces) throws CannotCompileException, NotFoundException {
        CtClass ctClass = pool.makeClass(name);
        if(!superClass.isInterface()) {
            ctClass.setSuperclass(fetch(pool, superClass));
        } else {
            ctClass.addInterface(fetch(pool, superClass));
        }
        //add interfaces
        for (Class<?> iface : interfaces) {
            ctClass.addInterface(fetch(pool, iface));
        }
        return ctClass;
    }

    CtClass fetch(ClassPool pool, Class<?> type) throws NotFoundException {
        return pool.get(type.getName());
    }

    CtClass[] fetch(ClassPool pool, Class<?>[] types) throws NotFoundException {
        CtClass[] resolvedClasses = new CtClass[types.length];
        for(int i=0; i<types.length; i++) {
           resolvedClasses[i] = pool.get(types[i].getName());
        }
        return resolvedClasses;
    }

    //Adds a constructor with the given body and arguments
    private void addConstructor(CtClass owner, String body, CtClass... args) throws CannotCompileException {
       CtConstructor proxyConstructor = new CtConstructor(args, owner);
       owner.addConstructor(proxyConstructor);
       proxyConstructor.setBody(body);
    }

    //add a class field
    private void addField(CtClass owner, String name, CtClass fieldType) throws CannotCompileException {
        CtField field = new CtField(fieldType, name, owner);
        field.setModifiers(Modifier.PRIVATE);
        owner.addField(field);
    }

    //add a static class field
    private void addStaticField(CtClass owner, String name, CtClass fieldType, CtField.Initializer init) throws CannotCompileException {
        CtField field = new CtField(fieldType, name, owner);
        field.setModifiers(Modifier.PRIVATE | Modifier.STATIC);
        owner.addField(field, init);
    }

    //creates a method from an existing method
    private void addMethod(ClassPool pool, CtClass owningClass, Method m, String body) throws CannotCompileException, NotFoundException {
        CtMethod method = CtNewMethod.make(
           fetch(pool, m.getReturnType()),
           m.getName(), fetch(pool, m.getParameterTypes()),
           fetch(pool, m.getExceptionTypes()),
           body, owningClass);
        method.setModifiers(m.getModifiers() & ~Modifier.ABSTRACT);
        owningClass.addMethod(method);
    }

    //creates a new method
    private void createMethod(ClassPool pool, CtClass owningClass, MethodSignature signature, String body, int modifiers) throws NotFoundException, CannotCompileException {
       CtMethod method = CtNewMethod.make(
         fetch(pool, signature.getReturnType()),
         signature.getName(),
         fetch(pool, signature.getParameterTypes()),
         fetch(pool, signature.getExceptionTypes()),
         body, owningClass);
       method.setModifiers(modifiers);
       owningClass.addMethod(method);
    }

    //creates a method body that delegates to another object
    private String getDelegateBody(String delegateExpr, Method m) {
        String template = m.getReturnType().equals(void.class) ?
                templates.getProperty("delegate.method.body.noreturn") :
                templates.getProperty("delegate.method.body.return");
        return wrapBody(MessageFormat.format(template, delegateExpr,
                m.getName(), "$$"));
    }

    //Generates a subtype of JavassistMethodInvocation
    private CtClass getMethodInvocationClass(ClassPool pool, Class<?> targetClass, String proxyClassName, int index, Method m)
            throws NotFoundException, CannotCompileException {

        CtClass invocationClass = getSkeleton(pool, proxyClassName + "$$" + index, JavassistMethodInvocation.class);
        Method proceedMethod;
        try {
            proceedMethod = JavassistMethodInvocation.class.getMethod("proceed");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to get proceed() method", e);
        }
        String template = (m.getReturnType().equals(void.class)) ? templates.getProperty("interceptor.proceed.body.noreturn"):
                          (m.getReturnType().isPrimitive()) ? templates.getProperty("interceptor.proceed.body.return.boxed") :
                            templates.getProperty("interceptor.proceed.body.return");
        addConstructor(invocationClass, "{ super($$); }",
                fetch(pool, Object.class),
                fetch(pool, String.class),
                fetch(pool, Object[].class));
        String body;
        if(!m.getReturnType().equals(void.class) && m.getReturnType().isPrimitive()) {
           body = MessageFormat.format(template, getBoxedType(m.getReturnType()), "((" + targetClass.getName() + ") getThis())", m.getName(), "$$");
        } else {
           body = MessageFormat.format(template, "((" + targetClass.getName() + ") getThis())", m.getName(), "$$");
        }
        addMethod(pool, invocationClass, proceedMethod, wrapBody(body));
        return invocationClass;
    }

    private String getBoxedType(Class<?> primitiveType) {
        if(primitiveType.equals(int.class)) {
            return "Integer";
        } else if(primitiveType.equals(long.class)) {
            return "Long";
        } else if(primitiveType.equals(float.class)) {
            return "Float";
        } else if(primitiveType.equals(double.class)) {
            return "Double";
        } else {
            return "Character";
        }
    }

    //wraps a method body with the required braces
    private String wrapBody(CharSequence body) {
       return new StringBuilder("{").append(body).append('}').toString();
    }

    //flattens list of classes into String
    private String classListAsString(Class<?>... classes) {
       StringBuilder sb = new StringBuilder();
       for(int i=0; i<classes.length; i++) {
           sb.append(classes[i].getName()).append(".class");
           if(i<classes.length-1) {
               sb.append(',');
           }
       }
       return sb.toString();
    }

    /**
     * Gets the {@link javassist.ClassPool} for the corresponding {@link ClassLoader}.
     *
     * @param classLoader The class loader
     * @return The class pool
     */
    ClassPool getClassPool(ClassLoader classLoader) {
        synchronized (classPoolMap) {
            ClassPool cpool = classPoolMap.get(classLoader);
            if (cpool == null) {
                cpool = new ClassPool();
                //cpool.appendClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
                cpool.appendClassPath(new LoaderClassPath(classLoader));
                classPoolMap.put(classLoader, cpool);
            }
            return cpool;
        }
    }

    /**
     * Represents the elements of a method signature.
     */
    private static class MethodSignature
    {
        private final String name;
        private final Class<?> returnType;
        private final Class<?>[] parameterTypes;
        private Class<?>[] exceptionTypes;

        MethodSignature(Class<?> returnType, String name, Class<?>... parameterTypes) {
           this.returnType = returnType;
           this.name = name;
           this.parameterTypes = parameterTypes;
        }

        MethodSignature(Method method) {
           this(method.getReturnType(), method.getName(), method.getParameterTypes());
           setExceptionTypes(method.getExceptionTypes());
        }

        void setExceptionTypes(Class<?>... exceptionTypes) {
           this.exceptionTypes = exceptionTypes;
        }

        String getName() { return name; }
        Class<?> getReturnType() { return returnType; }
        Class<?>[] getParameterTypes() { return parameterTypes; }
        Class<?>[] getExceptionTypes() { return exceptionTypes==null ? new Class<?>[0] : exceptionTypes; }
    }
}