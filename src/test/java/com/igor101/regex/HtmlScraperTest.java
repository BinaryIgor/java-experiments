package com.igor101.regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class HtmlScraperTest {

    private static final String HTML;

    static {
        try (var is = HtmlScraperTest.class.getResourceAsStream(
                "/regex/to-scrape.html")) {
            HTML = new String(is.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Can't load HTML...", e);
        }
    }

    @Test
    void shouldReturnMetaTagsProperties() {
        var expectedProperties = List.of(
                Map.of("charset", "UTF-8"),
                Map.of("name", "viewport",
                        "content", "width=device-width, initial-scale=1"),
                Map.of("property", "description",
                        "content", "Personal site of Igor Roztropiński"),
                Map.of("name", "author",
                        "content", "Igor Roztropiński")
        );

        Assertions.assertEquals(expectedProperties,
                HtmlScraper.metaTagsProperties(HTML));
    }

    @Test
    void shouldReturnAllHrefValues() {
        var expectedHrefValues = List.of("/css/styles_1666107074.css",
                "home", "about", "skills", "experience", "code",
                "https://youtube.com",
                "home", "about", "skills", "experience", "code");

        Assertions.assertEquals(expectedHrefValues,
                HtmlScraper.matchingPropertyValues(HTML,
                        "href", ".*"));
    }

    @Test
    void shouldReturnAbsoluteHrefValues() {
        var expectedHrefValues = List.of("https://youtube.com");

        Assertions.assertEquals(expectedHrefValues,
                HtmlScraper.matchingPropertyValues(HTML,
                        "href", "http.*"));
    }

    @Test
    void shouldReturnAllPropertyValuesMatchingPattern() {
        var expectedPropertyValues = List.of("top-nav fade-in hidden",
                "hidden-link",
                "top-nav-mobile fade-in hidden",
                "hidden");

        Assertions.assertEquals(expectedPropertyValues,
                HtmlScraper.matchingPropertyValues(HTML, "class",
                        "top-.*|hidden.*"));
    }

    @Test
    void shouldReturnAllScriptTagsData() {
        var expectedScriptTagsData = List.of(
                new ScriptTagData("""
                         (function () {
                                    const KEY = "MODE";
                                    const LIGHT_MODE = 'light';
                                    const DARK_MODE = 'dark';
                                                
                                    const currentMode = () => {
                                        const mode = localStorage.getItem(KEY);
                                        if (mode) {
                                            return mode;
                                        }
                                        return DARK_MODE;
                                    }
                                                
                                    const setDarkMode = () => document.documentElement.classList.add(DARK_MODE);
                                                
                                    const setLightMode = () => document.documentElement.classList.remove(DARK_MODE);
                                                
                                    const mode = currentMode();
                                    if (mode == LIGHT_MODE) {
                                        setLightMode();
                                    } else {
                                        setDarkMode();
                                    }
                                })();
                        """.strip(), Map.of()),
                new ScriptTagData("""
                         const URL_PROPERTY_REGEX = /url\\((.*)\\)/;
                                                
                                function setupMode() {
                                    const KEY = "MODE";
                                                
                                    const LIGHT_MODE = 'light';
                                    const DARK_MODE = 'dark';
                                    const DARK_MODE_ICON = "0";
                                    const LIGHT_MODE_ICON = "1";
                                                
                                    const DISPLAY_MOBILE_NAV_CLASS = "display";
                                                
                                    const topNav = document.querySelector(".top-nav");
                                    const themeMode = document.querySelector('.theme-mode');
                                    const topNavMobile = document.querySelector('.top-nav-mobile');
                                    const navMobile = document.getElementById('nav-mobile-menu');
                                    const navMobileClose = document.querySelector('#nav-mobile-close');
                                                
                                    const currentMode = () => {
                                        const mode = localStorage.getItem(KEY);
                                        if (mode) {
                                            return mode;
                                        }
                                        return DARK_MODE;
                                    }
                                                
                                    const setDarkMode = () => {
                                        document.documentElement.classList.add(DARK_MODE);
                                        localStorage.setItem(KEY, DARK_MODE)
                                        themeMode.textContent = DARK_MODE_ICON;
                                    };
                                                
                                    const setLightMode = () => {
                                        document.documentElement.classList.remove(DARK_MODE);
                                        localStorage.setItem(KEY, LIGHT_MODE)
                                        themeMode.textContent = LIGHT_MODE_ICON;
                                    };
                                                
                                    if (currentMode() == LIGHT_MODE) {
                                        setLightMode();
                                    } else {
                                        setDarkMode();
                                    }
                                                
                                    themeMode.addEventListener('click', e => {
                                        if (currentMode() == LIGHT_MODE) {
                                            setDarkMode();
                                        } else {
                                            setLightMode();
                                        }
                                        document.dispatchEvent(new Event("themeChange"));
                                    });
                                                
                                    navMobile.addEventListener('click', () => topNavMobile.classList.add(DISPLAY_MOBILE_NAV_CLASS));
                                                
                                    topNav.addEventListener("click", e => e.stopPropagation());
                                                
                                    navMobileClose.addEventListener('click', () => topNavMobile.classList.remove(DISPLAY_MOBILE_NAV_CLASS));
                                }
                                                
                                setupMode();
                        """.strip(),Map.of() ),
                new ScriptTagData("", Map.of("type", "module",
                        "src", "/js/rain-app_1666107074.js")),
                new ScriptTagData("", Map.of("type", "module",
                        "src", "/js/app_1666107074.js")));

        Assertions.assertEquals(expectedScriptTagsData,
                HtmlScraper.scriptTagsData(HTML));
    }
}
