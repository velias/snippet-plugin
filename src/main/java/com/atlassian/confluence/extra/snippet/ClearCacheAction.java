package com.atlassian.confluence.extra.snippet;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.io.IOException;

/**
 * Clears the snippet cache for a snippet entry
 */
public class ClearCacheAction extends ActionSupport implements ServletRequestAware {
    String url;
    String id;
    String redirect;
    HttpServletRequest req;
    private SnippetManager snippetManager;

    public void setServletRequest(HttpServletRequest req) {
        this.req = req;
    }

    public String execute() throws Exception {
        ResolvedUrl resolvedUrl = snippetManager.resolveUrl(url);
        if (resolvedUrl == null)
            throw new IllegalArgumentException("Invalid url: must begin with a configured prefix.");

        snippetManager.removeFromCache(new URL(resolvedUrl.getResolvedUrl()), id);
        redirect = req.getHeader("Referer");
        if (redirect == null) {
            redirect = "/";
        }

        return SUCCESS;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setSnippetManager(SnippetManager snippetManager)
    {
        this.snippetManager = snippetManager;
    }
}
