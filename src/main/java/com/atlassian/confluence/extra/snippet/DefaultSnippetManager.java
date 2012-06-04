package com.atlassian.confluence.extra.snippet;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.util.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages snippet loading and caching
 */
public class DefaultSnippetManager implements SnippetManager
{
    private static final int MINUTE = 60 * 1000;
    private final Map<String, Snippet> cache = new HashMap<String, Snippet>();
    private final Map<String, Long> timeCached = new HashMap<String, Long>();

    private BandanaManager bandanaManager;
    private Map<String, Object> cachedSettings = null;
    private long cachedSettingsExpiry;
    public static final String SETTINGS_KEY = "snippetSettings";

    private static Log log = LogFactory.getLog(DefaultSnippetManager.class);

    public Snippet getSnippet(URL url, Credentials credentials, String id) throws IOException
    {
        Snippet result = getCachedSnippet(url, id);
        if (result == null)
        {
            SnippetReader reader = null;
            if (credentials != null)
                reader = new SnippetReader(url, credentials.getUsername(), credentials.getPassword());
            else
                reader = new SnippetReader(url);
            result = reader.readSnippet(id);
            cacheSnippet(url, id, result);
        }

        return result;
    }

    public Snippet getCachedSnippet(URL url, String id)
    {
       if (isCacheTimedout(url, id))
       {
           removeFromCache(url, id);
       }
       return cache.get(globalSnippetId(url, id));
    }

    public void cacheSnippet(URL url, String id, Snippet snippet)
    {
        cache.put(globalSnippetId(url, id), snippet);
        timeCached.put(globalSnippetId(url, id), System.currentTimeMillis());
    }

    public String cleanupJavadoc(String msg) {
        return msg.replaceAll("\\{\\@link ([^ \\}]+)\\}", "$1");
    }

    public boolean shouldShowToggle()
    {
        return (Boolean) getSettings().get(TOGGLE);
    }

    private boolean isCacheTimedout(URL url, String id)
    {
        String globalId = globalSnippetId(url, id);
        long curTimeCached = timeCached.containsKey(globalId) ? timeCached.get(globalId) : 0;
        long timeInCache = System.currentTimeMillis() - curTimeCached;
        long timeout = (Long) getSettings().get(CACHE_TIMEOUT);
        return timeInCache >= timeout;
    }

    public void removeFromCache(URL url, String id)
    {
        String globalId = globalSnippetId(url, id);
        timeCached.remove(globalId);
        cache.remove(globalId);
    }

    public void clearCache()
    {
        timeCached.clear();
        cache.clear();
    }

    private String globalSnippetId(URL url, String id)
    {
        return url + " " + id;
    }

    public ResolvedUrl resolveUrl(String urlParam)
    {
        String url = null;
        Map<String, String> credentials = (Map<String, String>) getSettings().get(URL_CREDENTIALS);
        Map<String, String> classPrefixes = (Map<String, String>) getSettings().get(URL_PREFIXES);
        String longestKey = "";
        for (String key : classPrefixes.keySet())
        {
            if (urlParam.startsWith(key) && (key.length() > longestKey.length()))
            {
                String modParam = urlParam.substring(key.length());
                // Detect direct class reference
                int lastDot = modParam.lastIndexOf('.');
                if (modParam.indexOf('/') == -1 && Character.isUpperCase(modParam.charAt(lastDot + 1)))
                {
                    modParam = modParam.replaceAll("\\.", "/") + ".java";
                }

                String val = classPrefixes.get(key);
                url = val + modParam;
                longestKey = key;
            }
        }
        if (url != null)
        {
            Credentials creds = null;
            String credsStr = credentials.get(longestKey);
            if (credsStr != null)
                creds = new Credentials(credsStr);
            return new ResolvedUrl(longestKey, classPrefixes.get(longestKey), url, creds);
        }
        else
            return null;
    }

