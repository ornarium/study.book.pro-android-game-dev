package opengl.scenes.cubes;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {
	private IntBuffer mVertexBuffer;
	private IntBuffer mColorBuffer;
	private ByteBuffer mIndexBuffer;

	public Cube() {
		int one = 0x10000;
		//정점 8개의 x,y,z 좌표들
		int vertices[] = {
				-one, -one, -one,
				one, -one, -one,
				one, one, -one,
				-one, one, -one,
				-one, -one, one,
				one, -one, one,
				one, one, one,
				-one, one, one };
		
		//RGBA 색상 8개
		int colors[] = {
				0, 0, 0, one,
				one, 0, 0, one,
				one, one, 0, one,
				0, one, 0, one,
				0, 0, one, one,
				one, 0, one, one,
				one, one, one, one,
				0, one, one, one };
		
		//삼각형 12개의  x, y, z 좌표 색인들
		byte indices[] = {
				0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7,
				2, 7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1,
				3, 1, 2 };
		
		/*
		 * native heap에 저장된 자료는 GC가 건드리지 못하므로 ByteBuffer로 만들고,
		 * gl*Pointer함수에서 쓰려면 반드시 ByteBuffer로 만들어야 한다
		 */
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asIntBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asIntBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
		
		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}
	
	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
	}

}
