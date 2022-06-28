#!/bin/bash
cd ..
weavehome=`pwd`

classpath=''
for file in $weavehome/lib/service/*; do
  if [[ $file == *.jar ]]
  then
    classpath+=$file
    classpath+=":"
  fi
done

java -DWEAVE_HOME=$weavehome -XX:TieredStopAtLevel=1 -XX:TieredStopAtLevel=1 -noverify -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dfile.encoding=UTF-8 -classpath "$classpath" -Dspring.config.location=$weavehome/service/$APPLICATION.properties io.aftersound.weave.service.Application