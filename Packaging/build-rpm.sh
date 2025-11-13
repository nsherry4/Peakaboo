#!/bin/bash

RELEASE_TYPE=${1:-release}

jpackage @version @platform/common/args @platform/common/args-${RELEASE_TYPE} @platform/linux/args @platform/linux/args-${RELEASE_TYPE} --type rpm
