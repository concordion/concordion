package org.concordion.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;

public enum ImplementationStatusChecker {
    UNIMPLEMENTED(ImplementationStatus.UNIMPLEMENTED) {

        private void addToList(List<String> list, long x, String singular, String plural) {
            if (x == 1) {
                list.add(singular);
            } else if (x > 1) {
                list.add(plural);
            }
        }

        @Override
        public void assertIsSatisfied(ResultSummary rs, FailFastException ffe) {
            List<String> list = new ArrayList<String>();
            addToList(list, rs.getSuccessCount(), "a success", "some successes");
            addToList(list, rs.getFailureCount(), "a failure", "some failures");
            addToList(list, rs.getExceptionCount(), "an exception", "some exceptions");
            if (list.size() > 0) {
                StringBuilder s = new StringBuilder();
                s.append(list.get(0));
                if (list.size() > 1) {
                    for (int i = 1; i < (list.size() - 1); i++) {
                        s.append(", ").append(list.get(i));
                    }
                    s.append(", and ").append(list.get(list.size() - 1));
                }
                throw new ConcordionAssertionError("Specification is supposed to be unimplemented, but is reporting " + s + ".", rs);

            }
        }

        @Override
        public ResultSummary convertForCache(ResultSummary rs) {
            try {
                assertIsSatisfied(rs, null);
                return new SingleResultSummary(Result.IGNORED, rs.getSpecificationDescription());
            } catch (ConcordionAssertionError cce) {
                return new SingleResultSummary(Result.FAILURE, rs.getSpecificationDescription());
            }
        }

        @Override
        public ResultSummary getMeaningfulResultSummary(ResultSummary rs, FailFastException ffe) {
            assertIsSatisfied(rs, ffe);
            return new SingleResultSummary(Result.IGNORED);
        }

        @Override
        public String printNoteToString() {
            return "   <-- Note: This example has been marked as UNIMPLEMENTED";
        }

    },
    EXPECTED_TO_FAIL(ImplementationStatus.EXPECTED_TO_FAIL) {

        @Override
        public void assertIsSatisfied(ResultSummary rs, FailFastException ffe) {
            if (rs.getFailureCount() + rs.getExceptionCount() == 0) {
                throw new ConcordionAssertionError("Specification is expected to fail but has neither failures nor exceptions.", rs);
            }

        }

        @Override
        public ResultSummary getMeaningfulResultSummary(ResultSummary rs, FailFastException ffe) {
            assertIsSatisfied(rs, ffe);
            return new SingleResultSummary(Result.IGNORED);
        }

        @Override
        public ResultSummary convertForCache(ResultSummary rs) {
            try {
                assertIsSatisfied(rs, null);
                return new SingleResultSummary(Result.IGNORED, rs.getSpecificationDescription());
            } catch (ConcordionAssertionError cce) {
                return new SingleResultSummary(Result.FAILURE, rs.getSpecificationDescription());
            }
        }

        @Override
        public String printNoteToString() {
            return "   <-- Note: This example has been marked as EXPECTED_TO_FAIL";
        }
    },
    EXPECTED_TO_PASS(ImplementationStatus.EXPECTED_TO_PASS) {

        @Override
        public void assertIsSatisfied(ResultSummary rs, FailFastException ffe) {
            if (ffe != null) {
                throw ffe;
            }
            if (rs.getFailureCount() > 0) {
                throw new ConcordionAssertionError("Specification has failure(s). See output HTML for details.", rs);
            }
            if (rs.getExceptionCount() > 0) {
                throw new ConcordionAssertionError("Specification has exception(s). See output HTML for details.", rs);
            }
        }

        @Override
        public String printNoteToString() {
            return "";
        }

        @Override
        public ResultSummary getMeaningfulResultSummary(ResultSummary rs, FailFastException ffe) {
            assertIsSatisfied(rs, ffe);
            return rs;
        }

        @Override
        public ResultSummary convertForCache(ResultSummary rs) {
            // if we're expected to pass, then just use the result summary.
            return rs;
        }
    };

    private final ImplementationStatus implementationStatus;

    ImplementationStatusChecker(ImplementationStatus implementationStatus) {
        this.implementationStatus = implementationStatus;
    }

    public abstract void assertIsSatisfied(ResultSummary rs, FailFastException ffe);

    public void printNote(PrintStream out) {
        out.print(printNoteToString());
    }

    public abstract String printNoteToString();

    public abstract ResultSummary getMeaningfulResultSummary(ResultSummary rs, FailFastException ffe);

    public abstract ResultSummary convertForCache(ResultSummary rs);

    public String getAnnotationTag() {
        return implementationStatus.getTag();
    }

    public ImplementationStatus getImplementationStatus() {
        return implementationStatus;
    }

    public static ImplementationStatusChecker getImplementationStatusChecker(Fixture fixture, ImplementationStatus implementationStatus) {
        // examples have precedence
        if (implementationStatus != null) {
            return implementationStatusCheckerFor(implementationStatus);
        }

        return implementationStatusCheckerFor(fixture.getDeclaredImplementationStatus());
    }

    public static ImplementationStatusChecker implementationStatusCheckerFor(ImplementationStatus implementationStatus) {
        for (ImplementationStatusChecker checker : values()) {
            if (checker.getImplementationStatus() == implementationStatus) {
                return checker;
            }
        }

        return EXPECTED_TO_PASS;
    }
}
