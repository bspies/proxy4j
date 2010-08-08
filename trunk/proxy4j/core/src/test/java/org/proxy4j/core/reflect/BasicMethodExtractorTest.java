package org.proxy4j.core.reflect;

import org.junit.Test;
import org.proxy4j.core.testobj.AbstractTarget;
import org.proxy4j.core.testobj.Target;
import org.proxy4j.core.testobj.TestTarget;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * <p>Tests to ensure that the {@link BasicMethodExtractor} conforms to the
 * {@code MethodExtractor} interface contract.</p>
 * @author Brennan Spies
 */
public class BasicMethodExtractorTest extends BaseMethodExtractorTest
{
    /**
     * Test that the extractor extracts the correct proxyable methods.
     */
    @Test
    public void testExtractingProxyableMethods() {
      MethodExtractor extractor = new BasicMethodExtractor(TestTarget.class);
      this.assertExtractsProxyableMethods(extractor, TestTarget.class);
    }

    /**
     * Test that the extractor extracts the correct public methods.
     */
    @Test
    public void testExtractingPublicMethods() {
      MethodExtractor extractor = new BasicMethodExtractor(TestTarget.class);
      this.assertExtractsPublicMethods(extractor, TestTarget.class);
    }
}
