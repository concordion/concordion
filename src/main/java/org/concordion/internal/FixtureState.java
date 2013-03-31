package org.concordion.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public enum FixtureState {
    UNIMPLEMENTED {

        private void addToList(List<String> list, long x, String singular, String plural) {
            if (x == 1) {
                list.add(singular);
            } else if (x > 1) {
                list.add(plural);
            }
        }
        
        @Override
        public void assertIsSatisfied(long successCount, long failureCount, long exceptionCount) {
            List<String> list = new ArrayList<String>();
            addToList(list, successCount, "a success", "some successes");
            addToList(list, failureCount, "a failure", "some failures");
            addToList(list, exceptionCount, "an exception", "some exceptions");
            if (list.size() > 0) {
                String s = list.get(0);
                if (list.size() > 1) {
                    for (int i = 1; i < (list.size() - 1); i++) {
                        s += ", " + list.get(i);
                    }
                    s += ", and " + list.get(list.size() - 1);
                }
                throw new AssertionError("Specification is supposed to be unimplemented, but is reporting " + s + ".");
            }
        }

        @Override
        public void printNote(PrintStream out) {
            out.print("   <-- Note: This test has been marked as UNIMPLEMENTED");
        }
    },
    EXPECTED_TO_FAIL {

        @Override
        public void assertIsSatisfied(long successCount, long failureCount, long exceptionCount) {
            if (failureCount + exceptionCount == 0) {
                throw new AssertionError("Specification is expected to fail but has neither failures nor exceptions.");
            }
        }

        @Override
        public void printNote(PrintStream out) {
            out.print("   <-- Note: This test has been marked as EXPECTED_TO_FAIL");
        }
    },
    EXPECTED_TO_PASS {

        @Override
        public void assertIsSatisfied(long successCount, long failureCount, long exceptionCount) {
            if (failureCount > 0) {
                throw new AssertionError("Specification has failure(s). See output HTML for details.");
            }
            if (exceptionCount > 0) {
                throw new AssertionError("Specification has exception(s). See output HTML for details.");
            }
        }

        @Override
        public void printNote(PrintStream out) {
        }
    };
    
    public abstract void assertIsSatisfied(long successCount, long failureCount, long exceptionCount);

    public abstract void printNote(PrintStream out);
}
