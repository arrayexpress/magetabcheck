# SDRF checks (64)
(updated: 26/04/2013 15:50)

+ [Array Design Attribute Checks](#array-design-attribute-checks)
+ [Array Data Matrix Node Checks](#array-data-matrix-node-checks)
+ [Array Data Node Checks](#array-data-node-checks)
+ [Assay Node Checks](#assay-node-checks)
+ [Characteristic Attribute Checks](#characteristic-attribute-checks)
+ [Derived Array Data Matrix Node Checks](#derived-array-data-matrix-node-checks)
+ [Derived Array Data Node Checks](#derived-array-data-node-checks)
+ [Extract Node Checks](#extract-node-checks)
+ [Factor Value Attribute Checks](#factor-value-attribute-checks)
+ [Label Attribute Checks](#label-attribute-checks)
+ [Labeled Extract Node Checks](#labeled-extract-node-checks)
+ [Material Type Attribute Checks](#material-type-attribute-checks)
+ [Normalization Node Checks](#normalization-node-checks)
+ [Protocol Node Checks](#protocol-node-checks)
+ [Parameter Value Attribute Checks](#parameter-value-attribute-checks)
+ [Scan Node Checks](#scan-node-checks)
+ [Sample Node Checks](#sample-node-checks)
+ [Source Node Checks](#source-node-checks)
+ [Technology type Checks](#technology-type-checks)
+ [Unit Attribute Checks](#unit-attribute-checks)

## Array Design Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|AD01|**error**|Both|An array design attribute must have name specified||
|AD03|**error**|Both|Term source of an array design attribute must be declared in IDF||
|AD04|**error**|HTS|There are must not be any array design attributes in HTS experiment||
|AD02|warning|Both|An array design should have term source specified||

## Array Data Matrix Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|ADMN01|**error**|Both|An array data matrix node must have name specified||
|ADMN02|**error**|Both|Name of an array data matrix node must be valid file location||
|ADMN03|warning|Both|An array data matrix node should be described by a protocol||

## Array Data Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|ADN01|**error**|Both|An array data node must have a name||
|ADN02|**error**|Both|Name of an array data node must be a valid file location||
|ADN03|warning|Both|An array data node should be described by a protocol||

## Assay Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|AN01|**error**|Both|An assay node must have name specified||
|AN02|**error**|Both|An assay node must have 'Technology Type' attribute specified||

## Characteristic Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|CA03|**error**|Both|Term source of a characteristic attribute must be declared in IDF||
|CA01|warning|Both|A characteristic attribute should have name specified||
|CA02|warning|Both|A characteristic attribute should have term source specified||

## Derived Array Data Matrix Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|DADMN01|**error**|Both|A derived array data matrix node must have name specified||
|DADMN02|**error**|Both|Name of derived data matrix node must be valid file location||
|DADMN03|warning|Both|A derived array data matrix node should be described by protocol||

## Derived Array Data Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|DADN01|**error**|Both|A derived array data node must have name specified||
|DADN02|**error**|Both|Name of a derived array data node must be a valid file location||
|DADN03|warning|Both|A derived array data node should be described by a protocol||

## Extract Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|EX01|**error**|Both|An extract node must have name specified||
|EX02|warning|Both|An extract node should have 'Material Type' attribute specified||
|EX03|warning|Both|An extract node should be described by a protocol||

## Factor Value Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|FV03|**error**|Both|Term source of a factor value attribute must be declared in IDF||
|FV01|warning|Both|A factor value attribute should have name specified||
|FV02|warning|Both|A factor value attribute should have term source specified||

## Label Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|L03|**error**|Micro-array|Term source of a label attribute must be defined in IDF||
|L01|warning|Micro-array|A label attribute should have name specified||
|L02|warning|Micro-array|A label attribute should have term source specified||

## Labeled Extract Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|LE01|**error**|HTS|There are must not be any Labeled Extract nodes in HTS experiments||
|LE02|**error**|Micro-array|A labeled extract node must have name specified||
|LE04|**error**|Micro-array|A labeled extract node must have 'Label' attribute specified||
|LE03|warning|Micro-array|A labeled extract node should have 'Material Type' attribute specified||
|LE05|warning|Micro-array|A labeled extract node should be described by a protocol||

## Material Type Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|MT03|**error**|Both|Term source of a material type attribute must be defined in IDF||
|MT01|warning|Both|A material type attribute should have name specified||
|MT02|warning|Both|A material type attribute should have term source specified||

## Normalization Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|NN01|warning|Both|A normalization node should have a name||

## Protocol Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PN01|**error**|Both|A protocol node must have name specified||
|PN03|**error**|Both|A protocol's date must be in 'YYYY-MM-DD' format||
|PN05|**error**|Both|Term source value of a protocol node must be defined in IDF||
|PN06|**error**|HTS|A protocol must have 'performer' attribute specified||
|PN04|warning|Both|A protocol node should have term source specified||
|PN07|warning|Micro-array|A protocol should have 'performer' attribute specified||

## Parameter Value Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|PV01|warning|Both|A parameter value attribute (of a protocol) should have name specified||
|PV02|warning|Both|A parameter value attribute (of a protocol) should have unit specified||

## Scan Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|SC02|**error**|HTS|An SDRF graph must have at least one scan node||
|SC01|warning|Both|A scan node should have name specified||

## Sample Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|SM01|**error**|Both|A sample node must have name specified||
|SM02|warning|Both|A sample node should have 'Material Type' attribute specified||
|SM03|warning|Both|A sample node should be described by a protocol||

## Source Node Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|SR01|**error**|Both|A source node must have name specified||
|SR04|**error**|Both|A source node must have an 'Organism' characteristic specified||
|SR02|warning|Both|A source node should have 'Material Type' attribute specified||
|SR03|warning|Both|A source node should have 'Provider' attribute specified||
|SR05|warning|Both|A source node should have more than 2 characteristic attributes||
|SR07|warning|Both|A source node should be described by a protocol||

## Technology type Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|TT01|**error**|Both|Technology type attribute must have name specified||
|TT03|**error**|Both|Term source of a technology type attribute must be defined in IDF||
|TT02|warning|Both|Technology type attribute should have term source specified||

## Unit Attribute Checks

|Ref|Modality|Type|Title|Details|
|---|--------|----|-----|-------|
|UA03|**error**|Both|Term source of a unit attribute must be declared in IDF||
|UA01|warning|Both|A unit attribute should have name specified||
|UA02|warning|Both|A unit attribute should have term source specified||
