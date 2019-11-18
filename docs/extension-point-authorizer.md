# Weave Extension Point - Authorizer

## Quick Overview

### Definition

{ AuthorizationControl, Authorizer, Authorization }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-AUTHORIZER.png)

### Extension Category Name

authorizer

### META-INF Template

META-INF/weave/authorizer-extensions.json

```json
{
  "category": "authorizer",
  "baseType": "io.aftersound.weave.security.Authorizer",
  "types": [
    "Authorizer.implementation"
  ]
}
```

### Applicable Scope

- service

### Description

This extension point allows new authorization mechanism to be supported for service. 

- AuthorizationControl, instructions on how authorization check should be conducted
- Authorizer, conduct authorization check in according to AuthorizationControl
- Authorization, result of authorization check

## Component Development Guide

TODO