# Weave Extension Point - FileHandler

## Quick Overview

### Definition

{ FileHandlingControl, FileHandler, Void }

### Diagram

![](diagrams/WEAVE-EXTENSION-POINT-FILE-HANDLER)

### Extension Category Name

file-handler

### META-INF Template

META-INF/weave/file-handler-extensions.json

```json
{
  "category": "file-handler",
  "baseType": "io.aftersound.weave.filehandler.FileHandler",
  "types": [
    "FileHandler.implementation"
  ]
}
```

### Applicable Scope

- batch

### Description

This extension point allows listing/copying/deleting data files from/to a distributed file storage system, previously unsupported,
to be supported

- FileHandlingControl, instruction for FileHandler to act upon
- FileHandler, copy/delete files from source file storage to target file storage as instructed
- Void, depends on which action is involved

## Component Development Guide

Assume you'd like to make Weave Batch Framework to support cloud storage system "Box" and required Weave Data Client 
Factory component for "Box" is already developed and installed.

- start a Java project or use your existing project and create a new module
- include following dependency with scope *provided*, in the pom of the module, together with other dependencies needed.
Also make sure the pom asks for packaging jar with dependencies.
  
```xml
<groupId>io.xyz</groupId>
<artifactId>box-filehandler</artifactId>
<version>1.0.0</version>

<properties>
    <weave.version>0.0.1-SNAPSHOT</weave.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.aftersound.weave</groupId>
        <artifactId>weave-filehandler-core</artifactId>
        <version>${weave.version}</version>
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
- create a class which extends FileHandlingControl
  
```java
package io.xyz.filehandler;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.WithDataClientReference;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.filehandler.WithFileFilterControl;

public class BoxFileHandlingControl implements FileHandlingControl, WithDataClientReference, WithFileFilterControl {

    static final NamedType<FileHandlingControl> TYPE = NamedType.of(
            "Box",
            BoxFileHandlingControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String clientId;
    private FileFilterControl fileFilterControl;

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public FileFilterControl getFileFilterControl() {
        return fileFilterControl;
    }

    public void setFileFilterControl(FileFilterControl fileFilterControl) {
        this.fileFilterControl = fileFilterControl;
    }
}
```
- create a class which extends FileHandler, together with facility classes
  
```java
package io.xyz.filehandler;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.filehandler.FileFilter;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandler;
import io.aftersound.weave.filehandler.FileHandlingControl;
import org.apache.knox.gateway.shell.KnoxSession;

import java.util.List;

public class BoxFileHandler extends FileHandler<BoxClient, BoxFileHandlingControl> {

    public static final NamedType<FileHandlingControl> COMPANION_CONTROL_TYPE = BoxFileHandlingControl.TYPE;

    private final BoxClient boxClient;

    public BoxFileHandler(
            DataClientRegistry dataClientRegistry,
            ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory,
            BoxFileHandlingControl control) {
        super(dataClientRegistry, fileFilterFactory, control);
        this.boxClient = dataClientRegistry.getClient(control.getClientId());
    }

    @Override
    public Result listFiles() {
        // use boxClient to list files with instructions in given BoxFileHandlingControl
    }

    @Override
    public Result copyFileFrom(String sourceFilePath, String targetFilePath) {
        // use boxClient to specific file
    }

    @Override
    public Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory) {
        // use boxClient to specific files to local directory
    }

    @Override
    public Result deleteFile(String filePath) {
        // use boxClient to delete specific file in Box cloud storage
    }

    @Override
    public Result copyLocalFileTo(String sourceFilePath, String targetFilePath) {
        // use boxClient to copy local file (where file handling job runs) to Box cloud storage
    }

    @Override
    public Result copyLocalFilesTo(List<String> sourceFilePaths, String targetDirectory) {
        // use boxClient to copy local file (where file handling job runs) to Box cloud storage
    }
}
```
- include a Weave file-handler-extensions.json file under resources/META-INF/weave
  
```json
{
  "category": "file-filter",
  "baseType": "io.aftersound.weave.filehandler.FileHandler",
  "types": [
    "io.xyz.filehandler.BoxFileHandler"
  ]
}
```
- compile, test, package, install, and deploy. Your component is ready to be installed into Weave deployment for 
integration test.
- install the component in Weave deployment for integration test purpose , and restart all Weave instances which have 
the component installed.
  
```html
http://WEAVE_INSTANCE:PORT/admin/batch/extension/install?repository=maven://MAVEN_REPOSITORY_URL&groupId=io.xyz&artifactId=box-filehandler&version=1.0.0
  
```
- next, create a simple data file transfer job spec
  
```html
POST: http://WEAVE_INSTANCE:PORT/admin/service-metadata/create  
```
  
```json
{
  "type": "FT",
  "id": "ft-box-to-local",
  "dataSourceControls": [
    {
      "type": "Box",
      "id": "box123",
      "options": {
        "o1": "1",
        "o2": "2"
      }
    }
  ],
  "sourceControl": {
    "type": "Box",
    "directory": "/my-team-storage/data",
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
- also, a batch app config that recognizes box file handler is needed. Assume below is right in place, associated 
name is file-transfer-app-config
  
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
    "io.xyz.datasource.BoxDataSourceControl"
  ],
  "extractControlTypes": [],
  "transformControlTypes": [],
  "loadControlTypes": [],
  "dataClientFactoryTypes": [
    "io.xyz.dataclient.BoxDataClientFactory"
  ],
  "fileHandlerTypes": [
    "io.xyz.filehandler.BoxFileHandler"
  ],
  "fileFilterTypes": [
    "io.xyz.filefilter.ExtBasedFileFilter"
  ]
}
```
- make call to job run service to trigger a job and all the data files with extensions csv, tsv, dat should be copied 
from Box cloud storage over to local /data/target, where local means the machine runs the job.
  
```html
http://WEAVE_INSTANCE:PORT/job/run?appConfig=file-transfer-app-config&jobSpec=ft-box-to-local
```
- once tested, it's good to install it in production Weave deployment