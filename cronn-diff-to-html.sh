#!/usr/bin/env bash

BASE_DIR=`dirname "$(readlink -f "$0")"`

_COMMAND_ARGS=( $@  )

let _OPT_NUMBER=${#}

for ((i=0; i<${_OPT_NUMBER}; i++)); do  
  
  if [ -e ${_COMMAND_ARGS[i]} ]; then
  	_ELEMENT=$(readlink -f ${_COMMAND_ARGS[i]})  
  else
  	_ELEMENT=${_COMMAND_ARGS[i]}  
  fi
  
  _COMPLETE_PATHS=(${_COMPLETE_PATHS[@]} ${_ELEMENT})
done

_COMMAND_ARGS=(${_COMPLETE_PATHS[@]})

cd "$BASE_DIR"

if [[ ${#} -gt 1 ]]; then
  _OPTS=$(sed '{s/ /\x27,\x27/g;s/^/-Parguments=[\x27/g;s/$/\x27]/g;}'<<<${_COMMAND_ARGS[@]})
  ./gradlew --daemon run ${_OPTS}
else
  ./gradlew --daemon run 
fi

