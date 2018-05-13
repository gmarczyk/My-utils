package com.scheduler.shared.event.domain.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import com.scheduler.shared.event.infrastructure.RabbitConnection;
import com.scheduler.shared.event.utils.Serializer;

public abstract class Handler {

    public Handler() {
        System.out.println("Created handler: " + this.getClass().getName());
    }

    public void runOnQueue(String queueName) {
        Channel separateChannelToRabbitConnection = RabbitConnection.getConnection().getNewChannel();
        Consumer consumer = createConsumerOnConnection(separateChannelToRabbitConnection);

        try {
            separateChannelToRabbitConnection.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Consumer createConsumerOnConnection(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {

                try {
                    Handler.this.handle((Event) Serializer.deserialize(body));
                } catch (ClassNotFoundException e) {
                    System.out.println("Could not deserialize class of the event");
                    e.printStackTrace();
                }
            }
        };
    }

    protected <E extends Event> void handle(E toHandle) {
        try {
            Method method = this.getClass().getDeclaredMethod("handle", new Class[] { toHandle.getClass() });
            method.invoke(this, toHandle);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("Event handling method is not implemented or is being routed to wrong handler!");
            e.printStackTrace();
        }
    }
}
