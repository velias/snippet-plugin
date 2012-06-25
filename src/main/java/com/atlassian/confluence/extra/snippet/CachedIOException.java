/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package com.atlassian.confluence.extra.snippet;

import java.io.IOException;

/**
 * Cached IOException.
 *
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class CachedIOException extends IOException {

  /**
   * @param cause
   */
  public CachedIOException(IOException cause) {
    super(cause.getMessage());
  }
  
  
}
