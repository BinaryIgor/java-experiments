package com.igor101.designpatterns;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/*
Bridge pattern allows us to implement two layers of abstraction that vary independently,
but work together.

Usually we have abstraction A (class/interface) that uses abstraction B (class/interface).

They both change independently of each other, they are decoupled.

It is often confused with the Adapter pattern, but the intention is different.

Adapter pattern adapts existing abstraction to a different interface,
whereas Bridge separates two levels of abstraction, because they change for a different reason.
 */
public class BridgeApp {
    public static void main(String[] args) {
        var templateStore = new InMemoryTemplateStore();
//        var templateStore = new LocalFileTemplateStore("/tmp");

        var user = new User(1, "Igor");

        var htmlTemplate = new HtmlUserTemplate(templateStore);
        htmlTemplate.store(user);

        var markdownTemplate = new MarkdownUserTemplate(templateStore);
        markdownTemplate.store(user);

        System.out.println("Html user template:");
        System.out.println(htmlTemplate.get(user.id()));

        System.out.println("Markdown user template:");
        System.out.println(markdownTemplate.get(user.id()));
    }

    record User(long id, String name) { }

    interface TemplateStore {

        void store(String key, String template);

        String get(String key);
    }

    //This is our bridge, which uses TemplateStore
    interface UserTemplate {

        void store(User user);

        String get(long userId);
    }

    static class InMemoryTemplateStore implements TemplateStore {

        private final Map<String, String> templates = new HashMap<>();

        @Override
        public void store(String key, String template) {
            templates.put(key, template);
        }

        @Override
        public String get(String key) {
            return templates.get(key);
        }
    }

    static class LocalFileTemplateStore implements TemplateStore {

        private final String templatesDir;

        public LocalFileTemplateStore(String templatesDir) {
            this.templatesDir = templatesDir;
        }

        @Override
        public void store(String key, String template) {
            try {
                Files.writeString(Path.of(templatesDir, key), template);
            } catch (Exception e) {
                throw new RuntimeException("Problem while storing %s template...".formatted(key), e);
            }
        }

        @Override
        public String get(String key) {
           try {
               return Files.readString(Path.of(templatesDir, key));
           } catch (Exception e) {
               throw new RuntimeException("Problem while loading %s template".formatted(key), e);
           }
        }
    }

    static class HtmlUserTemplate implements UserTemplate {

        private final TemplateStore store;

        public HtmlUserTemplate(TemplateStore store) {
            this.store = store;
        }

        @Override
        public void store(User user) {
            var template = """
                    <h1>Name: %s</h1>
                    <h2>Id: %d</h2
                    """
                    .formatted(user.name(), user.id())
                    .strip();

            store.store(userKey(user.id()), template);
        }

        private String userKey(long id) {
            return "user-%d.html".formatted(id);
        }

        @Override
        public String get(long userId) {
            return store.get(userKey(userId));
        }
    }

    static class MarkdownUserTemplate implements UserTemplate {

        private final TemplateStore store;

        public MarkdownUserTemplate(TemplateStore store) {
            this.store = store;
        }

        @Override
        public void store(User user) {
            var template = """
                    # Name: %s
                    ## Id: %d
                    """
                    .formatted(user.name(), user.id())
                    .strip();

            store.store(userKey(user.id()), template);
        }

        private String userKey(long id) {
            return "user-%d.md".formatted(id);
        }

        @Override
        public String get(long userId) {
            return store.get(userKey(userId));
        }
    }
}
