# Weave Extension Point - ComponentFactory

## Quick Overview

### Definition

{ ComponentConfig, ComponentFactory, COMPONENT }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-COMPONENT-FACTORY.png)

### Extension Category Group Name

COMPONENT_FACTORY

### META-INF Template

META-INF/weave/extensions.json

```json
[
    {
        "group": "COMPONENT_FACTORY",
        "name": "unique component type name",
        "baseType": "io.aftersound.component.ComponentFactory",
        "type": "implementation class name of io.aftersound.component.ComponentFactory"
    }
]
```

### Applicable Scopes

- services
- job runners

### Description

This extension point allows new type of components to be added, especially those connectors which allow Weave service 
app to be able to connect and communicate with external systems.

- ComponentConfig, contains configuration/instruction for ComponentFactory to create COMPONENT accordingly
- ComponentFactory, acts on ComponentConfig and create COMPONENT
- COMPONENT, the component created by ComponentFactory

## Component Development Guide

Assume you'd like to make Weave to support a new type of data storage or database, called MyDB, and you want to create 
a component for that, here is how.

- start a Java project or use your existing project and create a new module
- include following Weave dependencies with scope *provided*, together with MyDB client SDK, in the pom of the module. Also 
make sure the pom asks for packaging jar with dependencies.

```xml
<groupId>io.xyz</groupId>
<artifactId>client-mydb</artifactId>
<version>1.0.0</version>

<dependencies>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>actor-core</artifactId>
        <version>${weave.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>component-core</artifactId>
        <version>${weave.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>config</artifactId>
        <version>${weave.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>misc</artifactId>
        <version>${weave.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>mydb</groupId>
        <artifactId>mydbsdk</artifactId>
        <version>${mydbsdk.version}</version>
    </dependency>
</dependencies>
```

- create a class which extends DataClientFactory, together with facility classes

```java
package io.xyz.mydb;

import io.aftersound.common.NamedType;
import io.aftersound.weave.config.Config;

public class MyDBClientFactory extends SimpleComponentFactory<MyDBClient> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("MyDB", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    MyDBClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected MyDBClient createComponent(Config config) {
        return new MyDBClient();
    }

    @Override
    protected void destroyComponent(MyDBClient myDBClient, Config config) {
        myDBClient.shutdown();
    }
}
```

- include a Weave extensions.json file under resources/META-INF/weave

```json
[
    {
        "group": "COMPONENT_FACTORY",
        "name": "MyDB",
        "baseType": "io.aftersound.component.ComponentFactory",
        "type": "io.xyz.mydb.MyDBClientFactory"
    }
]
```
- compile, test, package, install, and deploy. Your component is ready to be installed into Weave deployment for 
integration test.
- install the component in Weave deployment for integration test purpose , and restart all Weave instances which have 
the component installed.
- if you want service runtime of Weave deployment to connect to a MyDB cluster, use data client config management service
to create a config.

```html
POST: http://WEAVE_INSTANCE:PORT/admin/data-client-config/create  
```

```json
{
  "type": "MyDB",
  "id": "mydb-client-123",
  "options": {
    "servers": "host1:port,host2:port"
  }
}
```
- Once Weave service runtime sees this config and it'll call MyDBClientFactory to create an instance of MyDBClient 
and place it into ClientRegistry. Any ServiceExecutor which understands MyDBClient can get hold of the instance of 
MyDBClient by following code.

```java
// assume ExecutionControl.myDBClientId is defined for this imagined ExecutionControl
MyDBClient client = componentRepository.getComponent(executionControl.getMyDBClientId());
```

- once tested, it's good to install it in production Weave deployment