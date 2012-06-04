package com.atlassian.confluence.extra.snippet.config;

import com.atlassian.confluence.core.Administrative;
import com.atlassian.confluence.extra.snippet.*;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.xwork.ParameterSafe;
import com.opensymphony.util.TextUtils;

import java.util.*;

/**
 * Action to configure Snippet plugin settings
 */
public class ConfigureSnippetAction extends AbstractSpaceAction implements Administrative
{
    private SnippetManager snippetManager;
    private Map settings;

    private String[] prefixesToRemove;
    private String url;
    private String prefix;
    private Credentials credentials;
    private boolean useCredentials;
    private Boolean toggle;
    private String notificationEmail;
    private String cancel;
    private String clearErrors;
    private String clearSnippetCache;
    private String addUrlPrefix;
    private String removeUrlPrefix;
    private String emailBodyTemplate;
    private String emailSubjectTemplate;
    private Long cacheTimeout;
    private Map urlPrefixes = new HashMap() {
        public Object put(Object key, Object value)
        {
            if (value instanceof String[])
                value = ((String[]) value)[0];
            return super.put(key, value);
        }
    };
    private SnippetErrorManager snippetErrorManager;

    public boolean isToggle()
    {
        return toggle.booleanValue();
    }

    public void setToggle(boolean toggle)
    {
        this.toggle = Boolean.valueOf(toggle);
    }

    public String getNotificationEmail()
    {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail)
    {
        this.notificationEmail = notificationEmail;
    }

    public Long getCacheTimeout()
    {
        return cacheTimeout;
    }

    public void setCacheTimeout(Long cacheTimeout)
    {
        this.cacheTimeout = cacheTimeout;
    }

    public Map getUrlPrefixes()
    {
        return urlPrefixes;
    }

    //public void setUrlPrefixes(Map urlPrefixes)
    //{
    //    this.urlPrefixes = urlPrefixes;
    //}

    public Map getSettings()
    {
        return settings;
    }

    public void setCancel(String cancel)
    {
        this.cancel = cancel;
    }

    public String getEmailBodyTemplate()
    {
        return emailBodyTemplate;
    }

    public void setEmailBodyTemplate(String emailBodyTemplate)
    {
        this.emailBodyTemplate = emailBodyTemplate;
    }

    public String getEmailSubjectTemplate()
    {
        return emailSubjectTemplate;
    }

    public void setEmailSubjectTemplate(String emailSubjectTemplate)
    {
        this.emailSubjectTemplate = emailSubjectTemplate;
    }

    public void setSnippetManager(SnippetManager snippetManager)
    {
        this.snippetManager = snippetManager;
    }

    public String execute() throws Exception
    {
        loadSettings();
        toggle = (Boolean)settings.get(SnippetManager.TOGGLE);
        notificationEmail = (String) settings.get(SnippetManager.NOTIFICATION_EMAIL);
        cacheTimeout = new Long(((Long) settings.get(SnippetManager.CACHE_TIMEOUT)).longValue() / (1000 * 60)) ;
        emailBodyTemplate = (String) settings.get(SnippetManager.EMAIL_BODY_TEMPLATE);
        emailSubjectTemplate = (String) settings.get(SnippetManager.EMAIL_SUBJECT_TEMPLATE);
        urlPrefixes = (Map) settings.get(SnippetManager.URL_PREFIXES);

        return SUCCESS;
    }

    private void loadSettings()
    {
        settings = snippetManager.getSettings();
    }

    public String save() throws Exception
    {
        if (cancel != null)
            return "cancel";


        loadSettings();
        urlPrefixes = (Map) settings.get(SnippetManager.URL_PREFIXES);

        if (clearErrors != null)
        {
            snippetErrorManager.clear();
            addActionMessage(getText("configureSnippet.clearErrors.success"));
            return "input";
        }

        if (clearSnippetCache != null)
        {
            snippetManager.clearCache();
            addActionMessage(getText("configureSnippet.clearSnippetCache.success"));
            return "input";
        }

        if (addUrlPrefix != null)
        {
            urlPrefixes.put(prefix, url);
            if (useCredentials)
            {
                Map urlCredentials = (Map) settings.get(SnippetManager.URL_CREDENTIALS);
                urlCredentials.put(prefix, credentials.toString());
            }
        }

        if (removeUrlPrefix != null)
        {
            for (int x=0; x<prefixesToRemove.length; x++)
               urlPrefixes.remove(prefixesToRemove[x]);
        }

        settings.put(SnippetManager.TOGGLE, toggle);
        settings.put(SnippetManager.NOTIFICATION_EMAIL, (TextUtils.stringSet(notificationEmail)?notificationEmail:null));
        settings.put(SnippetManager.CACHE_TIMEOUT, new Long(cacheTimeout.longValue() * 1000 * 60));
        settings.put(SnippetManager.EMAIL_BODY_TEMPLATE, emailBodyTemplate);
        settings.put(SnippetManager.EMAIL_SUBJECT_TEMPLATE, emailSubjectTemplate);
        settings.put(SnippetManager.URL_PREFIXES, new HashMap(urlPrefixes));

        snippetManager.saveSettings(settings);
        addActionMessage(getText("configureSnippet.saved"));
        return SUCCESS;
    }

    public void setAddUrlPrefix(String addUrlPrefix)
    {
        this.addUrlPrefix = addUrlPrefix;
    }

    public void setRemoveUrlPrefix(String removeUrlPrefix)
    {
        this.removeUrlPrefix = removeUrlPrefix;
    }

    public void setPrefixesToRemove(String[] prefixesToRemove)
    {
        this.prefixesToRemove = prefixesToRemove;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void setClearErrors(String clearErrors)
    {
        this.clearErrors = clearErrors;
    }

    public void setSnippetErrorManager(SnippetErrorManager snippetErrorManager)
    {
        this.snippetErrorManager = snippetErrorManager;
    }

    public void setClearSnippetCache(String clearSnippetCache)
    {
        this.clearSnippetCache = clearSnippetCache;
    }

    @ParameterSafe
    public Credentials getCredentials()
    {
        return credentials;
    }

    public void setCredentials(Credentials credentials)
    {
        this.credentials = credentials;
    }

    public boolean getUseCredentials()
    {
        return useCredentials;
    }

    public void setUseCredentials(boolean useCredentials)
    {
        this.useCredentials = useCredentials;
    }
}