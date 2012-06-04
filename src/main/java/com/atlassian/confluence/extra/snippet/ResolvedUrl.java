package com.atlassian.confluence.extra.snippet;

public class ResolvedUrl
{
    private String prefix;
    private String url;
    private String resolvedUrl;
    private Credentials credentials;


    public ResolvedUrl(String prefix, String url, String resolvedUrl, Credentials credentials)
    {
        this.prefix = prefix;
        this.url = url;
        this.resolvedUrl = resolvedUrl;
        this.credentials = credentials;
    }


    public String getPrefix()
    {
        return prefix;
    }

    public String getUrl()
    {
        return url;
    }

    public String getResolvedUrl()
    {
        return resolvedUrl;
    }

    public Credentials getCredentials()
    {
        return credentials;
    }
}
