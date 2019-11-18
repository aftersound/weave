# Weave Extension Point - KeyGenerator

## Quick Overview

### Definition

{ KeyControl, KeyGenerator, Object }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-CACHE-KEY-GENERATOR.png)

### Extension Category Name

key-generator

### META-INF Template

META-INF/weave/key-generator-extensions.json

```json
{
  "category": "cache-key-generator",
  "baseType": "io.aftersound.weave.cache.KeyGenerator",
  "types": [
    "KeyGenerator.implementation"
  ]
}
```

### Applicable Scope

- service

### Description

This extension point allows new cache key generation behavior to be supported for service response cache.

- KeyControl, instructions on how a cache key should be generated
- KeyGenerator, generate cache key in according to KeyControl
- Key, cache key created by KeyGenerator

## Component Development Guide

TODO