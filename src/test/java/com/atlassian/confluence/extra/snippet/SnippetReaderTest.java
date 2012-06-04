package com.atlassian.confluence.extra.snippet;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class SnippetReaderTest extends TestCase {

    static final String EOL = System.getProperty("line.separator");
    private SnippetReader snippetReader;

    protected void setUp() throws Exception {
        snippetReader = new SnippetReader(getResource("SnippetTestFile.java"));
    }

    public void testStartAndEndChecksAreRobustAndHandlesXmlJavaAndBash() {
        assertTrue(snippetReader.isStart("goodId", "\t  // START SNIPPET: goodId"));
        assertFalse(snippetReader.isStart("goodId", "\t  // END SNIPPET: goodId"));
        assertFalse(snippetReader.isStart("badId", "\t  // START SNIPPET: goodId"));

        assertTrue(snippetReader.isEnd("goodId", "\t  // END SNIPPET: goodId"));
        assertFalse(snippetReader.isEnd("goodId", "\t  // START SNIPPET: goodId"));

        assertTrue(snippetReader.isDemarcator("xmlSnippet", "START", "<!-- START SNIPPET: xmlSnippet -->"));
        assertTrue(snippetReader.isDemarcator("bashSnippet", "START", "# START SNIPPET: bashSnippet"));
    }

    public void testStartEndAndSnippetAreNotCaseSensitive() {
        assertTrue(snippetReader.isDemarcator("id", "START", "// start snippet: id"));
        assertTrue(snippetReader.isDemarcator("id", "END", "// end snippet: id"));
    }

    public void testSnippetIdIsCaseSensitive() {
        assertFalse(snippetReader.isDemarcator("ID", "START", "// start snippet: id"));
    }

    public void testColonIsOptional() {
        assertTrue(snippetReader.isDemarcator("id", "START", "// start snippet id"));
    }

    public void testYouCanHaveHowManySpacesYouLike() {
        assertTrue(snippetReader.isDemarcator("id", "START", "// start snippet  \t  id"));
    }

    public void testCanReadSnippetsWithSeveralLinesAndNestedSnippet() throws IOException {
        SnippetReader snippetReader = new SnippetReader(getResource("SnippetTestFile.java"));
        Snippet snippet = snippetReader.readSnippet("multilineSnippet");
        StringWriter written = new StringWriter();
        snippet.writeContent(written, false, false);
        assertEquals(
                     "assertEquals(2, 1 + 1);\n" +
                     "if(true) {\n" +
                     "    assertEquals(6, 2 * 3);\n" +
                     "}\n" +
                     "assertEquals(2, 4 / 2);\n",
                     written.getBuffer().toString());
    }

    public void testCanReadSnippetsWithSnippetInPackage() throws IOException {
        SnippetReader snippetReader = new SnippetReader(getResource("SnippetTestFile.java"));
        Snippet snippet = snippetReader.readSnippet("all");
        StringWriter written = new StringWriter();
        snippet.writeContent(written, false, false);
        assertTrue(written.getBuffer().toString().startsWith("package snippet;"));
    }

    public void testMinIndentFindsTheNumberOfSpacesOfTheLeastIndentedLine() {
        List list = new ArrayList();
        list.add("        assertEquals(2, 1 + 1);");
        assertEquals(8, snippetReader.minIndent(list));
        list.add("  2");
        assertEquals(2, snippetReader.minIndent(list));
        list.add(" 1");
        assertEquals(1, snippetReader.minIndent(list));
        list.add("   3");
        assertEquals(1, snippetReader.minIndent(list));
    }

    public void testIndentReturnsTheNumberOfSpacesTheLineIsIndented() {
        assertEquals(1, snippetReader.indent(" 1"));
        assertEquals(2, snippetReader.indent("  2"));
    }

    static URL getResource(String resource) {
        URL result = SnippetReaderTest.class.getResource("/snippet/" + resource);
        assertNotNull(resource + " resource not included in classpath, " +
                      "please set up your IDE or build-script to copy all resources when compiling.",
                      result);
        return result;
    }

    public void testUsesCorrectIndentationOnASnippetWithBlankLines() throws Exception {
        String expected =
            "assertEquals(2, 1 + 1);\n" +
            "\n" +
            "assertEquals(3, 1 + 2);\n";

        Snippet       snippet       = snippetReader.readSnippet("blanklines");
        StringWriter  written       = new StringWriter();
        snippet.writeContent(written, false, false);
        assertEquals(expected, written.toString());
    }

    public void TODOtestThrowsExceptionIfSnippetNotFound() {

    }

    public void TODOtestRemovesAllSnippetLineDemarcatorsInSnippet() {

    }

}
