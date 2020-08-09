package org.proxy4j.core.init;

import javax.inject.Provider;

/**
 * Encapsulates the use of the double-checked lock idiom for lazily initializing
 * an instance in a thread-safe way. Subclasses override the {@code init()} method to
 * provide the lazily initialized instance.
 * @author Brennan Spies
 * @since 1.0.0
 */
public abstract class LazyProvider<T> implements Provider<T>
{
    private volatile T var;

    /**
     * Gets the instance of {@code T} lazily.
     * @return The instance of {@code T}
     */
    public T get() {
        if(var==null) {
            synchronized(this) {
            	if(var==null)
            		var = init();
            }
        }
        return var;
    }

    /**
     * Must be overriden to initialize the variable reference.
     * @return The newly created instance of {@code T}
     */
    protected abstract T init();
}
