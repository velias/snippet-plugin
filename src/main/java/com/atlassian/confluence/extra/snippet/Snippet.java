package com.atlassian.confluence.extra.snippet;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.SMTPMailServer;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

/**
 * A snippet of external text to be displayed in a Confluence page
 *
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Carlos Villela
 * @version $Revision: 269 $
 */
public class Snippet {
    private final List<String> lines;

    public Snippet(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void writeContent(Writer writer, boolean withLineNumbers, boolean javaDoc) throws IOException
    {
        writeContent(writer, withLineNumbers, javaDoc, SnippetMacro.NOT_SET, SnippetMacro.NOT_SET);
    }

    /**
     * Format the snippet onto the writer.
     * @param writer
     * @param withLineNumbers generate line numbers if {@code true}
     * @param javaDoc parse JavaDoc if {@code true}
     * @param start the start of the range to display
     * @param end the end of the range to display
     * @throws IOException
     */
    public void writeContent(Writer writer, boolean withLineNumbers, boolean javaDoc, int start, int end) throws IOException {
        NumberFormat intFormat = new DecimalFormat(getFormat(lines.size()));
        int ws = countLeadingWhiteSpaceAndJavaDoc(lines, javaDoc);
        final int totalLines = lines.size();
        final int startLine = start > 0 ? start : 1;
        final int endLine = (end > 0 && end <= totalLines) ? end : totalLines;
        for(int i = startLine; i <= endLine; i++)
        {
            final String line = lines.get(i - 1);
            if (withLineNumbers)
            {
                String lineNumber = intFormat.format(i);
                writer.write(lineNumber);
                writer.write(". ");
            }

            String strip = stripEOL(line);
            if (strip.length() > ws)
            {
                writer.write(strip.substring(ws));
            }
            writer.write("\n");
        }
    }

    public static int countLeadingWhiteSpaceAndJavaDoc(List<String> lines, boolean javaDoc) {
        int count = Integer.MAX_VALUE;
        int offsetCount = 0;
        for (String line : lines)
        {
            int tempCount = 0;
            if (javaDoc)
            {
                int index = line.indexOf("* ");
                if (index > -1)
                {
                    offsetCount = index + 2;
                    line = line.substring(offsetCount);
                }
            }
            while (line.startsWith(" ", tempCount))
            {
                tempCount++;
            }

            if (tempCount < count)
            {
                count = tempCount;
            }
        }

        return count + offsetCount;
    }

    public static String stripEOL(String line) {
        line = line.replaceAll("\r", "");
        line = line.replaceAll("\n", "");
        return line;
    }

    static String getFormat(int numberOfLines) {

	    int numberOfDigits = String.valueOf(numberOfLines).length();
        StringBuffer format = new StringBuffer();
        for(int i = 0; i < numberOfDigits; i++) {
            format.append('0');
        }
        return format.toString();
    }
}
