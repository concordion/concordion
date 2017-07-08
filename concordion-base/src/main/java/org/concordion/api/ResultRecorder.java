package org.concordion.api;

import org.concordion.internal.FailFastException;


public interface ResultRecorder {

    void record(Result result);

    void recordFailFastException(FailFastException exception);

    void setSpecificationDescription(String description);

    void record(ResultSummary result);

    void setForExample(boolean isForExample);

    void setImplementationStatus(ImplementationStatus implementationStatus);
    ImplementationStatus getImplementationStatus();
}
