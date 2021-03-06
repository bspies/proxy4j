package org.proxy4j.core.reflect;

import org.proxy4j.core.filter.MethodFilter;

import java.lang.reflect.Method;
import java.util.*;

/**
 * A two-level {@code MethodExtractor} where the super type acts as an additional source of information,
 * e.g. accessing annotations in {@link org.proxy4j.core.filter.AnnotationFilter method filters}.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class InheritableMethodExtractor extends UnitypeMethodExtractor
{
    private final Class<?> superType;
    private final Map<Method,Method> methodPairMap;

    /**
     * Creates a {@code MethodExtractor} with a super type that acts as an additional
     * source of information for {@link MethodFilter MethodFilters}, e.g. in exposing annotations.
     * In most cases, the super type will be an interface.
     * @param owningType The method type to be proxied
     * @param superType Any super type of the method type
     * @param <T> The {@link java.lang.reflect.Type Type} of the method class
     */
    public <T> InheritableMethodExtractor(Class<T> owningType, Class<? super T> superType) {
       super(owningType);
       this.superType = superType;
       this.methodPairMap = new TreeMap<>(SignatureKey.methodComparator());
       buildMethodMap();
    }

    //builds the method map
    private void buildMethodMap() {
        //get all supertype methods
        Map<SignatureKey,Method> superTypeMap = new HashMap<>();
        for(Method m : superType.getMethods()) {
            superTypeMap.put(new SignatureKey(m), m);
        }
       //traverse the owningType and supertypes/interfaces to build the method hierarchy
       for(Class<?> current = getOwningType(); current!=null; current=current.getSuperclass()) {
           if(!isIncludeObjectMethods() && current.getName().equals("java.lang.Object"))
               break;
           for(Method m : current.getDeclaredMethods()) {
               if(!methodPairMap.containsKey(m)) {
                  SignatureKey s = new SignatureKey(m);
                  methodPairMap.put(m, superTypeMap.get(s));
               }
           }
       }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Method> getProxyableMethods() {
        return methodPairMap.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Method> getPublicMethods() {
        Collection<Method> publicMethods = new ArrayList<>();
        for(Method m : methodPairMap.keySet()) {
            if(Visibility.getVisibility(m)==Visibility.PUBLIC)
                publicMethods.add(m);
        }
        return publicMethods;
    }

    /**
     * Returns accessible methods that pass the given filter.
     * @param filter The method filter
     * @return The filtered methods
     */
    public Collection<Method> getMethods(MethodFilter filter) {
        Collection<Method> methods = new ArrayList<>();
        for(Method m : methodPairMap.keySet()) {
           Method other;
           if(filter.accept(m) || ((other=methodPairMap.get(m))!=null && filter.accept(other)))
               methods.add(m);
        }
        return methods;
    }

    /**
     * True if the method is declared by {@code java.lang.Object}.
     * @param method The method
     * @return True if this method is declared by {@code Object}
     */
    protected boolean isObjectMethod(Method method) {
        return method.getDeclaringClass().getName().equals("java.lang.Object");
    }

    /**
     * Determines if the given method is proxyable.
     * @param method The method to test
     * @return True if method is proxyable
     */
    protected boolean isProxyable(Method method) {
       Visibility methodVisibility = Visibility.getVisibility(method);
       EnumSet<Modifier> modifiers = Modifier.getModifiers(method);
       if(methodVisibility==Visibility.PRIVATE || modifiers.contains(Modifier.FINAL) || modifiers.contains(Modifier.STATIC))
           return false;
       else
           return methodVisibility == Visibility.PUBLIC ||
               methodVisibility == Visibility.PROTECTED ||
               getPackageName(method.getDeclaringClass()).equals(getOwningPackageName());
    }
}
