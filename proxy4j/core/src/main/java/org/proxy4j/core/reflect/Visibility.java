package org.proxy4j.core.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * <p>Enumeration of class member visibility.</p>
 * @author Brennan Spies
 */
public enum Visibility {

    PUBLIC, PROTECTED, PACKAGE, PRIVATE;

    public static Visibility getVisibility(Member member) {
        return get(member.getModifiers());
    }

    /**
     * Returns the visibility from the given modifier flag set.
     * @param mod The modifier flag set
     * @return The visibility
     * @see Modifier
     */
    public static Visibility get(int mod) {
        if(Modifier.isPublic(mod)) {
            return PUBLIC;
        } else if(Modifier.isProtected(mod)) {
            return PROTECTED;
        } else if(Modifier.isPrivate(mod)) {
            return PRIVATE;
        } else {
            return PACKAGE;
        }
    }
}
