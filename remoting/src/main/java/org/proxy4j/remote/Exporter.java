package org.proxy4j.remote;

/**
 * Implementations of this interface are responsible for
 * exporting services remotely.
 *
 * @author Brennan Spies
 */
public interface Exporter {
    /**
     * Exports the given service implementation remotely.
     * @param remotableObject The service implementation to export
     * @return The proxied service
     */
    public <T> T export(T remotableObject);

    /**
     * Exports a <i>specific</i> interface of the given remotable object.
     * @param remoteInterface The interface to export
     * @param remotableObject The service implementation to export
     * @return The proxied service
     */
    public <T> T export(Class<T> remoteInterface, T remotableObject);
}
