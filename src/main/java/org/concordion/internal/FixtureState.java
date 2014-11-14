package org.concordion.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Result;
import org.concordion.api.ResultSummary;

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
        public void assertIsSatisfied(ResultSummary rs, FailFastException ffe) {
            List<String> list = new ArrayList<String>();
            addToList(list, rs.getSuccessCount(), "a success", "some successes");
            addToList(list, rs.getFailureCount(), "a failure", "some failures");
            addToList(list, rs.getExceptionCount(), "an exception", "some exceptions");
            if (list.size() > 0) {
                String s = list.get(0);
                if (list.size() > 1) {
                    for (int i = 1; i < (list.size() - 1); i++) {
                        s += ", " + list.get(i);
                    }
                    s += ", and " + list.get(list.size() - 1);
                }
                throw new ConcordionAssertionError("Specification is supposed to be unimplemented, but is reporting " + s + ".", rs);
                
            }
        }
        
		@Override
		public ResultSummary getMeaningfulResultSummary(
				ResultSummary rs, FailFastException ffe) {
			assertIsSatisfied(rs, ffe);
			return new SingleResultSummary(Result.IGNORED);
		}
 

        @Override
        public void printNote(PrintStream out) {
            out.print("   <-- Note: This test has been marked as UNIMPLEMENTED");
        }
    },
    EXPECTED_TO_FAIL {

        @Override
        public void assertIsSatisfied(ResultSummary rs, FailFastException ffe) {
            if (rs.getFailureCount() + rs.getExceptionCount() == 0) {
                throw new ConcordionAssertionError("Specification is expected to fail but has neither failures nor exceptions.", rs);
            }
           
        }

		@Override
		public ResultSummary getMeaningfulResultSummary(
				ResultSummary rs, FailFastException ffe) {
			assertIsSatisfied(rs, ffe);
			return new SingleResultSummary(Result.IGNORED);
		}
 
        
        @Override
        public void printNote(PrintStream out) {
            out.print("   <-- Note: This test has been marked as EXPECTED_TO_FAIL");
        }
    },
    EXPECTED_TO_PASS {

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
        public void printNote(PrintStream out) {
        }

		@Override
		public ResultSummary getMeaningfulResultSummary(
				ResultSummary rs, FailFastException ffe) {
			assertIsSatisfied(rs, ffe);
			return rs;
		}
    };

    public abstract void assertIsSatisfied(ResultSummary rs, FailFastException ffe);

    public abstract void printNote(PrintStream out);

	public abstract ResultSummary getMeaningfulResultSummary(ResultSummary rs, FailFastException ffe);
}
