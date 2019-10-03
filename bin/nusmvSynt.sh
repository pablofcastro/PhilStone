#!/bin/bash

if [ "$1" == "-h" ]; then
  echo "Usage: `basename $0` [scope] [name of the file with the spec]"
  exit 0
fi

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

export PhilStone='../'
if ! [ -x "$(command -v nuSMV)" ]; then
  echo 'Error: nuSMV is not installed.' >&2
  exit 1
fi

if [[ "$OSTYPE" == "darwin"* ]]; then
	export JAVA_LIBRARY_PATH='../lib/MacOs/'
fi

java PS/PhilStone -NuSMV -scope=$1 -pdf $2