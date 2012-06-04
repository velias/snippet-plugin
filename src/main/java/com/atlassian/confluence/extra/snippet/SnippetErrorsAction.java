package com.atlassian.confluence.extra.snippet;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.core.util.ClassLoaderUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.io.IOException;

/**
 * Display snippet errors for a space and allow the user to clear them
 */
public class SnippetErrorsAction extends AbstractSpaceAction
{

    private SnippetErrorManager snippetErrorManager;
    private List<SnippetError> snippetErrors;
    private String clearSnippetErrors;

    public String execute() throws Exception {

        if (clearSnippetErrors != null)
        {
            snippetErrorManager.clear(getKey());
            addActionMessage(getText("configureSnippet.clearErrors.success"));
        }

        snippetErrors = snippetErrorManager.get(getKey());
        return SUCCESS;
    }

    public void setSnippetErrorManager(SnippetErrorManager snippetErrorManager)
    {
        this.snippetErrorManager = snippetErrorManager;
    }

    public List<SnippetError> getSnippetErrors()
    {
        return snippetErrors;
    }

    public void setClearSnippetErrors(String clearSnippetErrors)
    {
        this.clearSnippetErrors = clearSnippetErrors;
    }

}
