#!/bin/bash
set -ex

# delete temporary directory if exists
if [ -d "./service-lib" ]
then
  rm -r ./service-lib
fi
