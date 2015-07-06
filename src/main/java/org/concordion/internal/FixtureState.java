package org.concordion.internal;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.*;

public enum FixtureState {
    UNIMPLEMENTED("unimplemented", Unimplemented.class) {

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
		public ResultSummary getMeaningfulResultSummary(
				ResultSummary rs, FailFastException ffe) {
			assertIsSatisfied(rs, ffe);
			return new SingleResultSummary(Result.IGNORED);
		}

       @Override
       public String printNoteToString() {
        	return "   <-- Note: This test has been marked as UNIMPLEMENTED";
       }

    },
    EXPECTED_TO_FAIL("ExpectedToFail", ExpectedToFail.class) {

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
        	return "   <-- Note: This test has been marked as EXPECTED_TO_FAIL";
       }
    },
    EXPECTED_TO_PASS("ExpectedToPass", ExpectedToPass.class) {

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
		public ResultSummary getMeaningfulResultSummary(
				ResultSummary rs, FailFastException ffe) {
			assertIsSatisfied(rs, ffe);
			return rs;
		}

        @Override
        public ResultSummary convertForCache(ResultSummary rs) {
            // if we're expected to pass, then just use the result summary.
            return rs;
        }
    };

    private final String tag;
    private final Class<? extends Annotation> annotation;

    FixtureState(String tag, Class<? extends Annotation> annotation) {
        this.tag = tag;
        this.annotation = annotation;
    }

    public abstract void assertIsSatisfied(ResultSummary rs, FailFastException ffe);

    public void printNote(PrintStream out) {
    	out.print(printNoteToString());
    }
    
    public abstract String printNoteToString();

	public abstract ResultSummary getMeaningfulResultSummary(ResultSummary rs, FailFastException ffe);

    public abstract ResultSummary convertForCache(ResultSummary rs);


    public static FixtureState getFixtureState(Class<?> fixtureClass, String example) {
        // examples have precedence
        if (example == null) {

            // loop through the states
            if (fixtureClass != null) {
                for (FixtureState state: values()) {
                    // if we found a match, then return the state
                    if (fixtureClass.getAnnotation(state.annotation) != null) {
                        return state;
                    }
                }
            }

        } else {
            // first check if the example has parameters
            int questionPlace = example.indexOf('?');
            if (questionPlace >= 0) {

                // right, it does. Get the list and split it.
                String parametersList = example.substring(questionPlace + 1);
                String[] parameters = parametersList.split("&");

                // for each parameter, we want to see if it matches a state
                for (String parameter: parameters) {

                    // so pull the parts of the parameter apart
                    int equalsSign = parameter.indexOf('=');
                    String parameterName;
                    String parameterValue;
                    if (equalsSign >= 0) {
                        parameterName = parameter.substring(0, equalsSign);
                        parameterValue = parameter.substring(equalsSign+1);
                    } else {
                        // no equals sign, assume the value is "true"
                        parameterName = parameter;
                        parameterValue = "true";
                    }

                    // right. We've decomposed a parameter. Now check if the parameter name
                    // is the same as any of our state names.
                    for (FixtureState checkState: values()) {

                        // equalsIgnoreCase makes life more interesting. And useful.
                        if (checkState.getAnnotationTag().equalsIgnoreCase(parameterName)) {

                            // Finally...check if the param is turned on!
                            if (Boolean.parseBoolean(parameterValue)) {
                                return checkState;
                            }

                        }
                    }
                }
            }

        }

        return EXPECTED_TO_PASS;
    }

    public String getAnnotationTag() {
        return tag;
    }
}
