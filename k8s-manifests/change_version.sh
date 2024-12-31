#!/bin/bash

# default the version to latest
version="latest" 

# accept version with -v 
while getopts "v:" opt; do
  case $opt in
    v) version=$OPTARG;;
  esac
done

DIR=. # set the directory to the default "."

echo "changing k8s yaml files version ..."

for file in "$DIR"/*.y*ml; do
    if [ -f "$file" ]; then
        # skip postgres-deployment_svc.yml
        if [[ $(basename "$file") == "postgres-deployment_svc.yml" ]]; then
            echo "Skipping $file"
            continue
        fi
        sed -i "s|image:\([^:]*\):.*|image:\1:$version|" $file # update the version
        echo "$file updated"
    else
        echo "no yaml files in : $DIR"
        break
    fi
done