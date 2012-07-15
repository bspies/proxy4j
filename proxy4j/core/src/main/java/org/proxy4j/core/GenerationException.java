package org.proxy4j.core;

/**
 * Exception indicating a failure in proxy generation.
 *
 * @author Brennan Spies
 * @since 1.0
 */
public class GenerationException extends RuntimeException
{
	private static final long serialVersionUID = -4305043242284725626L;

	/**
     * Creates a {@code GenerationException} with a message.
     * @param message The exception message
     */
    public GenerationException(String message) {
        super(message);
    }

    /**
     * Creates a {@code GenerationException} with a message and an
     * underlying cause.
     * @param message The message
     * @param cause The underlying cause
     */
    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
