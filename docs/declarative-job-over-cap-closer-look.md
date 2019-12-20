# Declarative Job over { CONTROL, ACTOR, PRODUCT } - Closer Look

(Before anything, let's make CAP = { CONTROL, ACTOR, PRODUCT }.)

Quick intro.

## High Level View

Let's start from the high level view of single Weave instance.

![](diagrams/WEAVE-JOB-ARCHITECTURE-HIGH-LEVEL.png)

At high level, the job part of each Weave instance consists of 3 layers,
- at the bottom, it's the runtime of Weave Job Framework Core which is centered around a set of CAP core concepts.
- on top of framework core runtime, runs a layer of extension components, each implements one CAP extension point.
- at the top layer, runs jobs which are declaratively defined by job specs

With that, let's move on to quickly walk through job extension points, which makes job framework extremely 
extensible and also makes declarative jobs possible.

## Job Extension Points

### 1. {Endpoint, DataClientFactory, DataClient}
![](diagrams/WEAVE-EXTENSION-POINT-DATA-CLIENT-FACTORY.png)

- Endpoint, contains connection parameters to obtain data client for target database/data storage system
- DataClientFactory, acts on Endpoint and create data client of target database/data storage system
- DataClient, data client created by DataClientFactory

## Job Framework in action

[Lifecycle Management under CAP Component Structure](https://aftersound.github.io/weave/lifecycle-management-under-cap-component-structure) 
offers a general explanation on how extension libiraries, extension components, controls, control metadata, etc. are 
managed. It's highly recommended to read it if you haven't, since that article covers some mechanism while this does not.

### Initialization

Before a Weave instance could run any job, it needs to be properly initialized. Just like boot process of a 
modern operating system has several stages, initialization of Weave job execution runtime  has more than 1 phase, 
each phase has a purpose for next phase to be successful.

#### Phase 1


### Phase 2

### Run Job

## Declarative Jobs over JobSpec(s)


### Structure of JobSpec


### Example of JobSpec
  
```json
{
}
```
  
## Conclusion


## Last but not the least

Weave is open source and there are a lot ToDo to make it useful.

[Weave Framework Core ToDo](https://github.com/aftersound/weave/issues)  
[Weave Extensions ToDo](https://github.com/aftersound/weave-managed-extensions/issues)

All contributions are welcome!
