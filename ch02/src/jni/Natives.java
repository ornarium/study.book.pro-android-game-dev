package jni;

import android.util.Log;

public class Natives {
	/**
	 * 네이티브 주 루틴
	 */
	public static native int LibMain(String[] argv);
	
	/**
	 * C쪽에서 넘겨준 메시지를 출력
	 */
	private static void OnMessage(String text, int level) {
		Log.d("ch02", "OnMessage text: " + text + " level = " + level);
	}
}
