package com.atlassian.confluence.extra.snippet;

import com.atlassian.renderer.RenderContext;

import java.net.URL;
import java.io.IOException;
import java.util.Map;

/**
 * Loads and caches snippets
 */
public interface SnippetManager
{
    String NOTIFICATION_EMAIL = "notificationEmail";
    String EMAIL_SUBJECT_TEMPLATE = "emailSubjectTemplate";
    String EMAIL_BODY_TEMPLATE = "emailBodyTemplate";
    String TOGGLE = "toggle";
    String CACHE_TIMEOUT = "cacheTimeout";
    String URL_PREFIXES = "urlPrefixes";
    String URL_CREDENTIALS = "urlCredentials";

    /**
     * Gets a specific snippet within a url
     *
     * @param url The url to load containing the snippet
     * @param id The id of the snippet to load, 'all' to load the whole file
     * @return The snippet
     * @throws IOException If the snippet cannot be located
     */
    Snippet getSnippet(URL url, Credentials credentials, String id) throws IOException;

    /**
     * Processes a Javadoc-encoded snippet
     *
     * @param msg The message
     */
    String cleanupJavadoc(String msg);

    /**
     * Whether the cache toggle should be shown when displaying the snippet or not
     *
     * @return True if it should be shown, false otherwise
     */
    boolean shouldShowToggle();

    /**
     * Resolves the snippet url using allowed url prefixes
     *
     * @param urlParam The url specified in the macro
     * @return The full url of the source document
     */
    ResolvedUrl resolveUrl(String urlParam);

    /**
     * Gets the plugin settings
     */
    Map<String, Object> getSettings();

    /**
     * Saves the plugin settings
     * @param settings A map of settings
     */
    void saveSettings(Map<String, Object> settings);

    /**
     * Removes a snippet from the cache
     *
     * @param url The snippet url
     * @param id The snippet id
     */
    void removeFromCache(URL url, String id);

    /**
     * Clears all snippets from the cache
     */
    void clearCache();
}
