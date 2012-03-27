############################################################
# 제 5장 예제를 위한 Makefile
############################################################

# 안드로이드 소스 : GL C 헤더들이 들어있다
SYS_DEV=$HOME/project/WORKING

# 컴파일러 스크립트
CC	= agcc

# 링커 스크립트
LINKER 	= ald


# C 파일들
MAIN_OBJS	= cuberenderer.o cube.o

# 라이브러리 이름, 동적 실행파일
LIB	= libgltest_jni.so
DYN	= gl-dyn

# 컴파일 옵션들과 라이브러리들
INCLUDES	= -I$(SYS_DEV)/frameworks/base/opengl/include
CFLAGS		= -DNORMALUNIX	-DLINUX	-DANDROID
LIBS		= -lGLESv1_CM	-lui

# 주 라이브러리 libgltest_jni.so 를 만든다
lib: $(MAIN_OBJS)	#jni
	@echo
	$(LINKER) -shared -o $(LIB) $(MAIN_OBJS) $(LIBS)
	@echo Done. Out file is $(LIB)

# JNI헤더들을 생성한다.
jni:
	@echo "Creating JNI C headers ..."
	javah -jni -classpath ../bin/classes/ -d include opengl.jni.Natives

# 시험용 동적 실행파일을 만든다
dyn:
	$(CC) -c test.c $(INCLUDES)
	$(LINKER) -o $(DYN) test.o -lgltest_jni -L.
	@echo
	@echo Done. Out file is $(DYN)
	@echo

.c.o:
	@echo
	$(CC) -Wall -O2 -fpic -c $(CFLAGS) $(INCLUDES) $<

# 라이브러리 파일을 기기의 /data 폴더로 올린다.
deplot-test:
	@echo "Deploying $(LIB) to /data"
	adb push $(LIB) /data

clean:
	rm -f *.o


