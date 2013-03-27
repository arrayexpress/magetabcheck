# IDF checks
(updated: 27/03/2013 14:34)

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
|C01|**error**|all|At least one contact must be specified|TBA|
|C02|**error**|all|A contact must have last name specified|TBA|
|C03|**error**|all|At least one contact must have email specified|TBA|
|C04|**error**|all|At least one contact must have a role specified|TBA|
|C05|**error**|all|At least one contact must have 'submitter' role specified |TBA|
|C06|**error**|ht|A contact with 'submitter' role must have affiliation specified|TBA|
|C07|**error**|all|At least one contact with 'submitter' role must have an email specified|TBA|
|C08|warning|all|A contact should have first name specified|TBA|
|C09|warning|all|A contact should have an affiliation specified|TBA|
|C10|warning|all|A contact role(s) should have a term source specified|TBA|

## Experimental Design Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|ED01|warning|micro-array|Micro-array experiment must have at least one experimental design specified|TBA|
|ED02|warning|micro-array|An experimental design should be defined by a term|TBA|
|ED03|warning|micro-array|An experimental design term should have a term source|TBA|

## Experimental Factor Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|EF01|**error**|all|An experiment must have at least one experimental factor specified|TBA|
|EF02|**error**|all|An experimental factor must have name specified|TBA|
|EF03|warning|all|An experimental factor should have a type specified|TBA|
|EF04|warning|all|An experimental factor type should have term source specified|TBA|

## General Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|G01|**error**|all|Experiment title must be specified|TBA|
|G02|**error**|all|Experiment description must be specified|TBA|
|G04|**error**|all|Date of Experiment must be in 'YYYY-MM-DD' format|TBA|
|G05|**error**|all|Experiment public release date must be specified|TBA|
|G06|**error**|all|Experiment public release date must be in 'YYYY-MM-DD' format|TBA|
|G07|**error**|all|Reference to the SDRF file must be specified|TBA|
|G08|**error**|all|Reference to the SDRF file must be valid file location|TBA|
|G03|warning|all|Date of Experiment should be specified|TBA|
|G09|warning|all|Experiment description should be at least 50 characters long|TBA|

## Normalization Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|NT01|warning|all|An experiment should have at least one normalization type specified|TBA|
|NT02|warning|all|A normalization type should be defined by a term|TBA|
|NT03|warning|all|A normalization type should have term source specified|TBA|

## Publication Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PB02|**error**|all|PubMed Id must be numeric|TBA|
|PB01|warning|all|A publication should have at least one of PubMed ID or Publication DOI specified|TBA|
|PB03|warning|all|A publication authors should be specified|TBA|
|PB04|warning|all|A publication title should be specified|TBA|
|PB05|warning|all|A publication status should be specified|TBA|
|PB06|warning|all|A publication status should have term source specified|TBA|

## Protocol Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PR01|**error**|all|At least one protocol must be used in an experiment|TBA|
|PR02|**error**|all|Name of a protocol must be specified|TBA|
|PR03|**error**|all|A protocol type must be specified|TBA|
|PR08|**error**|ht|Library construction protocol is required for HTS submissions|TBA|
|PR09|**error**|ht|Sequencing protocol is required for HTS submissions|TBA|
|PR04|warning|all|A protocol type should have term source specified|TBA|
|PR05|warning|all|Description of a protocol should be specified|TBA|
|PR06|warning|all|Description of a protocol should be over 50 characters long|TBA|
|PR07|warning|all|A protocol should have parameters|TBA|

## Quality Control Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|QC01|warning|all|An experiment should have at least one quality control type specified|TBA|
|QC02|warning|all|A quality control type should be defined by a term|TBA|
|QC03|warning|all|A quality control type should have term source specified|TBA|

## Replicate Type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|RT01|warning|all|An experiment should have at least one replicate type specified|TBA|
|RT02|warning|all|A replicate type should be defined by a term|TBA|
|RT03|warning|all|A replicate type should have term source specified|TBA|

## Term Source Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|TS01|**error**|all|Name of a term source must be specified|TBA|
|TS02|**error**|all|Name of a term source must be unique|TBA|
|TS03|warning|all|URL/File of a term source should be specified|TBA|
|TS04|warning|all|Version of a term source should be specified|TBA|
