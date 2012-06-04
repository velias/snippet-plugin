package snippet;

import junit.framework.TestCase;

public class SnippetTestFile {

    // START SNIPPET include
    // Testing
    // END SNIPPET include

    public void testTheObviousIsTrue() {
        // START SNIPPET: mySnippetId
        assertEquals(2, 1 + 1);
        // END SNIPPET: mySnippetId
    }

    public void anotherMethodThatHasBlankLines() {
        // START SNIPPET: blanklines
        assertEquals(2, 1 + 1);

        assertEquals(3, 1 + 2);
        // END SNIPPET: blanklines
    }

    public void testTheObviousIsTrueSeveralTimes() {
        // START SNIPPET: multilineSnippet
        assertEquals(2, 1 + 1);
        if(true) {
            // START SNIPPET: nestedSnippet
            assertEquals(6, 2 * 3);
            // START SNIPPET: nestedSnippet
        }
        assertEquals(2, 4 / 2);
        // END SNIPPET: multilineSnippet
    }

    /**
     * START SNIPPET: links
     * {@link Foo#bar()}
     * END SNIPPET: links
     */
    public void testLinksInJavadoc()
    {
    }

    private void assertEquals(int i, int i1)
    {
    }
}
