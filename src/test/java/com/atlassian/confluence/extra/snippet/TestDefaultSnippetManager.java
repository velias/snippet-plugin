package com.atlassian.confluence.extra.snippet;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;
import com.atlassian.bandana.BandanaManager;

import java.util.Map;
import java.util.HashMap;

public class TestDefaultSnippetManager extends TestCase
{

    DefaultSnippetManager snippetManager;
    Mock mockBandanaManager;


    public TestDefaultSnippetManager(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        snippetManager = new DefaultSnippetManager();
        mockBandanaManager = new Mock(BandanaManager.class);
        snippetManager.setBandanaManager((BandanaManager) mockBandanaManager.proxy());
    }

    public void testResolveUrl() throws Exception
    {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String,String> prefixes = new HashMap<String,String>(){{
            put("foo.bar", "http://server/svn/project/foo/bar");
            put("jim/", "http://server/svn/project/jim/trunk/");
        }};
        settings.put(DefaultSnippetManager.URL_PREFIXES, prefixes);

        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, settings);

        assertEquals("http://server/svn/project/foo/bar/Main.java", snippetManager.resolveUrl("foo.bar.Main").getResolvedUrl());
        assertEquals("http://server/svn/project/jim/trunk/foo/bar.txt", snippetManager.resolveUrl("jim/foo/bar.txt").getResolvedUrl());
        assertEquals("http://server/svn/project/jim/trunk/bar.txt", snippetManager.resolveUrl("jim/bar.txt").getResolvedUrl());
    }

    public void testResolveUrl_SameRoot() throws Exception
    {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String,String> prefixes = new HashMap<String,String>(){{
            put("foo.bar", "http://server/svn/project/foo/bar");
            put("foo.bar.jim", "http://server/svn/project/jim/foo/bar/jim");
        }};
        settings.put(DefaultSnippetManager.URL_PREFIXES, prefixes);

        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, settings);

        assertEquals("http://server/svn/project/foo/bar/Main.java", snippetManager.resolveUrl("foo.bar.Main").getResolvedUrl());
        assertEquals("http://server/svn/project/jim/foo/bar/jim/Main.java", snippetManager.resolveUrl("foo.bar.jim.Main").getResolvedUrl());
    }

    public void testResolveUrl_ShortClassName() throws Exception
    {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String,String> prefixes = new HashMap<String,String>(){{
            put("foo.bar", "http://server/svn/project/foo/bar");
        }};
        settings.put(DefaultSnippetManager.URL_PREFIXES, prefixes);

        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, settings);

        assertEquals("http://server/svn/project/foo/bar/If.java", snippetManager.resolveUrl("foo.bar.If").getResolvedUrl());
    }

    public void testCleanupJavadoc()
    {
        assertEquals("Foo bar", snippetManager.cleanupJavadoc("{@link Foo} bar"));
    }

    public void testCleanupJavadocWithHashes()
    {
        assertEquals("Foo#bar()", snippetManager.cleanupJavadoc("{@link Foo#bar()}"));
        assertEquals("s Foo#bar() s", snippetManager.cleanupJavadoc("s {@link Foo#bar()} s"));
        assertEquals("s #bar() s", snippetManager.cleanupJavadoc("s {@link #bar()} s"));
    }
}
