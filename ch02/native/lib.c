#include <stdio.h>
#include <stdlib.h>

/* JNI 헤더들 */
#include <jni.h>

#include "include/jni_Natives.h"

//C에서 호출할 메소드가 있는 클래스 이름(마침표 대신 Slash대체)
#define CB_CLASS "jni/Natives"

/*
 * OnMessage 콜백
 */
//호출할 Java 메소드 이름
#define CB_CLASS_MSG_CB "OnMessage"

/* 호출할 메소드의 서명. jni에서는 이 부분이 아주 중요
   서명은 >> (인수명세)반환형식
   I = Integer
   B = Byte
   S = Short
   C = Char
   LJava_클래스; = L과 ; 사이에 임의의 Java클래스를 지정가능
   
   인수 명세의 경우 여러 인수들의 형식을 구분자 없이 각각 한문자씩으로
   지정(L;로 지정하는 클래스는 예외)
   마지막 반환문자가 V이면 Void로 아무것도 반환하지 않는다
  */
#define CB_CLASS_MSG_SIG "(Ljava/lang/String;I)V"

//함수 원형들

//라이브러리 주 루틴
int lib_main(int argc, char **argv);

// Java 배열의 길이를 돌려주는 함수
const int getArrayLen(JNIEnv * env, jobjectArray jarray);

// Java에 돌려줄 문자열 메시지를 서식화하는 함수(printf에 해당)
void jni_printf(char *format, ...);

// 전역 환경 참조(콜백용)
static JavaVM * g_VM;

//Java쪽 네이티브 연동 클래스(jni.Natives.java) 에 대한 전역 참조
static jclass jNativesCls;

/*
 * 클래스 : jni_Natives
 * 메서드 : LibMain
 * 서명 : (Ljava/lang/String;)V
 */
  

JNIEXPORT jint JNICALL Java_jni_Natives_LibMain(JNIEnv * env,
                                                jclass class,
                                                jobjectArray jargv) {
        //호출자의 Java클래스에 대한 전역 참조를 얻는다
        (*env)->GetJavaVM(env, &g_VM);

        //Java String 배열에서 char ** 인수들을 뽑는다
        jsize clen = getArrayLen(env, jargv);

        char * args[(int) clen];
        int i;
        jstring jrow;
        for(i = 0; i < clen; ++i) {
                //Java String [i]에서 C문자열을 얻는다
                jrow = (jstring)(*env)->GetObjectArrayElement(env, jargv, i);
                const char * row = (*env)->GetStringUTFChars(env, jrow, 0);

                args[i] = malloc(strlen(row) + 1);
                strcpy(args[i], row);

                //문자열 인수를 출력한다
                jni_printf("Main arg[%d]=%s", i, args[i]);
                
                //Java 문자열 jrow를 해제한다
                (*env)->ReleaseStringUTFChars(env, jrow, row);
        }

        /*
         * jni.Natives 클래스를 적재한다
         */
        jNativesCls = (*env)->FindClass(env, CB_CLASS);

        if(jNativesCls == 0) {
                jni_printf("Unable to find class: %s", CB_CLASS);
                return -1;
        }

        //라이브러리 주 루틴을 호출한다. 이에 의해 무한 루프에 진입한다
        // 호출 인수들(args)은 Java에서 온 것이다
        lib_main(clen, args);
        return 0;
}

/*
 * 문자열을 Java에 보낸다
 */
jmethodID mSendStr;
static void jni_send_str(const char * text, int level) {
        JNIEnv * env;
        if(jNativesCls == 0) {
                printf("Unable to find class: %s", CB_CLASS);
                return;
        }

        //jni.Natives.OnMessage(String, int)를 호출한다
        if(!mSendStr) {
                //정적 메소드 jni.Natives.OnMessage에 대한 참조를 얻는다
                mSendStr = (*env)->GetStaticMethodID(env,
                                                     jNativesCls,
                                                     CB_CLASS_MSG_CB,
                                                     CB_CLASS_MSG_SIG);
        }

        if(mSendStr) {
                //메소드를 호출한다
                (*env)->CallStaticVoidMethod(env,
                                             jNativesCls,
                                             mSendStr,
                                             (*env)->NewStringUTF(env, text),
                                             (jint) level);
        } else {
                printf("Unable to find method: %s, signature: %s\n",
                       CB_CLASS_MSG_CB,
                       CB_CLASS_MSG_SIG);
        }
}

/*
 * 문자열을 서식화해서 Java에 보낸다.
 * 서식문자열을 가변 인수들로 서식화해서 임시 버퍼에 넣고
 * jni_sebd_str를 호출한다
 */
void jni_printf(char * format, ...) {
        va_list argptr;
        static char string[1024];

        va_start(argptr, format);
        vsprintf(string, format, argptr);
        va_end(argptr);

        jni_send_str(string, 0);
}

/*
 * Java 배열의 길이를 돌려준다
 */
const int getArrayLen(JNIEnv * env, jobjectArray jarray) {
        return (*env)->GetArrayLength(env, jarray);
}

/*
 * 라이브러리 주 루틴
 */
int lib_main(int argc, char **argv) {
        int i;
        jni_printf("Entering LIB MAIN");

        for(i = 0; i < argc; ++i) {
                jni_printf("Lib Main argv[%d]=%s", i, argv[i]);
        }
        return 0;
}
