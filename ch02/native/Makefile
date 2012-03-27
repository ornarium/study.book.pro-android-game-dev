############################################################
# Makefile
############################################################
# 컴파일러와 링커
CC = agcc
LD = ald

# 플래그
CFLAGS = -Werror # 컴파일러가 발견한 경고가 오류로 처리
MACROS =
INCLUDES =

# 정적 프로그램의 목적 파일
OBJ = main.o

# 공유 라이브러리의 목적 파일
LIBOBJ = lib.o

# 공유 라이브러리 시험용
TESTLIBOBJ = testlib.o


# make의 기본 대상. 공유 라이브러리와 시험용 프로그램 a.out도 빌드
all: testlib $(OBJ)
	@echo
	@echo "Linking..."
	$(LD) -static -o a.out $(OBJ)

# 공유 라이브러리를 빌드한다
lib: $(LIBOBJ)
	@echo
	@echo "Linking Shared Library..."
	$(LD) -shared -o libch02.so $(LIBOBJ)
	@echo
	#@echo "Copying Shared library to assets folder"
	#cp libch02.so ../assets
	@echo "Copying Shared library to libs folder"
	mkdir -p ../libs/armeabi/
	cp libch02.so ../libs/armeabi/

# 시험용 프로그램을 빌드한다
testlib: lib $(TESTLIBOBJ)
	@echo
	@echo "Linking Test for Sharedd library"
	$(LD) -o testlib $(TESTLIBOBJ) -L. -lch02

# 공유 라이브러리를 위한 JNI 헤더 생성. 프로젝트 생성시 ../bin 이 아니라
# ../bin/classes 아래에 생성되는 경우가 있음
jni:
	@echo "Creating JNI C hearders..."
	javah -jni -classpath ../bin/classes/ -d include jni.Natives

# 컴파일
.c.o:
	@echo
	@echo "Compiling $<..."
	$(CC) -c $(FLAGS) $(MACROS) $(INCLUDES) $<

# 정리
clean:
	rm *.o

############################################################
# 시험용 프로그램을 기기에 넣는다
# but, nexus s(2.3.3) 에서 테스트해본결과 /data에 넣을수 없다
# 보안상 막힌듯...
############################################################
pushbin:
	adb push testlib /data

#라이브러리와 시험용 프로그램을 기기에 넣는다 
pushlib:
	adb push testlib /data
	adb push libch02.so /data



