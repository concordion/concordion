package org.concordion.api;

import org.concordion.internal.FailFastException;

public interface ResultRecorder {

    void record(Result result);

    void recordFailFastException(FailFastException exception);

    void setSpecificationDescription(String description);
}
