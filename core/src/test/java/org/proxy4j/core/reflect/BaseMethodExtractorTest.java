package org.proxy4j.core.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * <p>Abstract base class for {@code MethodExtractor} tests.</p>
 * @author Brennan Spies
 */
public abstract class BaseMethodExtractorTest
{
    /**
     * Asserts that the {@code MethodExtractor} returns the correct set of
     * proxyable methods.
     * @param extractor The method extractor
     * @param primaryClass The primary class whose methods will be extracted 
     */
    protected void assertExtractsProxyableMethods(MethodExtractor extractor, Class<?> primaryClass)
    {
        Set<SignatureKey> extractedSet = new HashSet<SignatureKey>();
        for (Method m : extractor.getProxyableMethods()) {
            extractedSet.add(new SignatureKey(m));
        }
        for(Method m : getAllMethodsInHierarchy(primaryClass)) {
            if(isExcluded(m, primaryClass) || (!extractor.isIncludeObjectMethods() && isObjectMethod(m))) {
               assertFalse("Method should not be extracted: " + m, extractedSet.contains(new SignatureKey(m)));
            } else {
               assertTrue("Method should be extracted: " + m, extractedSet.contains(new SignatureKey(m)));
            }
        }
    }

    /**
     * Asserts that the {@code MethodExtractor} returns the correct set of
     * public methods.
     * @param extractor The method extractor
     * @param primaryClass The primary class whose methods will be extracted
     */
    protected void assertExtractsPublicMethods(MethodExtractor extractor,  Class<?> primaryClass) {
        Set<SignatureKey> extractedSet = new HashSet<SignatureKey>();
        for (Method m : extractor.getPublicMethods()) {
            extractedSet.add(new SignatureKey(m));
        }
        for(Method m : getAllMethodsInHierarchy(primaryClass)) {
            if(isExcluded(m, primaryClass) || !java.lang.reflect.Modifier.isPublic(m.getModifiers()) ||
                    (!extractor.isIncludeObjectMethods() && isObjectMethod(m))) {
               assertFalse("Method should not be extracted: " + m, extractedSet.contains(new SignatureKey(m)));
            } else {
               assertTrue("Method should be extracted: " + m, extractedSet.contains(new SignatureKey(m)));
            }
        }
    }

    /**
     * Tests whether or not a method should be excluded from proxying: is
     * {@code final}, {@code static}, or {@code private}, its declaring class
     * is not in the same package as the primary class
     * @param method The method to test
     * @param primaryClass The primary class being extracted
     * @return True if this method is excluded from proxying
     */
    protected boolean isExcluded(Method method, Class<?> primaryClass) {
        int mod = method.getModifiers();
        return java.lang.reflect.Modifier.isFinal(mod) ||
               java.lang.reflect.Modifier.isPrivate(mod) ||
               java.lang.reflect.Modifier.isStatic(mod) ||
               (isPackagePrivate(mod) && !method.getDeclaringClass().getPackage().equals(primaryClass.getPackage()));
    }

    private boolean isPackagePrivate(int mod) {
        return !java.lang.reflect.Modifier.isPrivate(mod) &&
               !java.lang.reflect.Modifier.isProtected(mod) &&
               !java.lang.reflect.Modifier.isPublic(mod);
    }

    private boolean isObjectMethod(Method method) {
        return method.getDeclaringClass().getName().equals("java.lang.Object");
    }

    /**
     * Returns a Set of all methods in the hierarchy starting from the primary
     * class.
     * @param primaryClass The primary class being extracted
     * @return The set of all methods
     */
    private Set<Method> getAllMethodsInHierarchy(Class<?> primaryClass) {
        Set<Method> methods = new HashSet<Method>();
        for(Class<?> current= primaryClass; current!=null; current=current.getSuperclass()) {
          methods.addAll(Arrays.asList(current.getDeclaredMethods()));
          for(Class<?> i : current.getInterfaces()) {
              methods.addAll(Arrays.asList(i.getMethods()));
          }
        }
        return methods;
    }
}