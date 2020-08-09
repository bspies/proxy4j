package org.proxy4j.core.reflect;

import java.lang.reflect.Method;
import java.util.EnumSet;

/**
 * Method extractor for single type.
 * @author Brennan Spies
 * @since 1.0.0
 */
public abstract class UnitypeMethodExtractor extends BaseMethodExtractor
{
    private final Class<?> owningType;

    public UnitypeMethodExtractor(Class<?> owningType) {
        this.owningType = owningType;
    }

    /**
     * Returns the package of the owning type.
     * @return The package
     */
    protected String getOwningPackageName() {
        return owningType.getPackage().getName();
    }

    /**
     * Returns the primary/owning type of the methods to be proxied.
     * @return The owning type
     */
    protected Class<?> getOwningType() {
        return owningType;
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
