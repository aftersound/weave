# Weave Extension Point - Authenticator

## Quick Overview

### Definition

{ AuthenticationControl, Authenticator, Authentication }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-AUTHENTICATOR.png)

### Extension Category Name

authenticator

### META-INF Template

META-INF/weave/authenticator-extensions.json

```json
{
  "category": "authenticator",
  "baseType": "io.aftersound.weave.security.Authenticator",
  "types": [
    "Authenticator.implementation"
  ]
}
```

### Applicable Scope

- service

### Description

This extension point allows new authentication mechanism to be supported for service. 

- AuthenticationControl, instructions on how authentication should be conducted
- Authenticator, acts on AuthenticationControl to authenticate token/credential bearer
- Authentication, result of authentication conducted by Authenticator 

## Component Development Guide

TODO