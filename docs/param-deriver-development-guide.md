# How to develop component extending Weave Parameter Deriver

## About Weave Parameter Deriver Extension Point

This allows new behavior of deriving parameter from another to be supported
- It is defined as {DeriveControl, Deriver, List}. 
- Its applicable scope is service, which means a component implements this extension point can be used in Weave Service 
Framework runtime.

## Efforts involved in create a parameter deriver component

Assume you'd like to make Weave Service Framework to support deriving parameter by BASE64 decoding a parameter with
BASE64 encoded value.

- start a Java project or use your existing project and create a new module
- include following dependency with scope *provided*, in the pom of the module, together with other dependencies needed.
Also make sure the pom asks for packaging jar with dependencies.
```xml

<groupId>io.xyz</groupId>
<artifactId>base64-param-deriver</artifactId>
<version>1.0.0</version>

<dependencies>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>weave-service-core</artifactId>
        <version>${project.version}</version>
        <scope>provided</scope>
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
- create a class which extends DeriveControl
```java
package io.xyz.param;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.DeriveControl;

public class Base64DeriveControl extends DeriveControl {

    static final NamedType<DeriveControl> TYPE = NamedType.of(
            "Base64",
            Base64DeriveControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
```
- create a class which extends Deriver, together with facility classes
```java
package io.xyz.param;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.util.Base64;
import java.util.List;

public class Base64Deriver implements Deriver {

    public static final NamedType<DeriveControl> COMPANION_CONTROL_TYPE = Base64Deriver.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public List<String> derive(DeriveControl deriveControl, ParamValueHolder sourceValueHolder) {
        List<String> decodedValues = new ArrayList<>();
        for (String rawValue : sourceValueHolder.getRawValues()) {
            try {
                String decoded = Base64.getDecoder().decode(rawValue);
                decodedValues.add(decoded);
            } catch (Exception e) {
                // TODO
            }
            decodedValues.add();
        }
        return decodedValues;
    }
}
```
- include a Weave param-deriver-extensions.json file under resources/META-INF/weave
```json
{
  "category": "param-deriver",
  "baseType": "io.aftersound.weave.service.request.Deriver",
  "types": [
    "io.xyz.param.Base64Deriver"
  ]
}

```
- compile, test, package, install, and deploy. Your component is ready to be installed into Weave deployment for 
integration test.
- install the component in Weave deployment for integration test purpose , and restart all Weave instances which have 
the component installed.
```html
http://WEAVE_INSTANCE:PORT/admin/service/extension/install?repository=maven://MAVEN_REPOSITORY_URL&groupId=io.xyz&artifactId=base64-param-deriver&version=1.0.0
```
- next, create a service metadata for DemoServiceExecutor
```html
POST: http://WEAVE_INSTANCE:PORT/admin/service-metadata/create  
```
```json
{
  "id": "/base64-deriver-verification",
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
      "name": "q1",
      "valueType": "String",
      "type": "Query",
      "multiValued": false,
      "constraint": {
        "type": "Required"
      }
    },
    {
      "name": "d1",
      "valueType": "String",
      "type": "Derived",
      "multiValued": false,
      "constraint": {
        "type": "Required"
      },
      "deriveControl": {
        "type": "Base64",
        "from": "q1"
      }
    }
  ],
  "executionControl": {
    "type": "Demo"
  }
}
```
- Weave service runtime periodically scans for changes of service metadata, it'll see this service metadata and load it.
- Lastly, make a service call to verify if the deriver works as expected.
```html
http://WEAVE_INSTANCE:PORT/base64-deriver-verification?q1=aGVsbG8=
The response echos back parsed parameters, "d1": "hello" is expected to be there.
```
- once tested, it's good to install it in production Weave deployment