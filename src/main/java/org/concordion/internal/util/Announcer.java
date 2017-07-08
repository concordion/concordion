package org.concordion.internal.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * @deprecated Depricated due to performance issues - use java.util.List instead. When cross-compiled to Concordion.NET, the Announcers generate dynamic proxies that are realized by code compiled at run time. When profiled, the Announcer took 1460ms, vs 5ms for the list based solution.
 * <a href="http://nat.truemesh.com/archives/000710.html">Easy Java bean event notification</a>
 * courtesy of Nat Pryce.
 * [<a href="http://svn.jmock.codehaus.org/browse/jmock/trunk/jmock2/test/org/jmock/example/announcer/Announcer.java">Source</a>]
 */
@Deprecated
public class Announcer<T extends EventListener> {
    private final T proxy;
    private final List<T> listeners = new ArrayList<T>();
    
    public Announcer(Class<? extends T> listenerType) {
        proxy = listenerType.cast(Proxy.newProxyInstance(
            listenerType.getClassLoader(), 
            new Class<?>[]{listenerType}, 
            new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    announce(method, args);
                    return null;
                }
            }));
    }
    
    public void addListener(T listener) {
        listeners.add(listener);
    }
    
    public void removeListener(T listener) {
        listeners.remove(listener);
    }
    
    public T announce() {
        return proxy;
    }
    
    private void announce(Method m, Object[] args) {
        try {
            for (T listener : listeners) {
                m.invoke(listener, args);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("could not invoke listener", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            } else if (cause instanceof Error) {
                throw (Error)cause;
            } else {
                throw new UnsupportedOperationException("listener threw exception", cause);
            }
        }
    }
    
    public static <T extends EventListener> Announcer<T> to(Class<? extends T> listenerType) {
        return new Announcer<T>(listenerType);
    }
}