# Weave Extension Point - AuthHandler

## Quick Overview

### Definition

{ AuthControl, AuthHandler, Auth }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-AUTH-HANDLER.png)

### Extension Category Group Name

SERVICE_AUTH_HANDLER

### META-INF Template

META-INF/weave/extensions.json

```json
[
    {
        "group": "SERVICE_AUTH_HANDLER",
        "name": "type name of AuthHandler implementation",
        "baseType": "io.aftersound.weave.security.AuthHandler",
        "type": "implementation class name of io.aftersound.weave.security.AuthHandler"
    }
]
```

### Applicable Scopes

- service

### Description

This extension point allows new authentication and authorization mechanism to be added for service. 

- AuthControl, instructions on how authentication/authorization should be conducted
- AuthHandler, acts on AuthControl to authenticate and/or authorize given token/credential bearer
- Auth, the result of authentication and/or authorization conducted by AuthHandler 

## Component Development Guide

TODO