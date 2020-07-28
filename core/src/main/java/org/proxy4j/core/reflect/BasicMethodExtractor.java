package org.proxy4j.core.reflect;

import org.proxy4j.core.filter.MethodFilter;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Extracts method information from the given type.
 * @author Brennan Spies
 */
public class BasicMethodExtractor extends UnitypeMethodExtractor
{
    private SortedSet<Method> methodSet;

    /**
     * Creates a {@code BasicMethodExtractor} with the primary type
     * @param owningType The primary type, owner of the proxied methods
     */
    public BasicMethodExtractor(Class<?> owningType) {
       super(owningType);
       methodSet = new TreeSet<Method>(SignatureKey.methodComparator());
       buildMap();
    }

    private void buildMap() {
        for (Class<?> current = getOwningType(); current != null; current = current.getSuperclass()) {
            if (!isIncludeObjectMethods() && current.getName().equals("java.lang.Object"))
                break;
            for (Method m : current.getDeclaredMethods()) {
                if(isProxyable(m)) {
                    if(!methodSet.contains(m))
                        methodSet.add(m);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Method> getProxyableMethods() {
        return methodSet;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Method> getPublicMethods() {
        Collection<Method> publicMethods = new ArrayList<Method>();
        for(Method m : methodSet) {
          if(Visibility.getVisibility(m)==Visibility.PUBLIC)
            publicMethods.add(m);
        }
        return publicMethods;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Method> getMethods(MethodFilter filter) {
        Collection<Method> results = new ArrayList<Method>();
        for(Method m : methodSet) {
            if(filter.accept(m)) {
                results.add(m);
            }
        }
        return results;
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
