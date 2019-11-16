#!/bin/bash

MAIN_DIR="build/classes/kotlin/main"
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

pushd "$DIR/$MAIN_DIR" >> /dev/null
kotlin oktavia.MainKt "$@"
popd >> /dev/null
