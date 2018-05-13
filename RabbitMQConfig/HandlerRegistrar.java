package com.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.rabbitmq.client.Channel;
import com.scheduler.shared.event.domain.event.Event;
import com.scheduler.shared.event.domain.event.Handler;
import com.scheduler.shared.event.infrastructure.RabbitConnection;

public class HandlerRegistrar {

    public void registerHandlers(RabbitConnection connection) {
        Set<Class<? extends Handler>> handlers = findAllClassesExtendingHandler();
        Map<Class<? extends Handler>, Set<String>> handlersWithKeys = determineRoutingKeysForAllHandlers(handlers);

        removeHandlersWithNoRoutingKeys(handlers, handlersWithKeys);
        registerQueueForEachHandler(handlersWithKeys, connection);
        startHandlers(handlers);
    }


    private Set<Class<? extends Handler>> findAllClassesExtendingHandler() {
        Set<Class<? extends Handler>> classes = new Reflections("").getSubTypesOf(Handler.class);
        return classes;
    }

    private Map<Class<? extends Handler>, Set<String>> determineRoutingKeysForAllHandlers(
            Set<Class<? extends Handler>> handlers) {
        Map<Class<? extends Handler>, Set<String>> keysForHandlersMap = new HashMap<>();
        handlers.forEach(handler -> keysForHandlersMap.put(handler, determineRoutingKeyForHandler(handler)));
        return keysForHandlersMap;
    }

    private Set<String> determineRoutingKeyForHandler(Class<? extends Handler> handlerClazz) {
        Method[] allHandlerMethods = handlerClazz.getDeclaredMethods();
        Set<String> routingKeys = new HashSet<>();

        for (Method method : allHandlerMethods) {
            if (isHandlingMethod(method)) {
                validateHandlingMethod(method);
                routingKeys.add(getEventNameFromHandlingMethod(method));
            }
        }

        return routingKeys;
    }

    private boolean isHandlingMethod(Method handlingMethod) {
        return handlingMethod.getName().equals("handle");
    }

    private void validateHandlingMethod(Method handlingMethod) {
        Class<?>[] events = handlingMethod.getParameterTypes();
        String handlerName = handlingMethod.getDeclaringClass().getName();

        if (events.length == 0) {
            throw new RuntimeException(handlerName + " has handling method without event parameter!");
        }
        else if (events.length > 1) {
            throw new RuntimeException(handlerName + " has more than two events in one of his handling methods!");
        }
        else if(Modifier.isPrivate(handlingMethod.getParameterTypes()[0].getModifiers())) {
            throw new RuntimeException(handlerName + " has private handling method - handle() must be public!");
        }
        else if (!(Event.class.isAssignableFrom(events[0]))) {
            throw new RuntimeException(handlerName + " handling method param [" + events[0].getSimpleName()
                    + "] is not extending event class");
        }
    }

    private String getEventNameFromHandlingMethod(Method handlingMethod) {
        return handlingMethod.getParameterTypes()[0].getName();
    }

    private void removeHandlersWithNoRoutingKeys(Set<Class<? extends Handler>> handlers,
            Map<Class<? extends Handler>, Set<String>> handlersWithKeys) {
        handlers.forEach(handler -> {
                    if(handlersWithKeys.get(handler).isEmpty()) {
                        handlers.remove(handler);
                        handlersWithKeys.remove(handler);
                    }
                }
        );
    }

    private void registerQueueForEachHandler(Map<Class<? extends Handler>, Set<String>> keysForHandlers,
            RabbitConnection connection) {
        Channel channel = connection.getNewChannel();

        keysForHandlers.keySet().forEach(handler -> {
            connection.createQueue(channel, handler.getName(), keysForHandlers.get(handler));
        });
    }

    private void startHandlers(Set<Class<? extends Handler>> handlers) {
        handlers.forEach(handler -> {
            try {
                handler.newInstance().runOnQueue(handler.getName());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}
