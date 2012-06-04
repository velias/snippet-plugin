'Snippet Plugin'
================

This is clone of https://marketplace.atlassian.com/plugins/com.atlassian.confluence.extra.snippet plugin source codes (BSD license).
Contains patches for some problems reported in https://studio.plugins.atlassian.com/browse/SNIP but not solved yet.

2.1.4.1 - changes against original 2.1.4 version
- SNIP-24 - patched IndexOutOfBoundsException when error queue gets full
- SNIP-28 - Improve snippet macro definition to show parameters in Macro insert/edit dialog
- SNIP-30 - patched bad template for "Snippet Errors" page - "Space Admin" instead of correct "Advanced"
- 'Snippet Plugin Configuration' page is now accessible from "Configuration" section in admin menu