#!/usr/bin/env bash

function build_so(){
    ANDROID_MK=Android.mk
    APPLICATION_MK=Application.mk
    /mnt/jjfly_c/android/android-ndk-r9d-x86_64/ndk-build -j8 NDK_DEBUG=0
    #-platforms
    cp -R ../libs/* ../src/main/assets/
}
build_so
