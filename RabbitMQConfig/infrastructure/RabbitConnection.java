package com.scheduler.shared.event.infrastructure;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitConnection {

    private final Connection tcpConnection;

    private static RabbitConnection instance = null;

    public static synchronized RabbitConnection getConnection() {
        if (instance == null) {
            instance = new RabbitConnection();
        }
        return instance;
    }

    private RabbitConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitConfig.HOST);

        try {
            tcpConnection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "RabbitMQ ERROR: Could not establish connection to RabbitMQ server because of: " + e.getCause());
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "RabbitMQ ERROR: Could not establish connection to RabbitMQ server because of: " + e.getCause());
        }
    }

    public Channel getNewChannel() {
        Channel newChannel = null;
        try {
            newChannel = tcpConnection.createChannel();
            newChannel.exchangeDeclare(RabbitConfig.EXCHANGE_NAME, RabbitConfig.EXHANGE_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "RabbitMQ ERROR: Could not create new channel to actual connection because of: " + e.getCause());
        }

        return newChannel;
    }

    public void createQueue(Channel exchangeChannel, String queueName, Set<String> routingKeys) {
        try {
            exchangeChannel.queueDeclare(queueName, true, false, false, null);
            for (String key : routingKeys) {
                exchangeChannel.queueBind(queueName, RabbitConfig.EXCHANGE_NAME, key);
            }

            System.out.println("Created queue: " + queueName + " keys: " + routingKeys.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "RabbitMQ ERROR: Could not create new queue on given channel because of: " + e.getCause());
        }
    }

}
