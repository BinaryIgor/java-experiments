package com.igor101;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Locks {
    public static void main(String[] args) throws Exception {
        var random = new Random();
        random.nextInt();

        var lock = new ReentrantLock();

        System.out.println("Locking for the first time...");
        lock.lock();
        System.out.println("Locking for the second time...");
        lock.lock();

        Thread.startVirtualThread(() -> {
            System.out.println("Would like to get this lock...");
            lock.lock();
            System.out.println("Got the lock!");
        });

        Thread.sleep(1000);
        lock.unlock();
        lock.unlock();
    }

    static void add(int a, int b) {

    }

    static void add(String a, String b) {
        
    }
}
