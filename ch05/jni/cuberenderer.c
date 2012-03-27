#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <math.h>

//#include <EGL/egl.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

#include <jni.h>

#include "include/opengl_jni_Natives.h"

#define ONE 1.0f
#define FIXED_ONE 0x1000

//함수 원형들
void jni_printf(char * format, ...);
void jni_gl_swap_buffers();

//회전각
static float mAngle = 0.0;

extern void cube_draw();

static void init_scene(void) {
    glDisable(GL_DITHER);

    /*
     * 한 번만 해주면 되는 OpenGL 초기화들
     * 다른 응용 프로그램이라면 다른 값들로 초기화해야 할 것이다.
     */
    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

    glClearColor(0.5f, 0.5f, 0.5f, 1);

    glEnable(GL_CULL_FACE);
    glShadeModel(GL_SMOOTH);
    glEnable(GL_DEPTH_TEST);
}


static void drawFrame() {
    /*
     * 기본적으로 OpenGL은 화질을 향상하나 성능을 떨어뜨리는 기능들을
     * 활성화한다. 그런 부분들을 기기 성능에 따라 적절히 조정할 필요가
     * 있다(특히 소프트웨어 렌더러가 쓰이는 경우).
     */
    glDisable(GL_DITHER);
    glTexEnvx(GL_TEXTURE_ENV,
            GL_TEXTURE_ENV_MODE, GL_MODULATE);

    /*
     * 보통의 경우 가장 먼저 할 일은 화면을 깨끗이 비우는 것이다.
     * 화면을 비우는 가장 효율적인 방법은 glClear()이다.
     */
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    /*
     * 카메라를 설정하고 3D물체들을 그린다.
     */
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glTranslatef(0, 0, -3.0f);
    glRotatef(mAngle, 0, 0, 1.0f);
    glRotatef(mAngle * 0.25f, 1, 0, 0);

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);

    cube_draw();
    
    glRotatef(mAngle * 2.0f, 0, 1, 1);
    glTranslatef(0.5f, 0.5f, 0.5f);

    cube_draw();
    
    mAngle += 1.2f;
}


/**
 * java에게 문자열 메시지를 보낸다.
 */
static jmethodID mSendStr;
static jclass jNativesCls;
static JavaVM * g_VM;

static void jni_send_str(const char * text) {
    JNIEnv * env;
    if(!g_VM)
        return;

    //java_opengl_jni_Natives_NativeRender()가 시작된 스레드가 아닌 곳에서
    //JAVA에 접근하려면 현재 스레드를 JAVA VM에 붙여야 한다.
    (*g_VM)->AttachCurrentThread(g_VM, (void **) &env, NULL);

    //Java Class 적재
    if(!jNativesCls)
        jNativesCls = (*env)->FindClass(env, "opengl/jni/Natives");

    if(jNativesCls == 0)
        return;

    //Java 메서드 opengl.jni.Nataives.OnMessage(String)을 호출한다.
    //메소드 이름과 서명을 이용하여메소드 참조를 얻는다
    if(!mSendStr)
        mSendStr = (*env)->GetStaticMethodID(env, jNativesCls, "OnMessage", "(Ljava/lang/String;)V");

    //메소드 참조로 메소드를 호출한다
    //c문자열을 그대로 사용할 수 없으므로, Java문자열로 변환해서 넘긴다
    if(mSendStr)
        (*env)->CallStaticVoidMethod(env, jNativesCls, mSendStr,
                (*env)->NewStringUTF(env, text));
}

void jni_gl_swap_buffers() {
    JNIEnv * env;

    if(!g_VM)
        return;

    (*g_VM)->AttachCurrentThread(g_VM, (void **) &env, NULL);

    if(!jNativesCls)
        jNativesCls = (*env)->FindClass(env, "opengl/jni/Natives");

    if(jNativesCls == 0)
        return;

    //Java 메서드 opengl.jni.Natives.GLSwapBuffers()를 호출한다
    jmethodID mid = (*env)->GetStaticMethodID(env, jNativesCls, "GLSwapBuffers", "()V");

    if(mid) 
        (*env)->CallStaticVoidMethod(env, jNativesCls, mid);
}

/**
 * printf의 Java 콜백 버전
 * 가변 인수 sprintf를 이용해서  서식화한
 * 문자열로 jni_send_str를 호출한다.
 */
void jni_printf(char * format, ...) {
    va_list argptr;
    static char string[1024];

    va_start(argptr, format);
    vsprintf(string, format, argptr);
    va_end(argptr);

    jni_send_str(string);
}


/*
 * 클래스 : opengl_jni_Natives
 * 메서드 : RenderTest
 * 서명 : ()V
 */
JNIEXPORT jint JNICALL Java_opengl_jni_Natives_NativeRender(JNIEnv * env, jclass cls) {
    (*env)->GetJavaVM(env, &g_VM);
    static int initialized = 0;

    if(!initialized) {
        jni_printf("Native:RenderTest initscene");
        init_scene();
        initialized = 1;
    }

    drawFrame();
    return 1;
}
