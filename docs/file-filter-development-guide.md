# How to develop component extending Weave File Filter

## About Weave File Filter Extension Point

File filter is used by file handling. 

This allows new file filtering behavior to be supported.
- It is defined as {FileFilterControl, FileFilter, Void}. 
- Its applicable scope is batch, which means a component implements this extension point can be used in Weave Batch 
Framework runtime.

## Efforts involved in create a file filter component

Assume you'd like to make Weave Batch Framework to support filtering data file simply by file extensions.

- start a Java project or use your existing project and create a new module
- include following dependency with scope *provided*, in the pom of the module, together with other dependencies needed.
Also make sure the pom asks for packaging jar with dependencies.  

```xml
  
<groupId>io.xyz</groupId>
<artifactId>ext-based-file-filter</artifactId>
<version>1.0.0</version>

<dependencies>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>weave-filefilter-core</artifactId>
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

- create a class which extends FileFilterControl  
  
```java
package io.xyz.filefilter;

import io.aftersound.weave.common.NamedType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtBasedFileFilterControl implements FileFilterControl {

    static final NamedType<FileFilterControl> TYPE = NamedType.of(
            "ExtBased",
            ExtBasedFileFilterControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private List<String> fileExtensions;
    
    public List<String> getFileExtensions() {
        return fileExtensions;
    }
    
    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }
}
```

- create a class which extends FileFilter, together with facility classes
  
```java
package io.xyz.filefilter;

import io.aftersound.weave.common.NamedType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtBasedFileFilter extends FileFilter<ExtBasedFileFilterControl> {

    public static final NamedType<FileFilterControl> COMPANION_CONTROL_TYPE = ExtBasedFileFilterControl.TYPE;

    private final Set<String> fileExtSet;

    public ExtBasedFileFilter(ExtBasedFileFilterControl filterControl) {
        super(filterControl);

        List<String> fileExtensions = filterControl.getFileExtensions();
        Set<String> fileExtSet = fileExtensions != null ? new HashSet<String>(fileExtensions) : Collections.<String>emptySet();
        this.fileExtSet = fileExtSet;
    }

    @Override
    public boolean accept(String candidateFilePath) {
        return filePathSet.contains(getFileExtension(candidateFilePath));
    }
    
    private String getFileExtension(String filePath) {
        // extract file extension
    }
}
```

- include a Weave file-filter-extensions.json file under resources/META-INF/weave
  
```json
{
  "category": "file-filter",
  "baseType": "io.aftersound.weave.filehandler.FileFilter",
  "types": [
    "io.xyz.filehandler.ExtBasedFileFilter"
  ]
}

```

- compile, test, package, install, and deploy. Your component is ready to be installed into Weave deployment for 
integration test.
- install the component in Weave deployment for integration test purpose , and restart all Weave instances which have 
the component installed.
  
```html
http://WEAVE_INSTANCE:PORT/admin/batch/extension/install?repository=maven://MAVEN_REPOSITORY_URL&groupId=io.xyz&artifactId=ext-based-file-filter&version=1.0.0
```

- next, create a simple data file transfer job spec
  
```html
POST: http://WEAVE_INSTANCE:PORT/admin/service-metadata/create  
```
  
```json
{
  "type": "FT",
  "id": "ext-file-filter-test-job-spec",
  "sourceControl": {
    "type": "FS",
    "directory": "/data/source",
    "fileFilterControl": {
      "type": "ExtBased",
      "fileExtensions": [
        "csv",
        "tsv",
        "dat"
      ]
    }
  },
  "targetControl": {
    "type": "FS",
    "directory": "/data/target"
  }
}
```

- also, a batch app config that recognizes ext-based file filter is needed. Assume below is right in place, associated 
name is local-file-transfer-app-config
  
```json
{
  "springDataSourceConfig": {
    "spring.datasource.driver-class-name": "org.mariadb.jdbc.Driver",
    "spring.datasource.url": "jdbc:mysql://localhost:3306/weave",
    "spring.datasource.username": "user",
    "spring.datasource.password": "password"
  },
  "jsonSpecTypes": [
    "io.aftersound.weave.batch.jobspec.ft.FTJobSpec"
  ],
  "dataSourceControlTypes": [
  ],
  "extractControlTypes": [],
  "transformControlTypes": [],
  "loadControlTypes": [],
  "dataClientFactoryTypes": [
  ],
  "fileHandlerTypes": [
    "io.aftersound.weave.fs.FSFileHandler"
  ],
  "fileFilterTypes": [
    "io.xyz.filefilter.ExtBasedFileFilter"
  ]
}
```
  
- make call to job run service to trigger a job and all the data files with extensions csv, tsv, dat should be copied 
from /data/source over to /data/target
  
```html
http://WEAVE_INSTANCE:PORT/job/run?appConfig=local-file-transfer-app-config&jobSpec=ext-file-filter-test-job-spec
```

- once tested, it's good to install it in production Weave deployment