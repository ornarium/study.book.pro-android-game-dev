package opengl.scenes;

import java.util.concurrent.Semaphore;

import opengl.jni.Natives.EventListener;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class GLThread extends Thread implements EventListener {
	private boolean mDone;

	private GLSurfaceView mGLSurfaceView;
	private boolean mIsStop;
	private static final Semaphore sEglSemaphore = new Semaphore(1);
	
	public GLThread(GLSurfaceView glSurfaceView) {
		super();
		mDone = false;
		mGLSurfaceView = glSurfaceView;
		setName("GLThread");
	}

	@Override
	public void run() {
		try {
			try {
				sEglSemaphore.acquire();
			} catch (InterruptedException e) {
				return;
			}
			guardedRun();
		} catch (Exception ex) {
			Log.e("ch05", "error in thread : " + ex);
		} finally {
			sEglSemaphore.release();
		}
	}

	private void guardedRun() throws InterruptedException {
		while (!mDone) {
			synchronized (this) {
				// Runnable r;
				// while((r = getEvent()) != null)
				// r.run();
				try {
					Thread.sleep(50);
				} catch (Exception e){
				}
				if (mDone)
					break;
				
				if (!mIsStop) {
					mGLSurfaceView.requestRender();
				}
			}
		}

	}

	public void onPause() {
		mIsStop = true;
	}
	
	public void onResume() {
		mIsStop = false;
	}

	public void finish() {
		mIsStop = true;
		mDone = true;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		
	}

	public void queueEvent(Runnable r) {
		// TODO Auto-generated method stub
		
	}

	public void requestExitAndWait() {
		// TODO Auto-generated method stub
		
	}

	public void OnMessage(String text) {
		Log.d("ch05", "GLThread::OnMessage" + text);
	}

	public void GLSwapBuffers() {
		if(mGLSurfaceView != null)
			mGLSurfaceView.requestRender();
	}
}
