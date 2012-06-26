'Snippet Plugin'
================

This is clone of https://marketplace.atlassian.com/plugins/com.atlassian.confluence.extra.snippet plugin source codes (BSD license).
Contains patches for some problems reported in https://studio.plugins.atlassian.com/browse/SNIP but not solved yet.

2.1.4.2
- ORG-1465 - cache implemented for snippet content retrieving errors. If snippet content http read fails, then subsequent reads are suspended for next 5 minutes. Same error is shown to the users whole 5 minutes and no DB reads or writes are performed for it in this interval.
- ORG-1465 - changed handling for "Snippet Errors" list - if some concrete error is already in list, it is not added to the list again. Every add means DB write, so this change decreases DB writes. As side effect there is date and time of first error occurrence, no of last!
- both "connection" and "socket read" timeouts decreased to 3 seconds

2.1.4.1 - changes against original 2.1.4 version
- SNIP-24 - patched IndexOutOfBoundsException when error queue gets full
- SNIP-28 - Improve snippet macro definition to show parameters in Macro insert/edit dialog
- SNIP-30 - patched bad template for "Snippet Errors" page - "Space Admin" instead of correct "Advanced"
- 'Snippet Plugin Configuration' page is now accessible from "Configuration" section in admin menu
- both "connection" and "socket read" timeouts set to 5 seconds