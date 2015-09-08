package org.proxy4j.core;

import org.proxy4j.core.testobj.Target;
import org.proxy4j.core.testobj.TestTarget;

/**
 * <p></p>
 * @author Brennan Spies
 */
public class TestMultiInterfaceProxyHandler implements ProxyHandler
{
    private Target t = new TestTarget();

    //Tests invocation from more than one interface on the proxy
    public Object handle(ProxyInvocation proxyInvocation) throws Throwable {
        String methodName = proxyInvocation.getMethod().getName();
        if(methodName.equals("getMessage"))
            return "Hello World!";
        else
            return proxyInvocation.getMethod().invoke(t, proxyInvocation.getArguments());
    }
}
