package org.proxy4j.core.reflect;

import org.proxy4j.core.filter.MethodFilter;

import java.lang.reflect.Method;
import java.util.*;

/**
 *  <p></p>
 * @author Brennan Spies
 */
public class MultitypeMethodExtractor extends BaseMethodExtractor   //TODO InterfaceMethodExtractor?
{
    private SortedSet<Method> methodSet;

    public MultitypeMethodExtractor(Class<?>... types) {
      methodSet = new TreeSet<Method>(SignatureKey.methodComparator());
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
        Collection<Method> filteredMethods = new ArrayList<Method>();
        for(Method m : filteredMethods) {
            if(filter.accept(m)) {
                filteredMethods.add(m);
            }
        }
        return filteredMethods;
    }
}
