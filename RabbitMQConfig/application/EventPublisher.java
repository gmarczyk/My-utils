package com.scheduler.shared.event.application;

import java.io.IOException;

import com.rabbitmq.client.Channel;

import com.scheduler.shared.event.infrastructure.RabbitConfig;
import com.scheduler.shared.event.domain.event.Event;
import com.scheduler.shared.event.infrastructure.RabbitConnection;
import com.scheduler.shared.event.utils.Serializer;

public class EventPublisher {

    private static Channel publisherChannel;

    static {
        RabbitConnection rabbitConnection = RabbitConnection.getConnection();
        if (rabbitConnection == null) {
            throw new RuntimeException("RabbitMQ ERROR: Event publisher could not obtain RabbitMQ connection");
        }

        publisherChannel = rabbitConnection.getNewChannel();
    }

    public static <E extends Event> void publish(E event) {
        String queueRoutingKey = event.getClass().getName();
        try {
            byte[] serializedEvent = Serializer.serialize(event);
            publisherChannel.basicPublish(RabbitConfig.EXCHANGE_NAME, queueRoutingKey, null, serializedEvent);

            System.out.println("Published event " + event.getClass().getName() + ", routing key: " + queueRoutingKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
