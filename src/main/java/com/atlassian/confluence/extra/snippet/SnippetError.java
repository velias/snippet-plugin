package com.atlassian.confluence.extra.snippet;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;

import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Represents a snippet error
 */
public class SnippetError
{
    private String spaceKey;
    private String pageTitle;
    private String snippetUrl;
    private String snippetId;
    private String message;
    private Date timestamp;
    private Page page;

    private static final SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");


    public SnippetError(String spaceKey, String pageTitle, String snippetUrl, String snippetId, String msg)
    {
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
        this.snippetUrl = snippetUrl;
        this.snippetId = snippetId;
        this.message = msg;
        this.timestamp = new Date();
    }

    public SnippetError(String values, PageManager pageManager)
    {
        StringTokenizer st = new StringTokenizer(values, "|");

        if (st.hasMoreTokens()) spaceKey = st.nextToken();
        if (st.hasMoreTokens()) pageTitle = st.nextToken();
        if (st.hasMoreTokens()) snippetUrl = st.nextToken();
        if (st.hasMoreTokens()) snippetId = st.nextToken();
        if (st.hasMoreTokens()) message = st.nextToken();
        if (st.hasMoreTokens()) timestamp = new Date(Long.parseLong(st.nextToken()));

        if (pageTitle != null && spaceKey != null)
        {
            page = pageManager.getPage(spaceKey, pageTitle);
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(spaceKey).append("|");
        sb.append(pageTitle).append("|");
        sb.append(snippetUrl).append("|");
        sb.append(snippetId).append("|");
        sb.append(message).append("|");
        if (timestamp != null)
            sb.append(timestamp.getTime()).append("|");
        return sb.toString();
    }
    
    public String toStringNoTimestamp()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(spaceKey).append("|");
        sb.append(pageTitle).append("|");
        sb.append(snippetUrl).append("|");
        sb.append(snippetId).append("|");
        sb.append(message).append("|");
        return sb.toString();
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnippetError that = (SnippetError) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (pageTitle != null ? !pageTitle.equals(that.pageTitle) : that.pageTitle != null) return false;
        if (snippetId != null ? !snippetId.equals(that.snippetId) : that.snippetId != null) return false;
        if (snippetUrl != null ? !snippetUrl.equals(that.snippetUrl) : that.snippetUrl != null) return false;
        if (spaceKey != null ? !spaceKey.equals(that.spaceKey) : that.spaceKey != null) return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (spaceKey != null ? spaceKey.hashCode() : 0);
        result = 31 * result + (pageTitle != null ? pageTitle.hashCode() : 0);
        result = 31 * result + (snippetUrl != null ? snippetUrl.hashCode() : 0);
        result = 31 * result + (snippetId != null ? snippetId.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    public String getSpaceKey()
    {
        return spaceKey;
    }

    public String getPageTitle()
    {
        return pageTitle;
    }

    public Page getPage()
    {
        return page;
    }

    public String getSnippetUrl()
    {
        return snippetUrl;
    }

    public String getSnippetId()
    {
        return snippetId;
    }

    public String getMessage()
    {
        return message;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public String getFormattedTimestamp()
    {
        synchronized(SnippetError.class)
        {
            return df.format(timestamp);
        }
    }
}
