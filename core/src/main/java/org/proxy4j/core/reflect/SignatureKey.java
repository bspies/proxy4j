package org.proxy4j.core.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents a method key for methods <i>within</i> the same class
 * hierarchy.
 * @author Brennan Spies
 */
public class SignatureKey implements Comparable<SignatureKey>
{
    private final String name;
    private final Class<?>[] parameterTypes;

    /**                                  
     * Creates a {@code SignatureKey} from the method.
     * @param method The method from which to create the signature
     */
    public SignatureKey(Method method) {
       this(method.getName(), method.getParameterTypes());
    }

    /**
     * Creates a {@code SignatureKey} from the given name and
     * parameter types.
     * @param name The method name
     * @param parameterTypes The method parameter types
     */
    public SignatureKey(String name, Class<?>... parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    /**
     * Returns a {@link Comparator} that compares two methods
     * on the basis of their signatures.
     * @return The method comparator
     */
    public static Comparator<Method> methodComparator() {
        return new Comparator<Method>() {
            public int compare(Method m1, Method m2) {
                return new SignatureKey(m1).compareTo(new SignatureKey(m2));
            }
        };
    }

    /**
     * Gets the method name.
     * @return The method name
     */
    public String getName() {
       return name; 
    }

    /**
     * Gets the parameter types to the method.
     * @return The parameter types
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Equality of method signatures is done strictly on method name and parameter types.
     * @param o The object to compare for equality
     * @return True if equal
     */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureKey signatureKey = (SignatureKey) o;
        return name.equals(signatureKey.name) &&
            Arrays.equals(parameterTypes, signatureKey.parameterTypes);
    }

    /**
     * Returns the hash code.
     * @return The hash code
     */
    @Override public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }

    /**
     * Compares two signatures on method name, number of parameters, and finally
     * the parameter types themselves.
     * @param other The other signature
     * @return 0 if equal, negative int if this is less than other, positive int if otherwise
     */
    public int compareTo(SignatureKey other) {
        int comparison = name.compareTo(other.name);
        if(comparison==0) {
          comparison = new Integer(parameterTypes.length).compareTo(other.parameterTypes.length);
          if(comparison==0) {
             for(int i=0; i<parameterTypes.length; i++) {
                 if((comparison=parameterTypes[i].getName().compareTo(other.parameterTypes[i].getName()))!=0)
                     break;
             }
          }
        }
        return comparison;
    }
}
