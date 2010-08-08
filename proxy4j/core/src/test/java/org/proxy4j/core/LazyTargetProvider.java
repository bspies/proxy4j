/**
 * 
 */
package org.proxy4j.core;

import org.proxy4j.core.testobj.TestTarget;

import javax.inject.Provider;

/**
 * <p>Test {@link Provider} that lazily initializes a {@link org.proxy4j.core.testobj.TestTarget}.</p>
 * @author Brennan Spies
 */
public class LazyTargetProvider implements Provider<TestTarget>
{
	private TestTarget target;
	
	public TestTarget get() {
		if(target==null) {
			target = new TestTarget();
		}
		return target;
	}
	
	public boolean isInitialized() {
		return target!=null;
	}
}
