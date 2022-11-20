package com.igor101.regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TemplateRendererTest {

    private static final String VARIABLES_TEMPLATE = """
            Hello $some_var1!

            Some long and probably boring text...

            Best,
            $some_var2
            """.strip();

    private static final String RENDERED_VARIABLES_TEMPLATE = """
            Hello nice-user!
                        
            Some long and probably boring text...
                        
            Best,
            nicer-user
            """.strip();

    private static final String IF_TEMPLATE = """
            <body>
                {{ if $some-var }}
                <div>$to_show_var is being shown!</div>
                {{ end }}
            </body>
            """.strip();

    private static final String IF_TRUE_RENDERED_TEMPLATE = """
            <body>
                <div>22 is being shown!</div>
            </body>
            """.strip();

    private static final String IF_FALSE_RENDERED_TEMPLATE = """
            <body>
            \s\s\s\s
            </body>
            """.strip();

    private static final String FOR_TEMPLATE = """
            <body>
                {{ for $e in $items }}
                <p>{- if $_first_ -}!!!{- end -}$e - $suffix</p>
                {- if $_last_ -}
                <p>Last one!</p>
                {- end -}
                {{ end }}
            </body>
            """.strip();

    private static final String FOR_RENDERED_TEMPLATE = """
            <body>
                <p>!!!first var - addon</p>
                <p>2 var - addon</p>
                <p>last_one - addon</p>
                <p>Last one!</p>
            </body>
            """.strip();

    private static final String FOR_OBJECTS_TEMPLATE = """
            INSERT INTO user (id, name) VALUES
                {{ for $e in $items }}
                ($e.id, '$e.name'){- if $_not_last_ -},{- end -}
                {{ end }};
            """.strip();

    private static final String FOR_OBJECTS_RENDERED_TEMPLATE = """
            INSERT INTO user (id, name) VALUES
                (22, 'Second Object'),
                (101, 'One Hundredth Object'),
                (1111, 'Last Object');
            """.strip();


    @Test
    void shouldRenderTemplateWithVariables() {
        Assertions.assertEquals(RENDERED_VARIABLES_TEMPLATE,
                TemplateRenderer.render(VARIABLES_TEMPLATE,
                        Map.of("some_var1", "nice-user",
                                "some_var2", "nicer-user")));
    }

    @Test
    void shouldRenderTemplateWithIfTrue() {
        Assertions.assertEquals(IF_TRUE_RENDERED_TEMPLATE,
                TemplateRenderer.render(IF_TEMPLATE,
                        Map.of("some-var", true,
                                "to_show_var", 22)));
    }

    @Test
    void shouldRenderTemplateWithIfFalse() {
        Assertions.assertEquals(IF_FALSE_RENDERED_TEMPLATE,
                TemplateRenderer.render(IF_TEMPLATE,
                        Map.of("some-var", false)));
    }

    @Test
    void shouldRenderTemplateWithFor() {
        Assertions.assertEquals(FOR_RENDERED_TEMPLATE,
                TemplateRenderer.render(FOR_TEMPLATE,
                        Map.of("items",
                                List.of("first var", "2 var", "last_one"),
                                "suffix", "addon")));
    }

    @Test
    void shouldRenderTemplateWithForObjects() {
        Assertions.assertEquals(FOR_OBJECTS_RENDERED_TEMPLATE,
                TemplateRenderer.render(FOR_OBJECTS_TEMPLATE,
                        Map.of("items",
                                List.of(new SomeObject(22, "Second Object"),
                                        new SomeObject(101, "One Hundredth Object"),
                                        new SomeObject(1111, "Last Object")))));
    }

    private record SomeObject(int id, String name) {
    }
}
