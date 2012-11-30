## Release 1.0 

This is the first release of MageTabChecker.
 
 * The very basic check runner was created. It goes through the model and runs a bunch of checks (validation rules) on every object. The results are returned as a list.  
 * Implemented ~115 checks
 * Added EFO integration, so that checks could use EFO to validate the terms
 * Added a system property to set the EFO cache directory, `-Defo.cachedir=/efo/cache/directory`  
 * Added the CLI support
 * Added a system property to turn off/on the debug output, `-Dchecker.debug=true`
 * The usage instructions could be found in README.md 