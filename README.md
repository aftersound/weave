## About Weave  
Weave is a Java-based declarative service/batch framework. It has a component structure built around simple idea 
[{CONTROL, ACTOR, PRODUCT}](https://aftersound.github.io/weave/control-actor-product-component-structure), because of 
this, it's highly extensible.

Weave defines a set of core concepts in form of {CONTROL, ACTOR, PRODUCT}. Typically, each of these trinities is defined
 as interface/abstract classes and naturally is an extension point, and better yet all the controls associated these 
core concepts and their implementations make Weave highly declarative (configuration-driven or metadata driven).

Below lists some of them.

* Common Extension Points
  * [{Endpoint, DataClientFactory, DataClient}](https://aftersound.github.io/weave/data-client-factory-development-guide).  
  Given an Endpoint, DataClientFactory creates DataClient.
  * {Void, DataFormat, Serializer/Deserializer}.  
  Extension point for serialize/deserialize data.

* Service Specific Extension Points
  * [{ServiceMetadata/ExecutionControl, ServiceExecutor, Response}](https://aftersound.github.io/weave/service-executor-development-guide).  
  With instructions of ExecutionControl in ServiceMetadata, ServiceExecutor processes request and produces Response.
  * {ParamFields, ParameterProcessor, ParamValueHolders}.  
  Under instructions of ParamFields, ParameterProcessor processes request and produces ParamValueHolders.
  * [{DeriveControl, Deriver, List}](https://aftersound.github.io/weave/param-deriver-development-guide).  
  Under instruction of DeriveControl, Deriver derives parameter values from source parameter.
  * {CacheControl, CacheFactory, Cache}.  
  Under instructions of CacheControl, CacheFactory creates Cache.
  * {KeyControl, KeyGenerator, Key}.  
  Under instructions of KeyControl, KeyGenerator generates key for service response.
  * {AuthenticationControl, Authenticator, Authencation}.  
  Authenticator authenticates under instructions of AuthenticationControl.
  * {AuthorizationControl, Authorizer, Authorization}.  
  Authorizer authorizes under instructions of AuthorizationControl.

* Batch Specific Extension Points
  * {JobSpec, JobWorker, VOID}.  
  JobWorker handles a job as commanded by JobSpec.
  * [{FileHandlingControl, FileHandler, VOID}](https://aftersound.github.io/weave/file-handler-development-guide).  
  FileHandler handles data files in according to FileHandlingControl.
  * [{FileFilterControl, FileFilter, VOID}](https://aftersound.github.io/weave/file-filter-development-guide).  
  FileFilter filters data files in according to FileFilterControl. 

Weave has a minimalist core which stitches these core concepts to form service framework and batch framework respectively, neither of both 
provides any functionality, with designed expectation that actual functionality would be provided by components which implement/extend core 
concepts.  

Each Weave deployment can have its own unique sets of components and defines functionality by CRUD controls. 
- The Service Framework runtime in a  Weave deployment can install a set of components which implements {ServiceMetadata/ExecutionControl, 
ServiceExecutor, Response}. Services could be created/updated/destroyed by CRUD ServiceMetadata(s).  
- The Batch Framework runtime in a Weave deployment can install a set of components which implement {Endpoint,DataClientFactory,DataClient} 
and {JobSpec, JobWorker, VOID}. Jobs could be created/updated/deleted by CRUD JobSpecs. Typical jobs include transferring data among 
distributed data storage system, processing data, submitting jobs to distribute data compute cluster, etc.

## Managed Extensions
Check [weave-managed-extension](https://github.com/aftersound/weave-managed-extensions) for managed extension components.

## Docker
Latest build of Docker image is available at [Docker Hub](https://hub.docker.com/r/aftersound/weave).

## Disclaimer
Weave is in early phase, it is still very buggy and not ready for product usage yet.




