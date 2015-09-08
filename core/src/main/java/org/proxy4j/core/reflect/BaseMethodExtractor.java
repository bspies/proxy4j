package org.proxy4j.core.reflect;

import java.lang.reflect.Method;
import java.util.EnumSet;

/**
 * <p>Abstract base class for method extractors.</p>
 * @author Brennan Spies
 */
public abstract class BaseMethodExtractor implements MethodExtractor
{
    private boolean includeObjectMethods;

    /************* Properties *****************/

    /**
     * True if {@code java.lang.Object} methods should be proxied.
     * @return True if Object methods should be proxied
     */
    public boolean isIncludeObjectMethods() {
        return includeObjectMethods;
    }

    /**
     * Determines whether or not {@code java.lang.Object} methods should
     * be proxied.
     * @param include True if Object methods shold be proxied
     */
    public void setIncludeObjectMethods(boolean include) {
        this.includeObjectMethods = include;
    }

    /************* Utility Methods **************/

    /**
     * Returns the package name of the class.
     * @param type The class
     * @return The package name of the class
     */
    protected String getPackageName(Class<?> type) {
        return type.getPackage().getName();
    }

}
