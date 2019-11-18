# Weave Extension Point - CacheFactory

## Quick Overview

### Definition

{ CacheControl, CacheFactory, Cache }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-CACHE-FACTORY.png)

### Extension Category Name

cache-factory

### META-INF Template

META-INF/weave/cache-factory-extensions.json

```json
{
  "category": "cache-factory",
  "baseType": "io.aftersound.weave.cache.CacheFactory",
  "types": [
    "CacheFactory.implementation"
  ]
}
```

### Applicable Scope

- service

### Description

This extension point allows new cache to supported for service response cache to speed up slow service.

- CacheControl, instructions on how a Cache should be created and its behavior
- CacheFactory, creates Cache in according to CacheControl
- Cache, created by CacheFactory

## Component Development Guide

TODO