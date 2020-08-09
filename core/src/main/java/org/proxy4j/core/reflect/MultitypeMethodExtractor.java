package org.proxy4j.core.reflect;

import org.proxy4j.core.filter.MethodFilter;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Method extractor for multiple types.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class MultitypeMethodExtractor extends BaseMethodExtractor   //TODO InterfaceMethodExtractor?
{
    private final SortedSet<Method> methodSet;

    public MultitypeMethodExtractor(Class<?>... types) {
      methodSet = new TreeSet<>(SignatureKey.methodComparator());
      for(Class<?> type : types) {
          methodSet.addAll(Arrays.asList(type.getMethods()));
      }
    }

    public Collection<Method> getProxyableMethods() {
        return methodSet;
    }

    public Collection<Method> getPublicMethods() {
        return methodSet;
    }

    public Collection<Method> getMethods(MethodFilter filter) {
        Collection<Method> filteredMethods = new ArrayList<>();
        for(Method m : methodSet) {
            if(filter.accept(m)) {
                filteredMethods.add(m);
            }
        }
        return filteredMethods;
    }
}
