package org.proxy4j.core.util;

import org.proxy4j.core.filter.MethodFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <p></p>
 * @author Brennan Spies
 */
public class MethodExtractor
{
    private Class<?> owningType;
    private String packageName;
    private boolean includeObjectMethods;
    //PROTECTED only needs to return true
    private static final MethodFilter PROTECTED_FILTER = new MethodFilter() {
        public boolean accept(Method method) {
            return true;
        }
    };

    public MethodExtractor(Class<?> owningType) {
       this.owningType = owningType;
       this.packageName = getPackageName(owningType);
    }

    /**
     * Sets the flag to include {@link Object} methods when
     * extracting methods from a type. By default this is false.
     * @param include True if {@code Object} methods are included
     */
    public void setIncludeObjectMethods(boolean include) {
        this.includeObjectMethods = include;
    }

    private boolean isObjectMethod(Method method) {
        return method.getDeclaringClass().getName().equals("java.lang.Object");
    }

    public Collection<Method> getMethods(Visibility visibility) {
        return visibility==Visibility.PROTECTED ?
                getAllMethods(PROTECTED_FILTER) :
                getPublicMethods();
    }

    /**
     * Returns all public methods from the type, excluding {@link Object}
     * methods if appropriate.
     * @return All public methods
     */
    public Collection<Method> getPublicMethods() {
        return getPublicMethods(new MethodFilter() {
            public boolean accept(Method method) {
                return true;
            }
        });
    }

    /**
     * Returns all public methods from the type filtered by the given filter.
     * Will also exclude {@link Object} method if appropriate.
     * @param filter The method filter
     * @return The filter collection of all public methods
     */
    public Collection<Method> getPublicMethods(MethodFilter filter) {
        Set<Method> publicMethods = new HashSet<Method>();
        for(Method m : owningType.getMethods()) {
          if((includeObjectMethods || !isObjectMethod(m)) && accept(m, false) && filter.accept(m))
              publicMethods.add(m);
        }
        return publicMethods;
    }

    /**
     * Returns all externally visible methods from the type (including package
     * private and protected methods), excluding {@link Object} methods if
     * appropriate. Methods are filtered by the given filter.
     * @param filter The method filter.
     * @return All public, package private, and protected methods
     */
    public Collection<Method> getAllMethods(MethodFilter filter) {
        Set<Method> methods = new HashSet<Method>();
        for(Class<?> current = owningType; current!=null; current=current.getSuperclass()) {
            if(!includeObjectMethods && current.getName().equals("java.lang.Object"))
                break;
            for(Method m : current.getDeclaredMethods()) {
                if(accept(m, true) && filter.accept(m))
                    methods.add(m);
          }
        }
        return methods;
    }

    private String getPackageName(Class<?> type) {
        return type.getPackage().getName();
    }

    //an internal method filter
    private boolean accept(Method method, boolean nonPublic) {
       int mod = method.getModifiers();
       if(Modifier.isPrivate(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod))
           return false;
       else if(Modifier.isPublic(mod))
           return true;
       else
           return nonPublic && (Modifier.isProtected(mod) ||
                  getPackageName(method.getDeclaringClass()).equals(packageName));
    }
}
