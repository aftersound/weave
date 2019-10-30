# Micro-service Virtualization over {ServiceMetadata/ExecutionControl,ServiceExecutor,Response}


Imagine there is a data set about beer samples sitting in a Couchbase cluster, and there is a need to create 
micro-service in order for clients to access it. How are you going to do it?

Here is Weave way.

## Start Weave instances
docker container run -d -p 8080:8080 aftersound/weave:0.0.1-SNAPSHOT

## Install Weave extensions
http://localhost:8080/admin/service/extension/install?

## Restart Weave Instances
docker container 

## Create Service Metadata
