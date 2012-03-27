#!/bin/bash


# 에뮬레이터 기기의 /system/lib 에 있는
# 시스템 라이브러리들
# 쓰기 위해서는 직접 목록을 추출해서 쓸것
libs=`adb shell "ls /system/lib/*.so;ls /system/lib/bluez-plugin/*.so;ls /system/lib/egl/*.so;ls /system/lib/hw/*.so;ls /system/lib/soundfx/*.so" | tr -d ^M | perl -pe "s/\r\n/ /"`
#libs="...."

mkdir "system-lib"
for lib in $libs
do
    #라이브러리를 지역 파일 시스템으로 가져온다
	echo $lib
    adb pull "$lib" ./system-lib/
done
