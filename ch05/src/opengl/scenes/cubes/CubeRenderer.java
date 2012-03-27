package opengl.scenes.cubes;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.jni.Natives;

import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

/**
 * 회전하는 두 입방체를 위한 렌더러
 *
 */
public class CubeRenderer implements Renderer {

	private boolean mTranslucentBackground;
	private float mAngle;
	private Cube mCube;
	private boolean mIsNativeDraw;

	public CubeRenderer(boolean useTranslucentBackground, boolean isNativeDraw) {
		mTranslucentBackground = useTranslucentBackground;
		mIsNativeDraw = isNativeDraw;
		mCube = new Cube();
	}

	public void onDrawFrame(GL10 gl) {
		if(mIsNativeDraw)
			doNativeDraw();
		else
			doJavaDraw(gl);
	}
	
	
	private void doNativeDraw() {
		Natives.NativeRender();
	}
	
	private void doJavaDraw(GL10 gl) {
		/*
		 * 보통의 경우 가장 먼저 할일은 화면을 깨끗이 비우는 것이다.
		 * 화면을 비우는 가장 효율적인 방법은 glClear()이다
		 */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		/*
		 * 카메라를 설정하고 3D물체를 그린다.
		 */
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -3.0f);
		gl.glRotatef(mAngle, 0, 1, 0);
		gl.glRotatef(mAngle * 0.25f, 1, 0, 0);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		mCube.draw(gl);
		gl.glRotatef(mAngle * 2.0f, 0, 1, 1);
		gl.glTranslatef(0.5f, 0.5f, 0.5f);
		
		mCube.draw(gl);
		mAngle += 1.2f;		
	}
	
	public int[] getConfigSpec() {
		if(mTranslucentBackground) {
			//반투명 배경의 경우 깊이 버퍼와 알파 버퍼가 필수
			int[] configSpec = {
					EGL10.EGL_RED_SIZE, 8,
					EGL10.EGL_GREEN_SIZE, 8,
					EGL10.EGL_BLUE_SIZE, 8,
					EGL10.EGL_ALPHA_SIZE, 8,
					
					EGL10.EGL_DEPTH_SIZE, 16,
					EGL10.EGL_NONE };
			return configSpec;
		} else {
			//깊이 버퍼는 필요하지만 색상 버퍼의 세부 사항은 중요하지 않다
			int[] configSpec = {
					EGL10.EGL_DEPTH_SIZE, 16,
					EGL10.EGL_NONE };
			return configSpec;
		}
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		/*
		 * 투영 행렬을 설정한다. 투영 행렬은 매 프레임마다 설정할 필요가 없다.
		 * 그러나 일반적으로 뷰포트가 변했다면  투영행렬을 새로이 설정할 필요
		 * 가 있다
		 */
		float ratio = (float) w / h;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig eglCfg) {
		/*
		 * 기본적으로 OpenGL은 화질을 향상하나 성능을 떨어뜨리는 기능들을 
		 * 활성화한다. 그런 부분들을 기기 성능에 따라 적절히 조정할 필요가 있다
		 * 특히 소프트웨어 렌더러가 쓰이는 경우. Logcat에 libhgl.so 관련 
		 * 오류 메시지가 나온다면 소프트웨어 렌더링으로 동작하게 된다(에뮬레이터는
		 * 소프트웨어 렌더링으로 동작)
		 */
		gl.glDisable(GL10.GL_DITHER);
		
		/*
		 * 한 번만 해주면 되는 OpenGL 초기화들
		 * 다른 응용 프로그램이라면 다른 값들로 초기화해야 할 것이다
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		
		if(mTranslucentBackground) {
			gl.glClearColor(0, 0, 0, 0.5f); 
		} else {
			gl.glClearColor(1, 1, 1, 0.5f);
		}
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

}
