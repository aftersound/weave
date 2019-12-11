# Micro-service Virtualization over { CONTROL, ACTOR, PRODUCT } - An Example

Imagine there is a data set about beer samples sitting in a Couchbase cluster (thanks, Couchbase!), and there is a need 
to create micro-service in order for clients to access it. How to do it? 

The Weave way is through micro-service virtualization. Here is an example. 

## An example with less steps

**What you need:**  

- Docker Desktop installed your computer
- [Weave](https://hub.docker.com/r/aftersound/weave)
- [Couchbase cluster](https://hub.docker.com/_/couchbase)

### 1.set up Couchbase

```html
docker pull docker:latest
docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase:latest
```

In browser, open Couchbase administration console.  

```html
http://localhost:8091
```

- follow the instruction to initiate Couchbase cluster
- create sample bucket bundled with Couchbase: beer-sample  
- create user for bucket beer-sample, make user as user, make password as password

### 2.start Weave instance

```html  
docker container run -d -p 8080:8080 aftersound/weave:bundled-0.0.1-SNAPSHOT --name weave-bundled
``` 

You might see error message saying it cannot connect to some Couchbase cluster, which is fine because data client 
config is not correct for your environment.

### 3.update data client config

**Firstly, check available data client config**  

GET: http://localhost:8080/admin/service/data-client-config/list

**Secondly, update data client config for your Couchbase cluster**  

```html
PUT: http://localhost:8080/admin/service/data-client-config/update
```

BODY:  
```json
{
    "type": "CouchbaseCluster",
    "id": "cluster.test",
    "options": {
        "nodes": "YOUR_MACHINE_IP",
        "username": "user",
        "password": "password"
    }
}
```

If everything is correct, started Weave instance should be able to connect to your Couchbase cluster. You could tell 
from the prompt in terminal console for that Weave instance.

### 4.make a call to virtualized micro-service
  
```html
http://localhost:8080/beer-sample/brewery?id=21st_amendment_brewery_cafe
```

## Same example start from scratch

**What you need with some assumptions:**  

- Docker Desktop installed your computer
- [Weave](https://hub.docker.com/r/aftersound/weave): { server: http://localhost:8080 }
- [Couchbase cluster](https://hub.docker.com/_/couchbase): { name: test, nodes: 192.168.1.150, username: user, password: password, bucket: beer-sample }
- [Nexus Repository OSS](https://oss.sonatype.org/#nexus-search;quick~io.aftersound.weave): has all required Weave extension components and schema libraries

### 1.start Weave instances

```shell script
docker pull aftersound/weave:0.0.1-SNAPSHOT
docker container run -d -p 8080:8080 aftersound/weave:0.0.1-SNAPSHOT --name weave-clean
```
  
### 2.install Weave extensions

- install data client extension to make Weave Service Framework runtime capable of connecting to Couchbase cluster
  
```html
http://localhost:8080/admin/service/extension/install?repository=mavens://oss.sonatype.org/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-dataclient-couchbase&version=0.0.1-SNAPSHOT
```

- install service executor extension to make Weave Service Framework runtime capable of serving data out of Couchbase cluster
  
```html
http://localhost:8080/admin/service/extension/install?repository=mavens://oss.sonatype.org/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-service-couchbase&version=0.0.1-SNAPSHOT
```

- install data format extension to make Weave Service Framework runtime capable of deserialize data from Couchbase cluster

```html
http://localhost:8080/admin/service/extension/install?repository=mavens://oss.sonatype.org/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-dataformat-json&version=0.0.1-SNAPSHOT
```

- install schema library to make Weave Service Framework runtime capable of understanding schema of beer sample data

```html
http://localhost:8080/admin/service/schema/install?repository=mavens://oss.sonatype.org/nexus/content/repositories/snapshots/&groupId=io.aftersound.weave&artifactId=weave-schema-samples&version=0.0.1-SNAPSHOT
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
POST: http://localhost:8080/admin/service/data-client-config/create
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
POST: http://localhost:8080/admin/service/data-client-config/create
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
POST: http://localhost:8080/admin/service/metadata/create
```
  
```json
{
  "path": "/beer-sample/brewery",
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
      "schema": {
        "selector": "brewery",
        "selections": {
          "brewery": {
            "format": "JSON",
            "schema": "io.aftersound.weave.schema.samples.Brewery"
          }
        }
      }
    }
  }
}
```
  
Once Weave Service Framework runtime sees the service metadata, a service at URI /beer-sample/brewer is virtualized/realized
  
### 6.first call to virtualized micro-service
  
```html
http://localhost:8080/beer-sample/brewery?id=21st_amendment_brewery_cafe
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

## Additional Examples

[Micro-service Virtualization - Demo](https://github.com/aftersound/weave/wiki/Micro-service-Virtualization-Demo) has 
more examples using the very same service executor extension.