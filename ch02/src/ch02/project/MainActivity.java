package ch02.project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jni.Natives;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	private static final String LIB = "libch02.so";
	private static final String LIB_NAME = "libch02.so";
	private static final String LIB_PATH = "/data/data/ch02.project/files/"
			+ LIB;
	
	//안드로이드 1.5이상부터는 프로젝트의 libs/armeabi 폴더에 저장하고 
	//System.loadLibrary로 적재할수 있다
	static {
		System.loadLibrary(LIB);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*android 1.5 이상부터는 다른 방식으로 구현 */
		/*
		try {
			// 라이브러리를 설치한다
			Log.d("ch02", "Installing LIB: " + LIB);

			// 프로젝트 assets 폴더의 lib파일을 응용 프로그램 파일 폴더,
			// 즉 /data/data패키지 이름/files 폴더에 복사한다
			writeToStream(getAssets().open(LIB), openFileOutput(LIB, 0));

			// 라이브러리를 적재한다.
			System.load(LIB_PATH);


		} catch (Exception e) {
			Log.e("ch02", e.toString());
		}
		*/
		
		// 라이브러리의 함수를 호출한다
		String[] argv = { "MyLib", "arg1", "arg2" };
		Natives.LibMain(argv);
	}

	/* 스트림에 기록.
	 * 안드로이드 1.5 이상부터는 다른 방식으로 구현되기 때문에 쓸일이 없음 */
	public static void writeToStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] bytes = new byte[2048];

		for (int c = in.read(bytes); c != -1; c = in.read(bytes))
			out.write(bytes, 0, c);
		in.close();
		out.close();
	}
}