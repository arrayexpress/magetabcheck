# IDF checks
(updated: 27/03/2013 14:28)

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

## Contact Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|C01|**ERROR**|ANY|At least one contact must be specified|TBA|
|C02|**ERROR**|ANY|A contact must have last name specified|TBA|
|C03|**ERROR**|ANY|At least one contact must have email specified|TBA|
|C04|**ERROR**|ANY|At least one contact must have a role specified|TBA|
|C05|**ERROR**|ANY|At least one contact must have 'submitter' role specified |TBA|
|C06|**ERROR**|HTS_ONLY|A contact with 'submitter' role must have affiliation specified|TBA|
|C07|**ERROR**|ANY|At least one contact with 'submitter' role must have an email specified|TBA|
|C08|WARNING|ANY|A contact should have first name specified|TBA|
|C09|WARNING|ANY|A contact should have an affiliation specified|TBA|
|C10|WARNING|ANY|A contact role(s) should have a term source specified|TBA|

## Experimental Design Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|ED01|WARNING|MICRO_ARRAY_ONLY|Micro-array experiment must have at least one experimental design specified|TBA|
|ED02|WARNING|MICRO_ARRAY_ONLY|An experimental design should be defined by a term|TBA|
|ED03|WARNING|MICRO_ARRAY_ONLY|An experimental design term should have a term source|TBA|

## Experimental Factor Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|EF01|**ERROR**|ANY|An experiment must have at least one experimental factor specified|TBA|
|EF02|**ERROR**|ANY|An experimental factor must have name specified|TBA|
|EF03|WARNING|ANY|An experimental factor should have a type specified|TBA|
|EF04|WARNING|ANY|An experimental factor type should have term source specified|TBA|

## General Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|G01|**ERROR**|ANY|Experiment title must be specified|TBA|
|G02|**ERROR**|ANY|Experiment description must be specified|TBA|
|G04|**ERROR**|ANY|Date of Experiment must be in 'YYYY-MM-DD' format|TBA|
|G05|**ERROR**|ANY|Experiment public release date must be specified|TBA|
|G06|**ERROR**|ANY|Experiment public release date must be in 'YYYY-MM-DD' format|TBA|
|G07|**ERROR**|ANY|Reference to the SDRF file must be specified|TBA|
|G08|**ERROR**|ANY|Reference to the SDRF file must be valid file location|TBA|
|G03|WARNING|ANY|Date of Experiment should be specified|TBA|
|G09|WARNING|ANY|Experiment description should be at least 50 characters long|TBA|

## Normalization Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|NT01|WARNING|ANY|An experiment should have at least one normalization type specified|TBA|
|NT02|WARNING|ANY|A normalization type should be defined by a term|TBA|
|NT03|WARNING|ANY|A normalization type should have term source specified|TBA|

## Publication Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PB02|**ERROR**|ANY|PubMed Id must be numeric|TBA|
|PB01|WARNING|ANY|A publication should have at least one of PubMed ID or Publication DOI specified|TBA|
|PB03|WARNING|ANY|A publication authors should be specified|TBA|
|PB04|WARNING|ANY|A publication title should be specified|TBA|
|PB05|WARNING|ANY|A publication status should be specified|TBA|
|PB06|WARNING|ANY|A publication status should have term source specified|TBA|

## Protocol Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PR01|**ERROR**|ANY|At least one protocol must be used in an experiment|TBA|
|PR02|**ERROR**|ANY|Name of a protocol must be specified|TBA|
|PR03|**ERROR**|ANY|A protocol type must be specified|TBA|
|PR08|**ERROR**|HTS_ONLY|Library construction protocol is required for HTS submissions|TBA|
|PR09|**ERROR**|HTS_ONLY|Sequencing protocol is required for HTS submissions|TBA|
|PR04|WARNING|ANY|A protocol type should have term source specified|TBA|
|PR05|WARNING|ANY|Description of a protocol should be specified|TBA|
|PR06|WARNING|ANY|Description of a protocol should be over 50 characters long|TBA|
|PR07|WARNING|ANY|A protocol should have parameters|TBA|

## Quality Control Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|QC01|WARNING|ANY|An experiment should have at least one quality control type specified|TBA|
|QC02|WARNING|ANY|A quality control type should be defined by a term|TBA|
|QC03|WARNING|ANY|A quality control type should have term source specified|TBA|

## Replicate Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|RT01|WARNING|ANY|An experiment should have at least one replicate type specified|TBA|
|RT02|WARNING|ANY|A replicate type should be defined by a term|TBA|
|RT03|WARNING|ANY|A replicate type should have term source specified|TBA|

## Term Source Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|TS01|**ERROR**|ANY|Name of a term source must be specified|TBA|
|TS02|**ERROR**|ANY|Name of a term source must be unique|TBA|
|TS03|WARNING|ANY|URL/File of a term source should be specified|TBA|
|TS04|WARNING|ANY|Version of a term source should be specified|TBA|
