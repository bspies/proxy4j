package org.proxy4j.core.reflect;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

/**
 * An enumeration of other modifiers besides {@link Visibility} that are of interest to
 * the {@link MethodExtractor}.
 * @author Brennan Spies
 * @since 1.0.0
 */
public enum Modifier {

    STATIC, FINAL, ABSTRACT;

    /**
     * Returns a set of the {@code Modifier} enum values that apply to
     * the given class member.
     * @param member The class member
     * @return The set of modifiers
     */
    public static EnumSet<Modifier> getModifiers(Member member) {
      int mod = member.getModifiers();
      Collection<Modifier> mods = new ArrayList<>();
      if(java.lang.reflect.Modifier.isStatic(mod))
          mods.add(STATIC);
      if(java.lang.reflect.Modifier.isFinal(mod)) {
          mods.add(FINAL);
      } else if(java.lang.reflect.Modifier.isAbstract(mod)) {
          mods.add(ABSTRACT);
      }
      return mods.isEmpty()?
              EnumSet.noneOf(Modifier.class):
              EnumSet.copyOf(mods);
    }
}
