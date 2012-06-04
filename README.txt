Development tips
----------------

Run the plugin in Confluence:
  mvn confluence:run 

Debug the plugin in Confluence
  mvn confluence:debug

Build and run integration tests:
  mvn verify

When executing the above run or debug commands, Maven takes care of downloading
Tomcat, Confluence, a Confluence license, and starting hsqldb database.
