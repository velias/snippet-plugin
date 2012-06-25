package com.atlassian.confluence.extra.snippet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Reads a snippet from a URL and processes the text
 * 
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author Carlos Villela
 * @author Nick Sieger
 * @version $Revision: 238 $
 */
public class SnippetReader {
    private final URL source;
    private final String username;
    private final String password;
    private static final Pattern LEADING_WHITE = Pattern.compile("^(\\s*)\\S.*");
    private static final Protocol MY_HTTPS = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
    
    private static final int HTTP_TIMEOUT = 2000;

    private static final String START_TOKEN = "START SNIPPET";
    private static final String END_TOKEN = "END SNIPPET";

    public SnippetReader(URL source) {
        this(source, null, null);
    }

    public SnippetReader(URL source, String username, String password) {
        this.source = source;
        this.username = username;
        this.password = password;
    }

    public Snippet readSnippet(String snippetId) throws IOException {
        List<String> lines = readLines(snippetId);
        List<String> snippetLines = new ArrayList<String>();
        int minIndent = minIndent(lines);
        for (String line : lines)
        {
            if (!isStart(null, line) && !isEnd(null, line))
            {
                snippetLines.add(line.length() >= minIndent ? line.substring(minIndent) : line);
            }
        }
        return new Snippet(snippetLines);
    }

    int minIndent(List<String> lines) {
        int minIndent = Integer.MAX_VALUE;
        for (String line : lines)
        {
            minIndent = Math.min(minIndent, indent(line));
        }
        return minIndent;
    }

    int indent(String line) {
        Matcher m = LEADING_WHITE.matcher(line);

        if (m.matches()) {
            String space = m.group(1);
            return space.length();
        }

        // lines with only whitespace don't count towards minimum
        // indentation
        return Integer.MAX_VALUE;
    }

    private List<String> readLines(String snippetId) throws IOException {
        boolean all = "ALL".equalsIgnoreCase(snippetId);

        InputStream inputStream = null;
        HttpMethod method = null;
        List<String> lines = null;
        try {
            if ("http".equals(source.getProtocol()) || "https".equals(source.getProtocol()))
            {
                // TODO: maybe we should be doing some connection pooling here
                HttpClient httpClient = new HttpClient();
                httpClient.getParams().setConnectionManagerTimeout(HTTP_TIMEOUT);
                httpClient.getParams().setSoTimeout(HTTP_TIMEOUT);
                if (username != null)
                    httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

                if ("https".equals(source.getProtocol()))
                    httpClient.getHostConfiguration().setHost(source.getHost(), (source.getPort() > 0 ? source.getPort() : 443), MY_HTTPS);
                else
                    httpClient.getHostConfiguration().setHost(source.getHost(), (source.getPort() > 0 ? source.getPort() : 80));
                
                method = new GetMethod();
                method.setPath(source.getPath());
                method.setQueryString(source.getQuery());
                int statusCode = httpClient.executeMethod(method);
                if (statusCode != HttpStatus.SC_OK) {
                    throw new IOException("Unable to retrieve the URL: "+source+" status code: "+statusCode);
                }

                // Read the response body.
                inputStream = new ByteArrayInputStream(method.getResponseBody());
            }
            else
            {
                inputStream = source.openStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            lines = new ArrayList<String>();

            boolean capture = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (all) {
                    lines.add(line);
                } else if (isStart(snippetId, line)) {
                    capture = true;
                } else if (isEnd(snippetId, line)) {
                    break;
                } else if (capture) {
                    lines.add(line);
                }
            }
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (method != null)
                method.releaseConnection();
        }
        return lines;
    }

    protected boolean isStart(String snippetId, String line) {
        return isDemarcator(snippetId, START_TOKEN, line);
    }

    protected boolean isDemarcator(String snippetId, String what, String line) {
        String upper = line.toUpperCase(Locale.ENGLISH);
        return upper.indexOf(what.toUpperCase(Locale.ENGLISH)) != -1 &&
                (snippetId == null || line.indexOf(snippetId) != -1);
    }

    protected boolean isEnd(String snippetId, String line) {
        return isDemarcator(snippetId, END_TOKEN, line);
    }
}
