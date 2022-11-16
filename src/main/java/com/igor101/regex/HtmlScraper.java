package com.igor101.regex;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlScraper {

    private static final Pattern META_TAG_PATTERN =
            Pattern.compile("(?s)<meta(.*?)/?>");
    private static final Pattern PROPERTIES_PATTERN =
            Pattern.compile("(\\S+)\\s*=\\s*\"(.+?)\"");
    private static final Pattern SCRIPT_TAG_PATTERN =
            Pattern.compile("(?s)<script(.*?)>(.*?)</script>");

    public static List<Map<String, String>> metaTagsProperties(String html) {
        return META_TAG_PATTERN.matcher(html).results()
                .map(r -> propertiesOfGroup(r.group(1)))
                .toList();
    }

    private static Map<String, String> propertiesOfGroup(String group) {
        return PROPERTIES_PATTERN.matcher(group).results()
                .collect(Collectors.toMap(r -> r.group(1).strip(),
                        r -> r.group(2).strip()));
    }

    public static List<String> matchingPropertyValues(String html,
                                                      String propertyKey,
                                                      String propertyRegex) {
        var propertyPattern = Pattern.compile("%s\\s*=\\s*\"(%s?)\""
                .formatted(propertyKey, propertyRegex));
        return propertyPattern.matcher(html).results()
                .map(r -> r.group(1))
                .toList();
    }

    public static List<ScriptTagData> scriptTagsData(String html) {
        return SCRIPT_TAG_PATTERN.matcher(html).results()
                .map(r -> {
                    var script = r.group(2).strip();
                    var properties = propertiesOfGroup(r.group(1));
                    return new ScriptTagData(script, properties);
                })
                .toList();
    }

}
