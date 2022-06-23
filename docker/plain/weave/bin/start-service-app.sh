#!/bin/bash
cd ..
weavehome=`pwd`

# prepare classpath
classpath=''
# core libraries
for file in $weavehome/service/lib/core/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

# extension/schema libraries
for file in $weavehome/service/lib/ext/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

# extra libraries required by extensions and schemas
for file in $weavehome/service/lib/extra/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

java -DWEAVE_HOME=$weavehome -XX:TieredStopAtLevel=1 -XX:TieredStopAtLevel=1 -noverify -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dfile.encoding=UTF-8 -classpath "$classpath" -Dspring.config.location=$weavehome/service/$APPLICATION.properties io.aftersound.weave.service.Application