## About Weave  
Weave is a Java-based declarative service/batch framework. It has a component structure built around simple idea [{CONTROL, ACTOR, PRODUCT}](https://aftersound.github.io/weave/control-actor-product-component-structure), because of this, it's highly extensible.

Weave defines a set of core concepts, each consists of 3 sub-concepts, {CONTROL, ACTOR, PRODUCT}. Typically, each of these trinities is defined as interface/abstract classes and naturally is an extension point, and better yet all the controls associated these core concepts and their implementations make Weave highly declarative (configuration-driven or metadata driven).

Below lists some of them.
- [{Endpoint, DataClientFactory, DataClient}](https://aftersound.github.io/weave/data-client-factory-development-guide). Given an Endpoint, DataClientFactory creates DataClient.
- {ParamFields, ParameterProcessor, ParamValueHolders}. Under instructions of ParamFields, ParameterProcessor processes request and produces ParamValueHolders.
- {CacheControl, CacheFactory, Cache}. Under instructions of CacheControl, CacheFactory creates Cache.
- [{ServiceMetadata/ExecutionControl, ServiceExecutor, Response}](https://aftersound.github.io/weave/service-executor-development-guide). With instructions of ExecutionControl in ServiceMetadata, ServiceExecutor processes request and produces Response.
- {JobSpec, JobRunner, VOID}. JobRunner runs a job as commanded by JobSpec.
- [{FileHandlingControl, FileHandler, VOID}](https://aftersound.github.io/weave/file-handler-development-guide). FileHandler handles data files in according to FileHandlingControl.
- [{FileFilterControl, FileFilter, VOID}](https://aftersound.github.io/weave/file-filter-development-guide). FileFilter filters data files in according to FileFilterControl.
- etc.  

Weave has a minimalist core structure which stitches these core concepts to form service framework and batch framework respectively. Neither of which provides any functionality, with designed expectation that actual functionality would be provided by components which implement/extend core concepts.  

Each Weave deployment can have its own unique sets of components and defines functionalities by CRUD controls. 
- The Service Framework runtime in a  Weave deploy can install a set of components which implements {ServiceMetadata/ExecutionControl, ServiceExecutor, Response}, in order to create services by CRUD ServiceMetadata(s) on top of installed service components.  
- The Batch Framework runtime in a  Weave deployment can install a set of components which implement {Endpoint, DataClientFactory, DataClient} and {JobSpec, JobRunner, VOID} and define jobs which transfers data among Hadoop cluster, Swift Object Storage cluster, Amazon S3, etc., do data processing or submit Spark jobs to target Spark cluster, etc., by CRUD JobSpec(s).

## How it works
Try [docker image](https://hub.docker.com/r/aftersound/weave) to get a feeling of how it works.

## Disclaimer
Weave is in early phase, it is still very buggy and not ready for product usage yet.




