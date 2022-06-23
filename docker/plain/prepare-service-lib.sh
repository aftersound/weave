#!/bin/bash
set -ex

# find and copy jar files as listed in 'service-lib-core.list'
while IFS= read -r jar
do
	find ~/.m2/repository -type f -path */$jar -exec cp {} ./service-lib/core \;
done < "service-lib-core.list"

# find and copy jar files as listed in 'service-lib-ext.list'
while IFS= read -r jar
do
	find ~/.m2/repository -type f -path */$jar -exec cp {} ./service-lib/ext \;
done < "service-lib-ext.list"

# find and copy jar files as listed in 'service-lib-extra.list'
while IFS= read -r jar
do
	find ~/.m2/repository -type f -path */$jar -exec cp {} ./service-lib/extra \;
done < "service-lib-extra.list"