package org.proxy4j.core.reflect;

import org.junit.Test;
import org.proxy4j.core.filter.AnnotationFilter;
import org.proxy4j.core.testobj.Target;
import org.proxy4j.core.testobj.TestMarker;
import org.proxy4j.core.testobj.TestTarget;

import java.lang.reflect.Method;
import java.util.Collection;

import static junit.framework.Assert.*;

/**
 * <p>Tests to ensure that the {@link InheritableMethodExtractor} conforms to the {@code MethodExtractor}
 * interface contract, and that a {@link org.proxy4j.core.filter.MethodFilter MethodFilter} is tested against
 * both concrete and interface methods.</p>
 * @author Brennan Spies
 */
public class InheritableMethodExtractorTest extends BaseMethodExtractorTest
{
    @Test
    public void testExtractingProxyableMethods() {
        MethodExtractor extractor = new InheritableMethodExtractor(TestTarget.class, Target.class);
        assertExtractsProxyableMethods(extractor, TestTarget.class);
    }

    @Test
    public void testExtractingPublicMethods() {
        MethodExtractor extractor = new InheritableMethodExtractor(TestTarget.class, Target.class);
        assertExtractsPublicMethods(extractor, TestTarget.class);
    }

    @Test
    public void testAccessingInterfaceMethods() throws Exception {
        MethodExtractor extractor = new InheritableMethodExtractor(TestImpl.class, TestAnnotatedIFace.class);
        Collection<Method> methods = extractor.getMethods(AnnotationFilter.forAnnotation(TestMarker.class));
        assertTrue(methods.contains(TestImpl.class.getMethod("testMethodWithAnnotation")));
        assertFalse(methods.contains(TestImpl.class.getMethod("testMethodWithoutAnnotation")));
    }

    /**
     * Test class definitions.
     */

    private static interface TestAnnotatedIFace {
        @TestMarker
        public void testMethodWithAnnotation();
        public void testMethodWithoutAnnotation();
    }

    private static class TestImpl implements TestAnnotatedIFace {
        public void testMethodWithAnnotation() {}
        public void testMethodWithoutAnnotation() {}
    }
}
