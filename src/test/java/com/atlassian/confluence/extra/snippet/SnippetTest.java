package com.atlassian.confluence.extra.snippet;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 240 $
 */
public class SnippetTest extends TestCase {
    private final Snippet nineLines = new Snippet(Arrays.asList("one\n", " two\n", "three\n", "four\n", "five\n", "six\n", "seven\n", "eight\n", "nine\n"));

    private final Snippet tenLines = new Snippet(Arrays.asList("one\n", " two\n", "three\n", "four\n", "five\n", "six\n", "seven\n", "eight\n", "nine\n", "ten\n"));

    private final Snippet javaCode = new Snippet(Arrays.asList("    public void foo() {\n", "        ...\n", "    }\n"));

    private final Snippet javaDocJavaCode = new Snippet(Arrays.asList(" *    public void foo() {\n", " *        ...\n", " *    }\n"));

    public void testNineLineSnippetCanBeWrittenWithOneDigitLineNumbers() throws IOException
    {
        StringWriter written = new StringWriter();
        nineLines.writeContent(written, true, false);
        assertEquals("" +
                "1. one\n" +
                "2.  two\n" +
                "3. three\n" +
                "4. four\n" +
                "5. five\n" +
                "6. six\n" +
                "7. seven\n" +
                "8. eight\n" +
                "9. nine\n"
                , written.getBuffer().toString());
    }

    public void testTenLineSnippetCanBeWrittenWithTwoDigitLineNumbers() throws IOException
    {
        StringWriter written = new StringWriter();
        tenLines.writeContent(written, true, false);
        assertEquals("" +
                "01. one\n" +
                "02.  two\n" +
                "03. three\n" +
                "04. four\n" +
                "05. five\n" +
                "06. six\n" +
                "07. seven\n" +
                "08. eight\n" +
                "09. nine\n" +
                "10. ten\n"
                , written.getBuffer().toString());
    }

    public void testSnippetCanBeWrittenWithTwoDigitLineNumbers() throws IOException
    {
        StringWriter written = new StringWriter();
        tenLines.writeContent(written, false, false);
        assertEquals("" +
                "one\n" +
                " two\n" +
                "three\n" +
                "four\n" +
                "five\n" +
                "six\n" +
                "seven\n" +
                "eight\n" +
                "nine\n" +
                "ten\n"
                , written.getBuffer().toString());
    }

    public void testWriteJavaCode() throws IOException
    {
        StringWriter written = new StringWriter();
        javaCode.writeContent(written, false, false);
        assertEquals("" +
                "public void foo() {\n" +
                "    ...\n" +
                "}\n"
                , written.getBuffer().toString());
    }


    public void testWriteJavaCodeWithRange() throws IOException
    {
        StringWriter written = new StringWriter();
        javaCode.writeContent(written, false, false, 1, 2);
        assertEquals("" +
                "public void foo() {\n" +
                "    ...\n"
                , written.getBuffer().toString());
    }

    public void testWriteJavaDocJavaCode() throws IOException
    {
        StringWriter written = new StringWriter();
        javaDocJavaCode.writeContent(written, false, true);
        assertEquals("" +
                "public void foo() {\n" +
                "    ...\n" +
                "}\n"
                , written.getBuffer().toString());
    }

    public void testStripLeadingWhitespace()
    {
        ArrayList<String> source = new ArrayList<String>();
        source.add("    public void foo() {");
        source.add("        ...");
        source.add("    }");

        assertEquals(4, Snippet.countLeadingWhiteSpaceAndJavaDoc(source, false));

        source.clear();
        source.add("public void foo() {");
        source.add("    ...");
        source.add("}");

        assertEquals(0, Snippet.countLeadingWhiteSpaceAndJavaDoc(source, false));

        source.clear();
        source.add("    public void foo() {");
        source.add("    ...");
        source.add("}");

        assertEquals(0, Snippet.countLeadingWhiteSpaceAndJavaDoc(source, false));
    }

    public void testGetFormat_short()
    {
        assertEquals("0", Snippet.getFormat(1));
    }

    public void testGetFormat_long()
    {
        assertEquals("000", Snippet.getFormat(100));
    }

    public void testGetFormat()
    {
        assertEquals("00", Snippet.getFormat(10));
    }
}