# Micro-service Virtualization over { CONTROL, ACTOR, PRODUCT }

Let's start with an example how micro-service virtualization works in Weave.

## An Example

Imagine there is a data set about beer samples sitting in a Couchbase cluster (thanks, Couchbase!), and there is a need to create 
micro-service in order for clients to access it. How to do it? Here is Weave way.

**Assumption:**  

- [Couchbase cluster](https://hub.docker.com/_/couchbase): { name: test, nodes: 192.168.1.150, username: user, password: password, bucket: beer-sample }
- [Sonatype Nexus repository](https://hub.docker.com/r/sonatype/nexus): { 192.168.1.151:8081 } has all required Weave extension components and schema libraries

### 1.start Weave instances

```html  
docker container run -d -p 8080:8080 aftersound/weave:0.0.1-SNAPSHOT --name weave
```
  
### 2.install Weave extensions

- install data client extension to make Weave Service Framework runtime capable of connecting to Couchbase cluster
  
```html
http://localhost:8080/admin/service/extension/install?repository=maven://192.168.1.151:8081/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-dataclient-couchbase&version=0.0.1-SNAPSHOT
```

- install service executor extension to make Weave Service Framework runtime capable of serving data out of Couchbase cluster
  
```html
http://localhost:8080/admin/service/extension/install?repository=maven://192.168.1.151:8081/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-service-couchbase&version=0.0.1-SNAPSHOT
```

- install data format extension to make Weave Service Framework runtime capable of deserialize data from Couchbase cluster

```html
http://localhost:8080/admin/service/extension/install?repository=maven://192.168.1.151:8081/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-dataformat-json&version=0.0.1-SNAPSHOT
```

- install schema library to make Weave Service Framework runtime capable of understanding schema of beer sample data

```html
http://localhost:8080/admin/service/schema/install?repository=maven://192.168.1.151:8081/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-schema-samples&version=0.0.1-SNAPSHOT
```

### 3.restart Weave Instances
  
```html 
docker stop weave
docker restart weave
```
  
Once restarted, installed extensions and schema libraries are visible to Weave Service Framework runtime

### 4.create data client configs

#### Couchbase cluster data client config
  
```html
POST: http://localhost:8080/admin/data-client-config/create
```
  
```json
{
  "type": "CouchbaseCluster",
  "id": "cluster.test",
  "options": {
    "nodes": "192.168.1.150",
    "username": "user",
    "password": "password"
  }
}
```
  
Once created, Weave Service Framework runtime will see the config and connect to specified Couchbase cluster and obtain an instance of
Cluster, which is Couchbase cluster level client API object.

#### Couchbase bucket data client config
  
```html
POST: http://localhost:8080/admin/data-client-config/create
```
  
```json
{
  "type": "CouchbaseBucket",
  "id": "cluster.test.beer-sample",
  "options": {
    "clusterId": "cluster.test",
    "bucket": "beer-sample"
  }
}
```
  
Once created, Weave Service Framework runtime will see the config and obtain an instance of Bucket, which is Couchbase bucket level client 
API object, linked with specified bucket at remote.
  
### 5.create service metadata
  
```html
POST: http://localhost:8080/admin/service-metadata/create
```
  
```json
{
  "id": "/beer-sample/brewer",
  "paramFields": [
    {
      "name": "p1",
      "valueType": "String",
      "type": "Path",
      "multiValued": false,
      "constraint": {
        "type": "Required"
      }
    },
    {
      "name": "p2",
      "valueType": "String",
      "type": "Path",
      "multiValued": false,
      "constraint": {
        "type": "Required"
      }
    },
    {
      "name": "id",
      "valueType": "String",
      "type": "Query",
      "multiValued": false,
      "constraint": {
        "type": "Required"
      }
    }
  ],
  "executionControl": {
    "type": "Couchbase",
    "repository": {
      "id": "cluster.test.beer-sample",
      "getControl": {
        "timeout": 50
      }
    },
    "byKey": {
      "keyTemplate": "@{id}",
      "schemaSelector": "default",
      "schemas": {
        "default": {
          "format": "JSON",
          "schema": "io.aftersound.weave.schema.samples.Brewer"
        }
      }
    }
  }
}
```
  
Once Weave Service Framework runtime sees the service metadata, a service at URI /beer-sample/brewer is virtualized/realized
  
### 6.first call to virtualized micro-service
  
```html
http://localhost:8080/beer-sample/brewer?id=21st_amendment_brewery_cafe
```
  
## The Power of Service Virtualization
  
The service virtualization implemented in Weave is powerful in many ways, the most important one is it makes change easy.

- if another client prefers a different endpoint, simply create a new service metadata.
- if service needs to be decommissioned, simply delete the service metadata.
- if data gets migrated to another Couchbase cluster, simply introduce a new data client config and update the service metadata to reference 
new one or simply update existing data client config.
- if Couchbase cluster and the network allows lower read latency, simply update the service metadata.

Since change is easier/faster, building and operating services becomes easier/faster. 

You might ask where the power comes from? Answers are available at these 2 articles,

- [An extensible component structure built around { CONTROL, ACTOR, PRODUCT }](https://aftersound.github.io/weave/control-actor-product-component-structure)
- [Lifecycle Management under {CONTROL,ACTOR,PRODUCT} Component Structure](https://aftersound.github.io/weave/lifecycle-management-under-cap-component-structure)

## Conclusion

The article showcases the feasibility of (Service) Component as a Service and (Service) Virtualization over (Service) Component and Weave 
presents a reference implementation. 
