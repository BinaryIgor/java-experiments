package com.igor101.designpatterns;

import java.util.function.Supplier;

/*
Decorator pattern allows to augment the behavior of the object/function without modifying them.

In the object-oriented case, an object implements some interface and holds a reference to another object,
which also implements the same interface.

Decorator object then delegates a function call/calls to a held reference and
adds new behavior before or/and after function call/calls of a held interface.

Similar behavior can be achieved using just functions.
 */
public class DecoratorApp {
    public static void main(String[] args) {
        var objectOrientedStyle = false;

        if (objectOrientedStyle) {
            //object-oriented style
            var basePage = new InMemoryPage("Some page");

            System.out.println(basePage.render());

            //Logging decorator
            var loggingPage = new LoggingPage(basePage);
            System.out.println(loggingPage.render());

            //Throttling decorator
            var throttlingPage = new ThrottlingPage(basePage, 1000);
            System.out.println(throttlingPage.render());

            //Logging and then throttling decorator
            var loggingThrottlingPage = new LoggingPage(throttlingPage);
            System.out.println(loggingThrottlingPage.render());
        } else {
            //functional style
            Supplier<String> basePage = DecoratorApp::renderPage;

            System.out.println(basePage.get());

            Supplier<String> loggingPage = () -> renderPageLogging(basePage);
            System.out.println(loggingPage.get());

            Supplier<String> throttlingPage = () -> renderPageThrottling(basePage, 1000);
            System.out.println(throttlingPage.get());

            Supplier<String> loggingThrottlingPage = () -> renderPageLogging(throttlingPage);
            System.out.println(loggingThrottlingPage.get());
        }
    }

    static String renderPage() {
        return "Some page";
    }

    static String renderPageLogging(Supplier<String> renderPage) {
        System.out.println();
        System.out.println("About to render a page...");
        var rendered = renderPage.get();
        System.out.println("Page rendered");
        return rendered;
    }

    static String renderPageThrottling(Supplier<String> renderPage, long delay) {
        try {
            Thread.sleep(delay);
            return renderPage.get();
        } catch (Exception e) {
            throw new RuntimeException("Problem while throttling page render", e);
        }
    }

    interface Page {
        String render();
    }

    static class InMemoryPage implements Page {

        private final String page;

        public InMemoryPage(String page) {
            this.page = page;
        }

        @Override
        public String render() {
            return page;
        }
    }

    static class LoggingPage implements Page {

        private final Page page;

        public LoggingPage(Page page) {
            this.page = page;
        }

        @Override
        public String render() {
            System.out.println();
            System.out.println("About to render a page...");
            var rendered = page.render();
            System.out.println("Page rendered");
            return rendered;
        }
    }

    static class ThrottlingPage implements Page {

        private final Page page;
        private final long delay;

        public ThrottlingPage(Page page, long delay) {
            this.page = page;
            this.delay = delay;
        }

        @Override
        public String render() {
            try {
                Thread.sleep(delay);
                return page.render();
            } catch (Exception e) {
                throw new RuntimeException("Problem while throttling page render", e);
            }
        }
    }
}
