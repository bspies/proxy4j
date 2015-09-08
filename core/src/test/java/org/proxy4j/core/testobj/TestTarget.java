package org.proxy4j.core.testobj;

/**
     * <p>Target type for proxy testing.</p>
     * @author Brennan Spies
     */
    public class TestTarget extends AbstractTarget implements Target {

        private int count = 0;

        @TestMarker
        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @TestMarker
        public void increment() {
            count++;
        }

        protected void doSomethingInSubclass() {
            System.out.println("Hello!");
        }
    }
