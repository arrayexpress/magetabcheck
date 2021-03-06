## Release 1.22
  * Updated Known Protocol Hardware List

## Release 1.21
  * Updated API to return reference code for enabling custom error messages in Annotare

## Release 1.20

  * Updated sequencing hardware list
  * Updated to latest version of Guice/Guava
  * Slightly updated reflection scanner invocation

## Release 1.19

  * Switched to limpopo 1.1.14 and org.reflections to 0.9.10

## Release 1.18

  * Clarified check AN08 (An assay must be connected to a number of distinctly labeled extracts that equals a number of channels (dyes used))
  * Reworked check runner framework so checks re-use common class instance for checking session
  * It is possible to use assertions in "visit" phase of class-based check
  * Some checks were reworded for clarity
  * Switched to limpopo 1.1.10
  
## Release 1.17

  * Re-enabled file presence checking for both Annotare-embedded and standalone checking 
  * Added verification of NOMINAL_LENGTH and NOMINAL_SDEV parameters for paired-end HTS samples

## Release 1.16

  * Switched to Guava 16.0.1
  * Changed wording of some error/warning messages from "experimental factor" to "experimental variable"
  * Updated list of accepted sequencing hardware
  * Fixed SequencingProtocolRequired and SequencingProtocolHardwareRequired to accept more than one sequencing protocol defined

## Release 1.15

  * Added check for characteristics of the same type
  * Updated error message about missing sequencing hardware so it contains some new platforms accepted by the ENA
  * Added check for presence of library comments attached to extract node for sequencing submissions
  * Updated check for presence of assay names

## Release 1.14

  * Added check for file names to ensure they only contain alphanumeric characters, underscores and dots.
  * Update to limpopo 1.1.8

## Release 1.13

  * Updated check for varying factor values so the offending factor is returned in error message
  * Reworded some warning and error messages

## Release 1.12

 * Enforce application of a minimum set of protocols based on experiment type.
 * Added logic for dependant factors like compound and dose
 * Updated code that checks for existence of contacts, experimental factors, designs and protocols
 * Switched to limpopo 1.1.7

## Release 1.11

 * Updated list of acceptable hardware for nucleic acid sequencing protocol.
 * Performer is now mandatory only for nucleic acid sequencing protocol.
 * Switched to limpopo 1.1.5

## Release 1.10

 * Loosing sequencing and library construction rules: if term source is not specified for a protocol type then do not check it; sequencing protocol hardware check extracted into separate rule.
 * Changed modality of a rule: description of a protocol is required
 * Disabled file location check; just check that a file name cell is not empty

## Release 1.9

 * bugfix: 'Definition' annotation of efo ontology classes was not extracted properly and as result all efo terms had empty definition field
 * bugfix: Started to ignore empty objects which are generated by Limpopo MAGETAB library empty strings
 * bugfix: Fixed implementation if rule AN05 (If 'Technology Type' value = 'array assay' then incoming nodes must be 'Labeled Extract' nodes only); the rule doesn't work when protocol nodes were inserted between labeled extract and assay nodes

## Release 1.8

 * 'efo' package is in 'magetabcheck' package now
 * EfoTerm now has 'definition' property (Annotare2 requires this feature)
 * LimpopoBasedExperiment can be created with separate IDF and SDRF instances (Annotare2 requires this feature)
 * [#28](https://github.com/arrayexpress/magetabcheck/issues/28) Add SDRF-to-IDF ref check: IDF should not contain protocol definitions that are not used in SDRF
 * [#9](https://github.com/arrayexpress/magetabcheck/issues/9) Add Assay Node check: 'Technology Type' value = 'array assay' then incoming 'Labeled Extract nodes must have distinct labels

## Release 1.7

 * EFO service was divided into two: MageTabCheckEfo (specific MageTabCheck) and EfoService (the core); it is needed
   for easy override/re-use the EFO core graph
 * [#8](https://github.com/arrayexpress/magetabcheck/issues/8) Add Assay Node check: If 'Technology Type' value = 'array assay' then incoming nodes must be 'Labeled Extract' nodes only
 * [#10](https://github.com/arrayexpress/magetabcheck/issues/10) Add Assay Node Check: An assay node must be described by a 'sequencing' protocol
 * [#4](https://github.com/arrayexpress/magetabcheck/issues/10) Add Extract Node Check: An extract node must be described by a 'construction library' protocol
 * [#14](https://github.com/arrayexpress/magetabcheck/issues/14) Add Labeled Extract Node Check: Number of unique labels in a experiment should not be greater than the number of channels

## Release 1.6

 * fixed a critical bug in graph traverse approach
 * [#6](https://github.com/arrayexpress/magetabcheck/issues/6)  Added check: protocol node should have non empty 'performer' attribute
 * [#7](https://github.com/arrayexpress/magetabcheck/issues/7)  Values of a experiment factor must vary
 * [#37](https://github.com/arrayexpress/magetabcheck/issues/37) Check for non empty Comment[AEExperimentType] added
 * [#42](https://github.com/arrayexpress/magetabcheck/issues/42) Check Removed (as useless) PN02 : A protocol node should have date specified
 * [#43](https://github.com/arrayexpress/magetabcheck/issues/43) StackOverflowException when EFO service is unavailable

## Release 1.5

 * [#21](https://github.com/arrayexpress/magetabcheck/issues/21) Generate the list of implemented checks automatically
 * [#40](https://github.com/arrayexpress/magetabcheck/issues/40) MageTabCheck configuration file required.
   Now one can use a property file to set MageTabCheck parameters, such as debug, EFO URL, EFO cache, etc. (see wiki for details)

## Release 1.4

 * Fixed a critical bug in the checker core, which was introduced by the previous release refactorings; the wrong set of checks were run for a target object
 * [#39](https://github.com/arrayexpress/magetabcheck/issues/39) NPE when IDF and SDRF has no location set
 * Added unit tests for SequencingProtocolRequired and LibraryConstructionProtocolRequired checks

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
