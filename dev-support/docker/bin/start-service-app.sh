#!/bin/bash
cd ..
weavehome=`pwd`

# prepare classpath
classpath=''
# service libraries
for file in $weavehome/service/library/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

# admin service extensions
for file in $weavehome/service/admin-extension/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

# service extensions
for file in $weavehome/service/extension/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

# service schema libraries
for file in $weavehome/service/schema/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

java -DWEAVE_HOME=$weavehome -XX:TieredStopAtLevel=1 -XX:TieredStopAtLevel=1 -noverify -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dfile.encoding=UTF-8 -classpath "$classpath" io.aftersound.weave.service.WeaveServiceApplication