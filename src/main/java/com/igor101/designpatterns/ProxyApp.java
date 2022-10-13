package com.igor101.designpatterns;

/*
Proxy pattern is a class/interface that serves as an interface to something else.

Something else could be anything: network connection, large/expensive object in a memory, file,
something that should be cached/pooled or a resource to which access should be controlled.

It resembles the Decorator pattern a lot.
It also adds functionality to a given class/interface, but the intention is different.
It tries to hide complexity of something that it is proxying or limit access to it
and a client is often not aware that they use it.

In a Decorator case, the intention is to augment base behavior dynamically (at runtime)
and let the client configure and use it.
With the Decorator, the client controls how it gets created, it's a white box for them.
Proxy is given to a client, and they are often not aware that they are using it.
It's a black box for them.

When to use?
When we want to control how clients are interacting with our object or a resource.
 */

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProxyApp {
    public static void main(String[] args) {
        //access to expensive resource that should be pooled/cached
        var connectionPool = new NetworkConnectionPool(2);

        var connection1 = connectionPool.connection();
        System.out.println(connection1.exchange("request from connection1"));

        var connection2 = connectionPool.connection();
        System.out.println(connection2.exchange("request from connection2"));

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                connection1.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        //this block until we release either connection1 or connection2
        var connection3 = connectionPool.connection();
        System.out.println(connection3.exchange("request from connection3"));

        //controlled/limited access to a resource
        var randomNumberGenerator = new LimitedRandomNumberGenerator(1000);
        System.out.println(randomNumberGenerator.next(1, 1000));
        System.out.println(randomNumberGenerator.next(1, 2000));
    }

    interface NetworkConnection {

        String exchange(String request);

        void close();
    }

    static class DefaultNetworkConnection implements NetworkConnection {

        private boolean closed;

        @Override
        public String exchange(String request) {
            if (closed) {
                throw new RuntimeException("Connection is closed!");
            }
            return "%s - Response to request: %s".formatted(LocalDateTime.now(), request);
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    static class NetworkConnectionFromPool implements NetworkConnection {

        private final NetworkConnection connection;
        private final Queue<NetworkConnection> connectionPool;
        private boolean freed;

        public NetworkConnectionFromPool(NetworkConnection connection,
                                         Queue<NetworkConnection> connectionPool) {
            System.out.println("Taking connection from the pool...");
            this.connection = connection;
            this.connectionPool = connectionPool;
        }

        @Override
        public String exchange(String request) {
            if (freed) {
                throw new RuntimeException("Connection was freed!");
            }
            return connection.exchange(request);
        }

        @Override
        public void close() {
            if (!freed) {
                System.out.println("Returning connection to the pool...");
                freed = true;
                connectionPool.add(connection);
            }
        }
    }

    static class NetworkConnectionPool {

        private final BlockingQueue<NetworkConnection> connections;

        public NetworkConnectionPool(int capacity) {
            this.connections = new ArrayBlockingQueue<>(capacity);

            for (int i = 0; i < capacity; i++) {
                this.connections.add(new DefaultNetworkConnection());
            }
        }

        public NetworkConnection connection() {
            try {
                var connection = connections.take();
                return new NetworkConnectionFromPool(connection, connections);
            } catch (Exception e) {
                throw new RuntimeException("Can't get new connection", e);
            }

        }
    }

    interface RandomNumber {
        int next(int from, int to);
    }

    static class RandomNumberGenerator implements RandomNumber {

        private static final Random RANDOM = new Random();

        @Override
        public int next(int from, int to) {
            return from + RANDOM.nextInt(to);
        }
    }

    static class LimitedRandomNumberGenerator implements RandomNumber {

        private final RandomNumberGenerator generator = new RandomNumberGenerator();
        private final int limit;

        public LimitedRandomNumberGenerator(int limit) {
            this.limit = limit;
        }

        @Override
        public int next(int from, int to) {
            try {
                Thread.sleep(limit);
                return generator.next(from, to);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