    /*
    public void email(String pageUrl, URL url, String id, String message, Exception e) {

        Map settings = getSettings();
        String emailAddress = (String) settings.get(NOTIFICATION_EMAIL);
        if (emailAddress == null)
            return;

        String mimeType = AbstractMailNotificationQueueItem.MIME_TYPE_TEXT;
        Email mail = new Email(emailAddress);
        mail.setEncoding(Settings.DEFAULT_DEFAULT_ENCODING);
        mail.setSubject(generateNotificationSection(EMAIL_SUBJECT_TEMPLATE, pageUrl, url, id, message, e));
        mail.setBody(generateNotificationSection(EMAIL_BODY_TEMPLATE, pageUrl, url, id, message, e));
        mail.setMimeType(mimeType);

        SingleMailQueueItem queueItem = new SingleMailQueueItem(mail);
        taskManager.addTask("mail", queueItem);
    }

    protected String generateNotificationSection(String sectionName, String pageUrl, URL url, String id, String message, Exception e)
    {
        Map settings = getSettings();
        String emailTemplate = (String) settings.get(sectionName);

        Map context = new HashMap();
        context.put("baseUrl", settingsManager.getGlobalSettings().getBaseUrl());
        context.put("pageUrl", pageUrl);
        context.put("snippetUrl", url);
        context.put("snippetId", id);
        context.put("message", message);
        context.put("exception", e);
        return VelocityUtils.getRenderedContent(emailTemplate, context);
    }
    */

    public synchronized Map<String, Object> getSettings()
    {
        long now = System.currentTimeMillis();
        if (cachedSettings != null && now < cachedSettingsExpiry)
            return cachedSettings;

        ConfluenceBandanaContext ctx = new ConfluenceBandanaContext();
        Map<String, Object> settings = (Map<String, Object>) bandanaManager.getValue(ctx, SETTINGS_KEY);
        if (settings == null)
            settings = new HashMap<String, Object>();

        if (!settings.containsKey(EMAIL_BODY_TEMPLATE))
            settings.put(EMAIL_BODY_TEMPLATE, readResource("templates/snippet/notification-body.vm"));
        if (!settings.containsKey(EMAIL_SUBJECT_TEMPLATE))
            settings.put(EMAIL_SUBJECT_TEMPLATE, readResource("templates/snippet/notification-subject.vm"));

        if (!settings.containsKey(TOGGLE))
            settings.put(TOGGLE, Boolean.FALSE);

        if (!settings.containsKey(CACHE_TIMEOUT))
            settings.put(CACHE_TIMEOUT, (long) MINUTE * 60);

        if (!settings.containsKey(URL_PREFIXES))
            settings.put(URL_PREFIXES, new HashMap());

        if (!settings.containsKey(URL_CREDENTIALS))
            settings.put(URL_CREDENTIALS, new HashMap());

        cachedSettings = settings;
        cachedSettingsExpiry = now + MINUTE;
        return settings;
    }

    public synchronized void saveSettings(Map<String, Object> settings)
    {

        if (!settings.containsKey(EMAIL_BODY_TEMPLATE))
            settings.put(EMAIL_BODY_TEMPLATE, readResource("templates/snippet/notification-body.vm"));
        if (!settings.containsKey(EMAIL_SUBJECT_TEMPLATE))
            settings.put(EMAIL_SUBJECT_TEMPLATE, readResource("templates/snippet/notification-subject.vm"));

        ConfluenceBandanaContext ctx = new ConfluenceBandanaContext();
        bandanaManager.setValue(ctx, SETTINGS_KEY, settings);
        cachedSettings = settings;
    }

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }

    private String readResource(String path)
    {
        StringWriter writer = new StringWriter();
        InputStream in = null;
        try
        {
            in = getClass().getClassLoader().getResourceAsStream(path);
            if (in == null)
            {
                throw new FileNotFoundException(path);
            }
            Reader reader = new BufferedReader(new InputStreamReader(in));
            char[] buffer = new char[2048];
            int len = 0;
            while ((len = reader.read(buffer)) > 0)
                writer.write(buffer, 0, len);
        } catch (IOException ex)
        {
            log.error("Unable to retrieve resource: "+path, ex);
        } finally
        {
            IOUtils.close(in);
        }
        return writer.toString();
    }
}
