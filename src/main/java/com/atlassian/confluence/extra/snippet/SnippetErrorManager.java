package com.atlassian.confluence.extra.snippet;

import java.util.List;

/**
 * Manages snippet errors
 */
public interface SnippetErrorManager
{
    /**
     * Clears all errors for a given space
     *
     * @param spaceKey The space key
     */
    void clear(String spaceKey);

    /**
     * Clears all errors for all spaces
     */
    void clear();

    /**
     * Gets a list of snippet errors for the given space
     *
     * @param spaceKey The space key
     * @return A list of SnippetError objects
     */
    List<SnippetError> get(String spaceKey);

    /**
     * Adds a snippet error
     *
     * @param error A snippet error
     */
    void add(SnippetError error);
}
