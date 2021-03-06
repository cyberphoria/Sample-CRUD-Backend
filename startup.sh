#!/bin/bash
# Java download code from: https://replit.com/@SimerLol/Minecraft-Server-Template?v=1#Startup.sh

set -e
root=$PWD

export JAVA_HOME=/usr/lib/jvm/java-1.11.0-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

download() {
    set -e
    echo By executing this script you agree to the JRE License, Maven License
    echo and the licenses of all packages used \in this project.
    # echo Press Ctrl+C \if you \do not agree to any of these licenses.
    # echo Press Enter to agree.
    # read -s agree_text
    # echo Thank you \for agreeing, the download will now begin.
    wget -O jre.tar.gz "https://javadl.oracle.com/webapps/download/AutoDL?BundleId=242050_3d5a2bb8f8d4428bbe94aed7ec7ae784"
    tar -zxf jre.tar.gz
    rm -rf jre.tar.gz
    mv ./jre* ./jre
    echo JRE downloaded
    echo "Download complete" 
}

require() {
    if [ ! $1 $2 ]; then
        echo $3
        echo "Running download..."
        download
    fi
}
require_file() { require -f $1 "File $1 required but not found"; }
require_dir()  { require -d $1 "Directory $1 required but not found"; }
require_env() {
  if [[ -z "${!1}" ]]; then
    echo "Environment variable $1 not set. "
    echo "Make a new secret called $1 and set it to $2"
    exit
  fi
}
require_executable() {
    require_file "$1"
    chmod +x "$1"
}

# java
require_dir "jre"
require_executable "jre/bin/java"

# Set path to present working directory
PATH=$PWD/jre/bin:$PATH

echo "Exit code $?"
