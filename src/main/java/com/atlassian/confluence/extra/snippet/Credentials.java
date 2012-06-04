package com.atlassian.confluence.extra.snippet;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

public class Credentials implements Serializable
{
    private String username;
    private String password;

    // necessary for webwork
    public Credentials()
    {
    }
    public Credentials(String enc)
    {
        String data = new String(Base64.decodeBase64(enc.getBytes()));
        int pos = data.indexOf(':');
        username = data.substring(0, pos);
        password = data.substring(pos+1);
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String toString()
    {
        return new String(Base64.encodeBase64((username+":"+password).getBytes()));
    }
}
