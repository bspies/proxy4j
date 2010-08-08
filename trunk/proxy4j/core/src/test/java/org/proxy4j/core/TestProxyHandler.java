package org.proxy4j.core;

import org.proxy4j.core.testobj.TestTarget;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * <p>Test implementation of {@link ProxyHandler}</p>
 * @author Brennan Spies
 */
public class TestProxyHandler implements ProxyHandler<TestTarget>
{
    private TestTarget target = new TestTarget();
    private Map<Method,Integer> methods = new HashMap<Method,Integer>();

    public TestProxyHandler() {
       for(Method m : target.getClass().getDeclaredMethods()) {
           methods.put(m, m.getParameterTypes().length);
       }
    }

    //handles method invocation
    public Object handle(ProxyInvocation<TestTarget> testTargetProxyInvocation) throws Throwable {
        Method m = testTargetProxyInvocation.getMethod();
        assertTrue("Method " + m.getName() + " not in TestTarget",
            methods.keySet().contains(testTargetProxyInvocation.getMethod()));
        assertEquals("Wrong number of arguments to method " + m.getName() + ": " + testTargetProxyInvocation.getArguments().length,
            methods.get(m),
            new Integer(testTargetProxyInvocation.getArguments().length));
        return testTargetProxyInvocation.invoke(target);
    }
}
