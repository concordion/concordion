package test.concordion;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;

public class EventRecorder implements AssertEqualsListener, ThrowableCaughtListener {

    private List<Object> events = new ArrayList<Object>();

    public void failureReported(AssertFailureEvent event) {
        events.add(event);
    }

    public void successReported(AssertSuccessEvent event) {
        events.add(event);
    }

    public void throwableCaught(ThrowableCaughtEvent event) {
        events.add(event);
    }

    public Object getLast(Class<?> eventClass) {
        Object lastMatch = null;
        for (Object object : events) {
            if (eventClass.isAssignableFrom(object.getClass())) {
                lastMatch = object;
            }
        }
        return lastMatch;
    }


}
