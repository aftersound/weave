# To do list

## Common Extensions to-do-list
### {Endpoint,DataClientFactory,DataClient} extensions
- {Endpoint,CassandraSessionFactory,Session} for Cassandra
- {Endpoint,ElasticsearchClientFactory,Client} for Elasticsearch
- {Endpoint,KnoxSessionFactory,KnoxSession} for HDFS via Apache Knox gateway, complete the implementation
- {Endpoint,FileSystemFactory,FileSystem} for HDFS via webHDFS api, complete the implementation
- {Endpoint,SwiftAccountFactory,Account} for Open Swift Object storage, complete the implementation
- {Endpoint,CephAccountFactory,Account} for Ceph Object storage, complete the implementation

## {Void,DataFormat,Serializer/Deserializer} extensions
- {Void,AvroDataFormat,Serializer/Deserializer}
- {Void,BsonDataFormat,Serializer/Deserializer}
- {Void,KyroDataFormat,Serializer/Deserializer}
- {Void,Protobuf3DataFormat,Serializer/Deserializer}
- {Void,ThriftDataFormat,Serializer/Deserializer}

## Weave Service to-do-list

### Weave Service Framework Core
1. Integrate with SecurityEnforcer core, goal is to support ServiceMetadata level SecurityControl. General idea is to 
- to plug SecurityEnforcer extensions into Spring Security filter
- inject ServiceMetadata level security policy into Spring Security filter.

2. Status Code handling

3. DataClientRegistry and DataClientManager finetune data client lifecycle management

4. ServiceMetadata.id pattern matching

5. Introduce namespace

### Weave Service Extensions

#### {ServiceMetadata/ExecutionControl,ServiceExecutor,Response} extensions
- CouchbaseServiceExecutor, complete the implementation
- CassandraServiceExecutor, design ExecutionControl and ServiceExecutor
- CompositeServiceExecutor
- ElasticsearchServiceExecutor
- CalciteSQLServiceExecutor, to provide SQL interface for databases doesn't support SQL

#### {CacheControl,CacheFactory,Cache} extensions
- {GuavaCacheControl,GuavaCacheFactory,LoadingCache}, a service response cache using Guava cache
- {OHCacheControl,OHCacheFactory,OHCache}, a service response cache using Off-heap cache

#### SecurityEnforcer core {SecurityControl,SecurityEnforcer,Boolean}

## Weave Batch to-do-list

### Weave Batch Framework Core
1. introduce namespace

### Weave Batch Extensions

#### {JobSpec,JobWorker,Void} extensions
- {FETLJobSpec,FETLJobWorker,Void}, complete the implementation
- {FTJobSpec,FTJobWorker,Void}, testing
