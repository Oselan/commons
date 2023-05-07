# Getting Started
This module provides a unified API for creating and managing application lookup tables.  
It allows integrating read only enums to be retrieved using the same API. 
It supports localization out of the box.  

### Features 

* Supports Database Lookup tables and Enum(ReadOnly) lookups 
* Returns lookup values localized to language requested in accept-lang header
* Management allows adding any number of localizations for each lookup value. 
* Extensible:  supports adding lookups in other modules easily by registering new lookup type Enums on the lookup service. 
* Auto-create lookup tables controlled by a property.


### Integration Guide
The following guides illustrate how to use the features:

- To add a database lookup 
  - Add entity table to your schema OR set property app.lookups.auto-create=true 
  - Define entity class , cachable & extending BaseLookupEntity
  - Define repository wrapper class - spring component to contain all entities repositories
  - Define entity repository extending BaseLookupRepository

- To add an enum/ read-only lookup 
  - Define enum implementing ILookupEnum

- Common steps
  - Define one or more enum of lookup Types implementing ILookupTypeEnum to list all database and read-only lookups
  - Register lookup types enums with the lookup service




