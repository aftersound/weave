# Weave Extension Point - JobWorker

## Quick Overview

### Definition

{ JobSpec, JobWorker, Void }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-JOB-WORKER.png)

### Extension Category Name

job-worker

### META-INF Template

META-INF/weave/job-worker-extensions.json

```json
{
  "category": "job-worker",
  "baseType": "io.aftersound.weave.batch.worker.JobWorker",
  "types": [
    "JobWorker.implementation"
  ]
}
```

### Applicable Scope

- batch

### Description

This extension point allows new types of job to be supported. 

- JobSpec, instructions on how job should be conducted
- JobWorker, acts on JobSpec to do the job
- Void 

## Component Development Guide

TODO