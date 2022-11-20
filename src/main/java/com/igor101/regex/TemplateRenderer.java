package com.igor101.regex;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRenderer {

    private static final String VARIABLE_PATTERN_STR = "\\$([a-zA-Z0-9-_.]+)";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_PATTERN_STR);
    private static final Pattern OBJECT_VARIABLE_PATTERN =
            Pattern.compile("(.+?)\\.(.+)");
    private static final Pattern IF_PATTERN =
            Pattern.compile("(?s)\\{\\{\\s*if\\s*%s\\s*}}(.*?)\\{\\{\\s*end\\s*}}"
                    .formatted(VARIABLE_PATTERN_STR));

    private static final Pattern NESTED_IF_PATTERN =
            Pattern.compile("(?s)\\{-\\s*if\\s*%s\\s*-}(.*?)\\{-\\s*end\\s*-}"
                    .formatted(VARIABLE_PATTERN_STR));

    private static final Pattern FOR_PATTERN =
            Pattern.compile("(?s)\\{\\{\\s*for\\s*%s\\s*in\\s*%s\\s*}}[\r\n]*(.*?)\\{\\{\\s*end\\s*}}"
                    .formatted(VARIABLE_PATTERN_STR, VARIABLE_PATTERN_STR));

    private static final String FIRST_VAR = "_first_";
    private static final String LAST_VAR = "_last_";
    private static final String NOT_LAST_VAR = "_not_last_";

    public static String render(String template, Map<String, Object> variables) {
        var withoutIfsTemplate = replaceIfs(template, variables, false);

        var withoutForsTemplate = replaceFors(withoutIfsTemplate, variables);

        return replaceVariables(withoutForsTemplate, variables);
    }

    private static String replaceIfs(String template,
                                     Map<String, Object> variables,
                                     boolean nested) {
        return (nested ? NESTED_IF_PATTERN : IF_PATTERN).matcher(template)
                .replaceAll(m -> {
                    var ifVar = m.group(1);
                    var toRenderBlock = m.group(2).strip();

                    if (Boolean.parseBoolean(variables.getOrDefault(ifVar, false)
                            .toString())) {
                        return replaceVariables(toRenderBlock, variables);
                    }

                    return "";
                });
    }

    private static String replaceVariables(String template,
                                           Map<String, Object> variables) {
        return VARIABLE_PATTERN.matcher(template)
                .replaceAll(m -> {
                    var varKey = m.group(1);

                    var objectMatcher = OBJECT_VARIABLE_PATTERN.matcher(varKey);
                    if (objectMatcher.find()) {
                        return objectVariable(objectMatcher, variables);
                    }


                    return variables.getOrDefault(varKey, "").toString();
                });
    }

    private static String objectVariable(Matcher matcher,
                                         Map<String, Object> variables) {
        var objVar = matcher.group(1);
        var objField = matcher.group(2);
        var objValue = variables.get(objVar);
        if (objValue == null) {
            return "";
        }

        try {
            var field = objValue.getClass().getDeclaredField(objField);
            field.setAccessible(true);
            return field.get(objValue).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String replaceFors(String template,
                                      Map<String, Object> variables) {
        return FOR_PATTERN.matcher(template).replaceAll(m -> {
            var elVar = m.group(1);
            var collVar = m.group(2);
            var toRenderBlock = m.group(3);

            if (variables.getOrDefault(collVar, List.of()) instanceof
                    Collection<?> collValue) {
                return replaceFor(collValue, elVar, toRenderBlock, variables);
            }

            return "";
        });
    }

    private static String replaceFor(Collection<?> collection,
                                     String elVar,
                                     String toRenderBlock,
                                     Map<String, Object> variables) {
        var linesToReplace = new ArrayList<String>();
        var extendedVariables = new HashMap<>(variables);

        var idx = 0;
        for (var e : collection) {
            var first = idx == 0;
            var last = idx == (collection.size() - 1);
            extendedVariables.put(FIRST_VAR, first);
            extendedVariables.put(LAST_VAR, last);
            extendedVariables.put(NOT_LAST_VAR, !last);
            extendedVariables.put(elVar, e);

            var withoutIfsBlock = replaceIfs(toRenderBlock, extendedVariables, true);

            var sanitizedBlock = first ?
                    withoutIfsBlock.strip() : withoutIfsBlock.stripTrailing();

            linesToReplace.add(replaceVariables(sanitizedBlock, extendedVariables));

            idx++;
        }

        return String.join("\n", linesToReplace);
    }
}
