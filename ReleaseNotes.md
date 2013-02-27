## Release 1.4

* Fixed a major bug in the checker core, which was introduced by the previous release refactorings; the wrong set of checks were run for a target object.
* [#39](https://github.com/arrayexpress/magetabcheck/issues/39) NPE when IDF and SDRF has no location set
* Added unit tests for SequencingProtocolRequired and LibraryConstructionProtocolRequired checks.

## Release 1.3

* Core code was refactored with proper guice injections
* EfoService: Extracted interface
* EfoService: Added a feature to get list of child term names by a given term accession
* Added MGED Ontology to the list of known term sources
* [#17](https://github.com/arrayexpress/magetabcheck/issues/23) Add check: Experiment description should be at least 50 characters long

## Release 1.2

Fixed bugs:

* [#24](https://github.com/arrayexpress/magetabcheck/issues/24) Staging POM validation issues
* [#23](https://github.com/arrayexpress/magetabcheck/issues/23) Missing Signature for maven staging release

## Release 1.1

Fixed bugs:

* [#16](https://github.com/arrayexpress/magetabcheck/issues/16) Checker does not run on experiments with type 'transcription profiling by array'
* [#15](https://github.com/arrayexpress/magetabcheck/issues/16) Without -Defo.cachedir option the CL checker throws and error

## Release 1.0

This is the first release of MageTabChecker.

 * The very basic check runner was created. It goes through the model and runs a bunch of checks (validation rules) on every object. The results are returned as a list.
 * Implemented ~115 checks
 * Added EFO integration, so that checks could use EFO to validate the terms
 * Added a system property to set the EFO cache directory, `-Defo.cachedir=/efo/cache/directory`
 * Added the CLI support
 * Added a system property to turn off/on the debug output, `-Dchecker.debug=true`
 * The usage instructions could be found in README.md