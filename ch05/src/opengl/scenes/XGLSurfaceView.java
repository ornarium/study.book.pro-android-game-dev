package opengl.scenes;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

/*
 * SurfaceView의 한 구현으로, OpenGL 애니메이션의 표시를 위한 개별적인
 *  표면을 사용. 이 덕분에 애니메이션을 뷰 계통구조의 갱신과 무관하게 개별 스레드
 *  에서 진행 가능.
 * 
 * 
 *  렌더링 작업은 GLView.Renderer의 한 인스턴스에서 처리
 */
public class XGLSurfaceView extends GLSurfaceView implements Callback {
	private SurfaceHolder mHolder;
	private GLThread mGLThread;

	public XGLSurfaceView(Context context) {
		super(context);
		init();
	}
	
	public XGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 내부 표면이 생성, 변경, 파괴될때 콜백을 받을 수 있도록
		//SurfaceHolder.Callback 설정
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		
//		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//        mHolder.setFormat(PixelFormat.RGBA_8888);
//        setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
       
		mGLThread = new GLThread(this);
		mGLThread.start();		
	}
	
	public SurfaceHolder getSurfaceHolder() {
		return mHolder;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		//이 호출이 반환되면 표면 파괴
		mGLThread.finish();
	}

	/**
	 * 활동이 정지되었음을 뷰에게 알려줌
	 */
	public void onPause() {
		super.onPause();
		mGLThread.onPause();
	}
	
	/**
	 * 활동이 재개되었음을 뷰에게 알려줌
	 */
	public void onResume() {
		super.onResume();
		mGLThread.onResume();
	}
	
	/**
	 * 창의 초점이 변했음을 뷰에게 알려줌
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mGLThread.onWindowFocusChanged(hasFocus);
	}
	
	/**
	 * GL 렌더링 스레드에서 실행될 "사건" 하나를 추가.
	 * 
	 * @param r
	 * GL렌더링 스레드에서 실행될 코드를 담은 Runnable객체
	 */
	public void quueueEvent(Runnable r) {
		mGLThread.queueEvent(r);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mGLThread.requestExitAndWait();
	}
}
