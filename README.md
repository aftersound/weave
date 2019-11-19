## About Weave  

Weave is a Java-based declarative service/batch framework targeting data area, especially [data insight delivery](https://aftersound.github.io/weave/data-insight-delivery-solution).

## Frameworks and CAP Component Structure

Weave has 2 independent frameworks, each built on top of an extensible component structure called CAP 
[{CONTROL, ACTOR, PRODUCT}](https://aftersound.github.io/weave/control-actor-product-component-structure)

- [service framework](https://aftersound.github.io/weave/micro-service-virtualization-over-cap-closer-look), which enables micro-service virtualization
- batch job framework, which supports declarative jobs

Each framework has a minimalist core, which is formed by stitching a set of CAP cores (or extension points). Neither 
provides any functionality, with designed expectation that actual functionality would be provided by installed 
components which implement/extend those CAP cores.  

## Extension Points

Weave defines a set of core concepts in form of {CONTROL, ACTOR, PRODUCT}. Typically, each of these trinities is defined
 as interface/abstract classes and naturally is an extension point. All the controls associated these core concepts and 
 their implementations make Weave highly declarative (configuration-driven or metadata driven).

Below are the list if extension points.

* Common Extension Points
  * [{ Endpoint, DataClientFactory, DataClient }](https://aftersound.github.io/weave/extension-point-data-client-factory)  
  * [{ DataFormatControl, DataFormat, Serializer/Deserializer }](https://aftersound.github.io/weave/extension-point-data-format)

* Service Specific Extension Points
  * [{ ExecutionControl, ServiceExecutor, Response }](https://aftersound.github.io/weave/extension-point-service-executor)  
  * [{ ParamFields, ParameterProcessor, ParamValueHolders }](https://aftersound.github.io/weave/extension-point-parameter-processor)  
  * [{ DeriveControl, Deriver, List }](https://aftersound.github.io/weave/extension-point-param-deriver)  
  * [{ CacheControl, CacheFactory, Cache }](https://aftersound.github.io/weave/extension-point-cache-factory)  
  * [{ KeyControl, KeyGenerator, Key }](https://aftersound.github.io/weave/extension-point-key-generator)  
  * [{ AuthenticationControl, Authenticator, Authencation }](https://aftersound.github.io/weave/extension-point-authenticator)  
  * [{ AuthorizationControl, Authorizer, Authorization }](https://aftersound.github.io/weave/extension-point-authorizer)  

* Batch Specific Extension Points
  * [{ JobSpec, JobWorker, VOID }](https://aftersound.github.io/weave/extension-point-job-worker)  
  * [{ FileHandlingControl, FileHandler, VOID }](https://aftersound.github.io/weave/extension-point-file-handler)  
  * [{ FileFilterControl, FileFilter, VOID }](https://aftersound.github.io/weave/extension-point-file-filter)  

## Managed Extensions

Check [weave-managed-extension](https://github.com/aftersound/weave-managed-extensions) for managed extension components.

## Weave Deployment

A Weave deployment can have a several, tens, hundreds, even thousands of Weave instances, and each Weave deployment can 
have its own unique sets of extension components, virtualized micro-services and declared jobs.

## Docker Images

Latest build of Docker image is available at [Docker Hub](https://hub.docker.com/r/aftersound/weave).

## Disclaimer
Weave is in early phase, it is still very buggy and not ready for product usage yet.



