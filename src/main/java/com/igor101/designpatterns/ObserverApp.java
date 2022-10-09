package com.igor101.designpatterns;

/*
Observer pattern (also called a Listener)
allows us to observe changes of another object data/state,
listen to events emitted by this object and react accordingly.

When somebody talks about Event-driven programming they often simply mean this pattern
and the whole Publish-subscribe pattern is just a generalization of this idea.

The main advantage of this pattern is loosely coupled communication between objects.
Observer doesn't need to know anything about the source object of the data/event
that it is interested in and vice versa, the Source doesn't need to know anything
about the Observer.

The main disadvantage is that code is hard to test and understand,
because we have no idea who, at runtime, will listen to what events/changes.

The usual structure is something like this:

class Source (one that emits changes) {
    {
       //some code block
       for (var o: observers) {
         o.onChange(data);
       }
    }
}

interface Observer<T> {
  void onChange(T data);
}

 */

import java.util.LinkedList;
import java.util.List;

public class ObserverApp {
    public static void main(String[] args) {
        //changed data/state
        var user = new ActiveUser("user1", "user1@email.com");

        var firstObserver = new ActiveUserObserver() {
            @Override
            public void onNameChanged(String name) {
                System.out.println("First observer, new name = " + name);
            }

            @Override
            public void onEmailChanged(String email) {
                System.out.println("First observer, new email = " + email);
            }
        };

        var secondObserver = new ActiveUserObserver() {
            @Override
            public void onNameChanged(String name) {
                System.out.println("Second observer, new name = " + name);
            }

            @Override
            public void onEmailChanged(String email) {
                System.out.println("Second observer, new email = " + email);
            }
        };

        user.addObserver(firstObserver);
        user.addObserver(secondObserver);

        user.changeName("user2");
        user.changeEmail("user2@email.com");

        user.removeObserver(firstObserver);
        user.removeObserver(secondObserver);

        //event
        var button = new Button();

        button.setOnClickListener(() -> System.out.println("Button has been clicked!"));

        button.click();

        button.setOnClickListener(null);
    }

    static class ActiveUser {
        private final List<ActiveUserObserver> observers = new LinkedList<>();
        private String name;
        private String email;

        public ActiveUser(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public void changeName(String newName) {
            name = newName;
            observers.forEach(o -> o.onNameChanged(name));
        }

        public void changeEmail(String newEmail) {
            email = newEmail;
            observers.forEach(o -> o.onEmailChanged(email));
        }

        public void addObserver(ActiveUserObserver observer) {
            observers.add(observer);
        }

        public void removeObserver(ActiveUserObserver observer) {
            observers.remove(observer);
        }
    }

    interface ActiveUserObserver {
        void onNameChanged(String name);

        void onEmailChanged(String email);
    }

    static class Button {

        private Runnable onClickListener;

        public void click() {
            if (onClickListener != null) {
                onClickListener.run();
            }
        }

        public void setOnClickListener(Runnable listener) {
            onClickListener = listener;
        }
    }
}
