## Weave Service to-do-list

### {ServiceMetadata/ExecutionControl,ServiceExecutor,Response} extensions
- CouchbaseServiceExecutor, complete the implementation
- CassandraServiceExecutor, design ExecutionControl and ServiceExecutor
- ComsositeServiceExecutor
- ElasticsearchServiceExecutor
- CalciteSQLServiceExecutor, to provide SQL interface for databases doesn't support SQL

### SecurityEnforcer core {SecurityControl,SecurityEnforcer,Boolean}

### Weave Service Framework Core
1. Integrate with SecurityEnforcer core, goal is to support ServiceMetadata level SecurityControl. General idea is to 
- to plug SecurityEnforcer extensions into Spring Security filter
- inject ServiceMetadata level security policy into Spring Security filter.

2. Status Code handling

3. DataClientRegistry and DataClientManager finetune data client lifecycle management

## {Endpoint,DataClientFactory,DataClient} extension to-do-list
- {Endpoint,CassandraSessionFactory,Session} for Cassandra
- {Endpoint,ElasticsearchClientFactory,Client} for Elasticsearch
- {Endpoint,KnoxSessionFactory,KnoxSession} for HDFS via Apache Knox gateway, complete the implementation
- {Endpoint,FileSystemFactory,FileSystem} for HDFS via webHDFS api, complete the implementation
- {Endpoint,SwiftAccountFactory,Account} for Open Swift Object storage, complete the implementation
- {Endpoint,CephAccountFactory,Account} for Ceph Object storage, complete the implementation

## {Void,DataFormat,Void} extension to-do-list
- {Void,AvroDataFormat,Void}
- {Void,BsonDataFormat,Void}
- {Void,KyroDataFormat,Void}
- {Void,Protobuf3DataFormat,Void}
- {Void,ThriftDataFormat,Void}
- {Void,XMLDataFormat,Void}