package com.atlassian.confluence.extra.snippet;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.pages.Page;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;
import junit.framework.TestCase;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SnippetMacroTest extends TestCase
{

    static final String EOL = System.getProperty("line.separator");

    private SnippetMacro snippetMacro;
    private Map parameters;
    private Mock mockSnippetManager;
    private Mock mockSnippetErrorManager;

    protected void setUp() throws Exception
    {
        snippetMacro = new SnippetMacro();
        snippetMacro.setSubRenderer(new MockSubRenderer());
        parameters = new HashMap();
        mockSnippetManager = new Mock(SnippetManager.class);
        mockSnippetErrorManager = new Mock(SnippetErrorManager.class);
        snippetMacro.setSnippetManager((SnippetManager) mockSnippetManager.proxy());
        snippetMacro.setSnippetErrorManager((SnippetErrorManager) mockSnippetErrorManager.proxy());

    }

    protected void tearDown() {
        mockSnippetManager = null;
    }

    /*public void testExecuteWritesSnippetFromParameter() throws IOException
    {
        parameters.put("id", "mySnippetId");
        parameters.put("url", snippetUrl().toExternalForm());
        assertEquals("assertEquals(2, 1 + 1);\n", executeMacro());
    }
    */

    private URL snippetUrl()
    {
        return SnippetReaderTest.getResource("SnippetReaderTest.java");
    }

    private String executeMacro()
    {

        try
        {
            Page page = new Page();
            page.setTitle("My page");
            String url = (String) parameters.get("url");
            mockSnippetManager.expectAndReturn("resolveUrl", C.args(C.eq(url)), new ResolvedUrl("", url, url, null));
            //mockSnippetManager.expectAndReturn("getSnippet")
            return snippetMacro.execute(parameters, "", new PageContext(page));
        }
        catch (MacroException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    /*
    public void testIfYouPassInLangItGetsWrappedInCodeMacro() throws IOException
    {
        parameters.put("lang", "java");
        parameters.put("id", "multilineSnippet");
        parameters.put("url", snippetUrl().toExternalForm());
        assertEquals("{code:lang=java}\n" +
                "assertEquals(2, 1 + 1);\n" +
                "if(true) {\n" +
                "    assertEquals(6, 2 * 3);\n" +
                "}\n" +
                "assertEquals(2, 4 / 2);\n" +
                "{code}\n",
                executeMacro());
    }

    public void TODOtestQuotesStartCurlyBracket()
    {

    }
    */

    /*public void testReturnsCachedSnippetIfSnippetIsInCache() throws IOException
    {
        URL url = new URL("file:cachedUrl");
        String id = "snippet id";
        Snippet snippet = new Snippet(Arrays.asList(new String[]{"cached content"}));
        snippetManager.cacheSnippet(url, id, snippet);
        assertSame(snippet, snippetManager.getSnippet(url, id, null));
    }

    public void testRemovesSnippetFromCacheWhenTimedOut() throws IOException
    {
        Snippet snippetOne = snippetManager.getSnippet(snippetUrl(), "mySnippetId", null);
        Snippet snippetTwo = snippetManager.getSnippet(snippetUrl(), "mySnippetId", null);
        assertSame(snippetOne, snippetTwo);

        snippetMacro.setCacheTimeout(0);
        Snippet snippetThree = snippetManager.getSnippet(snippetUrl(), "mySnippetId", null);
        assertNotSame(snippetOne, snippetThree);
    }
*/

    public void TODOtestCachesResults()
    {

    }

    /*
    public void testPutsInLineNumbersIfSpecified() throws IOException
    {
        parameters.put("lang", "java");
        parameters.put("id", "multilineSnippet");
        parameters.put("linenumbers", "true");
        parameters.put("url", snippetUrl().toExternalForm());
        assertEquals("{code:lang=java}\n" +
                "1. assertEquals(2, 1 + 1);\n" +
                "2. if(true) {\n" +
                "3.     assertEquals(6, 2 * 3);\n" +
                "4. }\n" +
                "5. assertEquals(2, 4 / 2);\n" +
                "{code}\n",
                executeMacro());
    }
    */

    // Need to fix the tests in this class
    public void testNothing() {}

    public void TODOtestUnindentsIfSpecified()
    {
    }

    public void TODOtestConvertsTabsToSpacesIfSpecified()
    {

    }

    public void TODOtestDisplaysRedErrorThingIfSnippetNotFound()
    {

    }

    public void TODOtestDisplaysRedErrorThingIfUrlIsInvalid()
    {

    }

    public void testDecodeJavadoc()
    {
        assertEquals("<foo>", snippetMacro.decodeJavadoc("&lt;foo&gt;"));
        assertEquals("<foo>bar</foo>", snippetMacro.decodeJavadoc("&lt;foo&gt;bar&lt;/foo&gt;"));
        assertEquals("hi \"bob\"", snippetMacro.decodeJavadoc("hi &quot;bob&quot;"));
    }

    /**
     * Mock renderer that skips confluence renderering so that this test class can perform tests over raw wiki text
     */
    private class MockSubRenderer implements SubRenderer
    {
        public String render(String wiki, RenderContext renderContext, RenderMode newRenderMode)
        {
            return wiki;
        }

        public String render(String wiki, RenderContext renderContext)
        {
            return wiki;
        }

        public String renderAsText(String string, RenderContext renderContext) {
            return string;
        }

        public String getRendererType() {
            return "simple";
        }
    }
}
