# About Weave Data Client Extension Point
Weave data client extension point allows new database/data storage to be supported. 
- Weave Data Client Extension Point is defined as {Endpoint, DataClientFactory, DataClient}. 
- Its applicable scopes include both service and batch, which means a component implements this extension point can be 
used in both Weave Service Framework runtime and Weave Batch Framework runtime.

## Efforts involved in create a data client component

Assume you'd like to make Weave to support a new type of data storage or database, called MyDB, and you want to create 
a component for that, here is how.

- start a Java project or use your existing project and create a new module
- include following 2 dependencies with scope *provided*, together with MyDB client SDK, in the pom of the module. Also 
make sure the pom asks for packaging jar with dependencies.
```xml

<groupId>io.xyz</groupId>
<artifactId>weave-extension-mydb</artifactId>
<version>1.0.0</version>

<dependencies>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>weave-dataclient-core</artifactId>
        <version>${weave.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>weave-misc</artifactId>
        <version>${weave.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>mydb</groupId>
        <artifactId>mydbsdk</artifactId>
        <version>${mydbsdk.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <archive/>
                <finalName>${project.artifactId}</finalName>
            </configuration>
            <executions>
                <execution>
                    <id>with-dependencies</id>
                    <configuration>
                        <appendAssemblyId>false</appendAssemblyId>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                        <finalName>${project.artifactId}-with-dependencies-${project.version}</finalName>
                    </configuration>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
- create a class which extends DataClientFactory, together with facility classes
```java
package io.xyz.mydb;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import MyDBClient;

public class MyDBClientFactory extends DataClientFactory<MyDBClient> {
    
    // Must be included
    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of(
        "MyDB",
        Endpoint.class
    );
    
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of(
        "MyDB", 
        MyDBClient.class
    );
    
    public FileSystemFactory(DataClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }
    
    @Override
    protected MyDBClient createDataClient(Map<String, Object> options) {
        // create MyDBClient with given options
    }
    
    @Override
    protected void destroyClient(MyDBClient client) {
        // shut down client
    }
    
}
```
- include a Weave data-client-factory-extensions.json file under resources/META-INF/weave
```json
{
  "category": "data-client-factory",
  "baseType": "io.aftersound.weave.dataclient.DataClientFactory",
  "types": [
    "io.xyz.mydb.MyDBClientFactory"
  ]
}

```
- compile, test, package, install, and deploy. Your component is ready to be installed into Weave deployment for 
integration test.
- install the component in Weave deployment for integration test purpose , and restart all Weave instances which have 
the component installed.
http://WEAVE_INSTANCE:PORT/admin/service/extension/install?repository=maven://MAVEN_REPOSITORY_URL&groupId=io.xyz&artifactId=weave-extension-mydb&version=1.0.0
http://WEAVE_INSTANCE:PORT/admin/batch/extension/install?repository=maven://MAVEN_REPOSITORY_URL&groupId=io.xyz&artifactId=weave-extension-mydb&version=1.0.0
- if you want service runtime of Weave deployment to connect to a MyDB cluster, use data client config management service
to create a config.  
POST: http://WEAVE_INSTANCE:PORT/admin//data-client-config/create  
BODY:  
```json
{
  "type": "MyDB",
  "id": "mydb-client-123",
  "options": {
    "servers": "host1:port,host2:port",
  }
}
```
- Weave service runtime periodically scans for changes of data client configs, it'll see this config and call 
MyDBClientFactory to create an instance of MyDBClient and place it into DataClientRegistry. Any ServiceExecutor can 
understands MyDBClient can get hold of the instance of MyDBClient by following code.
```java
DataClientRegistry dataClientRegistry = managedResources.getResource(DataClientRegistry.class.getName(), DataClientRegistry.class);

// assume ExecutionControl.myDBClientId is defined for this imagined ServiceExecutor
MyDBClient client = dataClientRegistry.getClient(executionControl.getMyDBClientId());
```
- once tested, it's good to install it in production Weave deployment