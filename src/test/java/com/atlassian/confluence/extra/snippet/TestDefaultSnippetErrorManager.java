package com.atlassian.confluence.extra.snippet;

import junit.framework.TestCase;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.bandana.BandanaManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class TestDefaultSnippetErrorManager extends TestCase
{

    Mock mockBandanaManager;
    Mock mockSpaceManager;
    Mock mockPageManager;
    DefaultSnippetErrorManager snippetErrorManager;

    List<String> globalErrors = new ArrayList<String>();
    List<String> spaceErrors = new ArrayList<String>();

    String spaceKey = "space1";
    SnippetError spaceErr = new SnippetError(spaceKey, "pageUrl", "snippetUrl", "id", "message");
    SnippetError spaceErr2 = new SnippetError(spaceKey, "pageUrl2", "snippetUrl2", "id2", "msg2");
    SnippetError globalErr = new SnippetError(null, "pageUrl", "snippetUrl", "id", "message");


    protected void setUp() throws Exception
    {
        super.setUp();
        mockBandanaManager = new Mock(BandanaManager.class);
        mockSpaceManager = new Mock(SpaceManager.class);
        mockPageManager = new Mock(PageManager.class);

        snippetErrorManager = new DefaultSnippetErrorManager(
                (BandanaManager) mockBandanaManager.proxy(),
                (SpaceManager) mockSpaceManager.proxy(),
                (PageManager) mockPageManager.proxy());

    }

    public void testAdd()
    {
        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, globalErrors);
        mockBandanaManager.expect("setValue", C.args(C.isA(ConfluenceBandanaContext.class),
                C.eq(DefaultSnippetErrorManager.PERSISTENCE_KEY), C.eq(Collections.singletonList(globalErr.toString()))));
        snippetErrorManager.add(globalErr);
        mockBandanaManager.verify();

        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, spaceErrors);
        mockBandanaManager.expect("setValue", C.args(C.isA(ConfluenceBandanaContext.class),
                C.eq(DefaultSnippetErrorManager.PERSISTENCE_KEY), C.eq(Collections.singletonList(spaceErr.toString()))));
        snippetErrorManager.add(spaceErr);
        mockBandanaManager.verify();


        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, spaceErrors);
        mockBandanaManager.expect("setValue", C.args(C.isA(ConfluenceBandanaContext.class),
                C.eq(DefaultSnippetErrorManager.PERSISTENCE_KEY), C.eq(Collections.singletonList(spaceErr2.toString()))));
        snippetErrorManager.add(spaceErr2);
        mockBandanaManager.verify();

    }

    public void testGet() throws Exception
    {
        spaceErrors.add(spaceErr.toString());
        spaceErrors.add(spaceErr2.toString());
        globalErrors.add(globalErr.toString());

        mockPageManager.expectAndReturn("getPage", C.ANY_ARGS, new Page());
        mockPageManager.expectAndReturn("getPage", C.ANY_ARGS, new Page());
        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, spaceErrors);
        List list = snippetErrorManager.get(spaceKey);
        assertNotNull(list);
        assertEquals(2, list.size());
        mockBandanaManager.verify();

        assertTrue(list.get(0) instanceof SnippetError);

        mockPageManager.expectAndReturn("getPage", C.ANY_ARGS, new Page());
        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, globalErrors);
        list = snippetErrorManager.get(null);
        assertNotNull(list);
        assertEquals(1, list.size());
        mockBandanaManager.verify();
    }

    public void testRetrieveErrorsEnsureCopy() throws Exception
    {
        spaceErrors.add(spaceErr.toString());
        spaceErrors.add(spaceErr2.toString());
        globalErrors.add(globalErr.toString());

        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, spaceErrors);
        List list = snippetErrorManager.retrieveErrors(spaceKey);
        assertNotNull(list);
        assertEquals(2, list.size());
        list.clear();
        mockBandanaManager.verify();

        mockBandanaManager.expectAndReturn("getValue", C.ANY_ARGS, spaceErrors);
        list = snippetErrorManager.retrieveErrors(spaceKey);
        assertNotNull(list);
        assertEquals(2, list.size());
        mockBandanaManager.verify();
    }

    public void testClear() throws Exception
    {
        spaceErrors.add(spaceErr.toString());
        spaceErrors.add(spaceErr2.toString());
        globalErrors.add(globalErr.toString());

        List<Space> spaces = new ArrayList<Space>();
        spaces.add(new Space("key"));

        mockSpaceManager.expectAndReturn("getAllSpaces", C.ANY_ARGS, spaces);
        mockBandanaManager.expect("setValue", C.ANY_ARGS);
        snippetErrorManager.clear(spaceKey);
        mockBandanaManager.verify();

        mockSpaceManager.expectAndReturn("getAllSpaces", C.ANY_ARGS, spaces);
        mockBandanaManager.expect("setValue", C.ANY_ARGS);
        mockBandanaManager.expect("setValue", C.ANY_ARGS);
        snippetErrorManager.clear();
        mockBandanaManager.verify();
    }

}
