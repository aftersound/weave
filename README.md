## About Weave  
Weave is a Java-based, highly extensible, highly metadata-driven service/batch framework built around simple idea {CONTROL, ACTOR, PRODUCT}.  
- CONTROL, controls the behavior of ACTOR 
- ACTOR, acts in according to CONTROL, processes input and produces PRODUCT  
- PRODUCT, produced by ACTOR  

Weave defines a set of core concepts, each consists of 3 sub-concepts, {CONTROL, ACTOR, PRODUCT}. Typically, each of these trinities is defined as interface/abstract classes and naturally is an extension point, and better yet all the controls associated these core concepts and their implementations make Weave highly configuration-driven (or metadata driven).  

Below lists some of the core concepts.
- {Endpoint, DataClientFactory, DataClient}. Given an Endpoint, DataClientFactory creates/updates/destroys DataClient.
- {ParamFields, ParameterProcessor, ParamValueHolders}. Under instructions of ParamFields, ParameterProcessor processes request and produces ParamValueHolders.
- {CacheControl, CacheFactory, Cache}. Under instructions of CacheControl, CacheFactory creates/destroys Cache.
- {ServiceMetadata/ExecutionControl, ServiceExecutor, Response}. With instructions of ExecutionControl in ServiceMetadata, ServiceExecutor processes request and produces Response.
- {JobSpec, JobRunner, VOID}. JobRunner runs a job as commanded by JobSpec.
- {FileHandlingControl, FileHandler, VOID}. FileHandler handles data files in according to FileHandlingControl.
- {FileFilterControl, FileFilter, VOID}. FileFilter filters data files in according to FileFilterControl.
- etc.  

Weave has a minimalist core structure which weaves/stitches these core concepts to form service framework and batch framework respectively. Neither of which provides any functionality, with designed expectation that actual functionality would be provided by components which implement/extend core concepts.  

Take one example to illustrate how Weave functionality could be enriched, which describes the from-scratch works/steps involved in order to make a Weave deployment to support Couchbase and be able to connect to a Couchbase cluster.
- a component which implements/extends {Endpoint, DataClientFactory, DataClient}, say it consists of {Endpoint, CouchbaseBucketFactory, Bucket}
- the component is properly packaged with Weave extension meta-info
- the component package is installed into Weave deployment using built-in extension management services. Once installed, both Weave Service Framework runtime and Batch Framework runtime in the deployment can support Couchbase after restart of Weave instances.
- a concrete Endpoint control, which is specific to target Couchbase Cluster, needs to be defined, in JSON or part of a bigger JSON as enclosing control, and be deployed. Once Weave runtime sees the control, it'll interact with CouchbaseBucketFactory and create an instance of Bucket which connects to target Couchbase cluster.
- the data client instance,  is accessible by identifier defined in the concrete Endpoint.
- when the control is deleted, the Bucket instance will be destroyed and the Weave deployment cuts the connection to target Couchbase cluster. When the control is updated for another Couchbase cluster, it will connect to that Couchbase cluster.

Each Weave deployment can have its own unique sets of components and defines functionalities by CRUD controls. 
- The Service Framework runtime in a  Weave deploy can install a set of components which implements {ServiceMetadata/ExecutionControl, ServiceExecutor, Response}, in order to create services by CRUD ServiceMetadata(s) on top of installed service components.  
- The Batch Framework runtime in a  Weave deployment can install a set of components which implement {Endpoint, DataClientFactory, DataClient} and {JobSpec, JobRunner, VOID} and define jobs which transfers data among Hadoop cluster, Swift Object Storage cluster, Amazon S3, etc., do data processing or submit Spark jobs to target Spark cluster, etc., by CRUD JobSpec(s).

## How it works
Try docker image to get a feeling of how it works.

## Disclaimer
Weave is in early phase, it is still very buggy and not ready for product usage yet.

## Docker
https://hub.docker.com/r/aftersound/weave
