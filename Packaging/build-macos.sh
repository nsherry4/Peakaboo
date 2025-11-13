#!/bin/bash

RELEASE_TYPE=${1:-release}

jpackage @version @platform/common/args @platform/common/args-${RELEASE_TYPE} @platform/macos/args @platform/macos/args-${RELEASE_TYPE}
