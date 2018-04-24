package io.qameta.atlas.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Atlas method handler.
 */
public class AtlasMethodHandler implements InvocationHandler {

    private final Map<Method, InvocationHandler> handlers;

    private final ListenerNotifier notifier;

    public AtlasMethodHandler(final Map<Method, InvocationHandler> handlers) {
        this.notifier = new ListenerNotifier();
        this.handlers = handlers;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        notifier.beforeMethodCall(method, args);
        try {
            final InvocationHandler handler = handlers.get(method);
            final Object result = handler.invoke(proxy, method, args);
            notifier.onMethodReturn(method, args, result);
            return result;
        } catch (Throwable e) {
            notifier.onMethodFailure(method, args, e);
            throw e;
        } finally {
            notifier.afterMethodCall(method, args);
        }
    }

}
