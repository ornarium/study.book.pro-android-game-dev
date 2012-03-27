package opengl.test;

import opengl.scenes.XGLSurfaceView;
import opengl.scenes.cubes.CubeRenderer;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class NativeGLActivity extends Activity {
    private GLSurfaceView mGLSurfaceView;
    {
//    	final String LIB_PATH = "/data/libgltest_jni.so";
//    	System.out.println("Loading JNI libi using abs path: " + LIB_PATH);
//    	System.load(LIB_PATH);
    	
    	final String LIB_NAME = "gltest-jni";
    	System.loadLibrary(LIB_NAME);
    		
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        mGLSurfaceView = new XGLSurfaceView(this);
        try {
        	mGLSurfaceView.setRenderer(new CubeRenderer(true, true));
        	setContentView(mGLSurfaceView);
        } catch (Exception e) {
        	Log.e("ch05", "error : " + e);
        }        
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	//이상적으로 활동이 없거나 다시 재개될때 적절한 대응을 위함
    	mGLSurfaceView.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mGLSurfaceView.onResume();
    }
}