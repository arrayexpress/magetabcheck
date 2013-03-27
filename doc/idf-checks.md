# IDF checks (54)
(updated: 27/03/2013 17:06)

+ [Contact Checks](#contact-checks)
+ [Experimental Design Checks](#experimental-design-checks)
+ [Experimental Factor Checks](#experimental-factor-checks)
+ [General Checks](#general-checks)
+ [Normalization Type Checks](#normalization-type-checks)
+ [Publication Checks](#publication-checks)
+ [Protocol Checks](#protocol-checks)
+ [Quality Control Type Checks](#quality-control-type-checks)
+ [Replicate Type Checks](#replicate-type-checks)
+ [Term Source Checks](#term-source-checks)
+ [Term source list](#term-source-list)
+ [Supported protocol hardware list](#supported-protocol-hardware-list)

## Contact Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|C01|**error**|Both|At least one contact must be specified||
|C02|**error**|Both|A contact must have last name specified||
|C03|**error**|Both|At least one contact must have email specified||
|C04|**error**|Both|At least one contact must have a role specified||
|C05|**error**|Both|At least one contact must have 'submitter' role specified ||
|C06|**error**|HTS|A contact with 'submitter' role must have affiliation specified||
|C07|**error**|Both|At least one contact with 'submitter' role must have an email specified||
|C08|warning|Both|A contact should have first name specified||
|C09|warning|Both|A contact should have an affiliation specified||
|C10|warning|Both|A contact role(s) should have a term source specified||

## Experimental Design Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|ED01|warning|Micro-array|Micro-array experiment must have at least one experimental design specified||
|ED02|warning|Micro-array|An experimental design should be defined by a term||
|ED03|warning|Micro-array|An experimental design term should have a term source||

## Experimental Factor Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|EF01|**error**|Both|An experiment must have at least one experimental factor specified||
|EF02|**error**|Both|An experimental factor must have name specified||
|EF03|warning|Both|An experimental factor should have a type specified||
|EF04|warning|Both|An experimental factor type should have term source specified||

## General Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|G01|**error**|Both|Experiment title must be specified||
|G02|**error**|Both|Experiment description must be specified||
|G04|**error**|Both|Date of Experiment must be in 'YYYY-MM-DD' format||
|G05|**error**|Both|Experiment public release date must be specified||
|G06|**error**|Both|Experiment public release date must be in 'YYYY-MM-DD' format||
|G07|**error**|Both|Reference to the SDRF file must be specified||
|G08|**error**|Both|Reference to the SDRF file must be valid file location||
|G03|warning|Both|Date of Experiment should be specified||
|G09|warning|Both|Experiment description should be at least 50 characters long||

## Normalization Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|NT01|warning|Both|An experiment should have at least one normalization type specified||
|NT02|warning|Both|A normalization type should be defined by a term||
|NT03|warning|Both|A normalization type should have term source specified||

## Publication Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PB02|**error**|Both|PubMed Id must be numeric||
|PB01|warning|Both|A publication should have at least one of PubMed ID or Publication DOI specified||
|PB03|warning|Both|A publication authors should be specified||
|PB04|warning|Both|A publication title should be specified||
|PB05|warning|Both|A publication status should be specified||
|PB06|warning|Both|A publication status should have term source specified||

## Protocol Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PR01|**error**|Both|At least one protocol must be used in an experiment||
|PR02|**error**|Both|Name of a protocol must be specified||
|PR03|**error**|Both|A protocol type must be specified||
|PR08|**error**|HTS|Library construction protocol is required for HTS submissions|1. A `Protocol Type` field must be the name of ['library construction protocols' class in EFO](http://bioportal.bioontology.org/ontologies/49470/?p=terms&conceptid=efo%3AEFO_0004184) or one of its children; <br/> 2. `Protocol Term Source REF` must be "EFO" ([supported term sources](#term-source-list));|
|PR09|**error**|HTS|Sequencing protocol is required for HTS submissions|1. A `Protocol Type` field must be the name of ['sequencing protocols class' in EFO](http://bioportal.bioontology.org/ontologies/49470/?p=terms&conceptid=efo%3AEFO_0004170) or one of its children;<br/>2. `Protocol Term Source REF` must be "EFO" ([full list](#term-source-list))<br/>3. `Protocol Hardware` field must contain a comma separated list of protocol hardware used ([supported term sources](#supported-protocol-hardware-list));|
|PR04|warning|Both|A protocol type should have term source specified||
|PR05|warning|Both|Description of a protocol should be specified||
|PR06|warning|Both|Description of a protocol should be over 50 characters long||
|PR07|warning|Both|A protocol should have parameters||

## Quality Control Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|QC01|warning|Both|An experiment should have at least one quality control type specified||
|QC02|warning|Both|A quality control type should be defined by a term||
|QC03|warning|Both|A quality control type should have term source specified||

## Replicate Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|RT01|warning|Both|An experiment should have at least one replicate type specified||
|RT02|warning|Both|A replicate type should be defined by a term||
|RT03|warning|Both|A replicate type should have term source specified||

## Term Source Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|TS01|**error**|Both|Name of a term source must be specified||
|TS02|**error**|Both|Name of a term source must be unique||
|TS03|warning|Both|URL/File of a term source should be specified||
|TS04|warning|Both|Version of a term source should be specified||

## Term source list
* ArrayExpress http://www.ebi.ac.uk/arrayexpress/
* NCBI Taxonomy http://www.ncbi.nlm.nih.gov/taxonomy
* EFO http://www.ebi.ac.uk/efo/
* MGED Ontology http://mged.sourceforge.net/ontologies/index.php

## Supported protocol hardware list
* illumina genome analyzer
* illumina genome analyzer ii
* illumina genome analyzer iix
* illumina hiseq 2000
* illumina hiseq 1000
* illumina miseq
* 454 gs
* 454 gs 20
* 454 gs flx
* 454 gs flx titanium
* 454 gs junior
* ab solid system
* ab solid system 2.0
* ab solid system 3.0
* ab solid 4 system
* ab solid 4hq system
* ab solid pi system
* ab solid 5500
* ab solid 5500xl
* complete genomics
