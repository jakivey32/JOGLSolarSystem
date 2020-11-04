import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

import java.io.File;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Shape {
	 private Point3D sPos = new Point3D(0,0,0);
	 private int[] vbo = new int[2];
	 private float rotX = 0, rotY = 0, rotZ = 0;
	 protected float[] pos, tex;
	 private int texture;
	 Matrix3D mMat = new Matrix3D();
	 public float distance = 1;
	 private float orbitSpeedX = 0;
	 private float orbitSpeedY = 0;
	 private float orbitSpeedZ = 0;
	 
	 public Shape() {
		 pos = new float[0];
		 tex = new float[0];
	 }
	 
	 public float getX() { 
		 return (float)sPos.getX(); 
		 }

	 public float getY() { 
		 return (float)sPos.getY(); 
		 }

	 public float getZ() { 
		 return (float)sPos.getZ(); 
		 }

	 public void setX(float x) { 
		 sPos.setX(x); 
		 }

	 public void setY(float y) { 
		 sPos.setY(y); 
		 }

	 public void setZ(float z) {
		 sPos.setZ(z); 
		 }

	 public void move(Point3D point) { 
		 sPos = sPos.add(point); 
		 }

	 public void move(Vector3D vector) {
	        sPos.setX(sPos.getX() + vector.getX());
	        sPos.setY(sPos.getY() + vector.getY());
	        sPos.setZ(sPos.getZ() + vector.getZ());
	    }

	 public float getDegrees() {
	        return Math.max(Math.abs(rotX), Math.max(Math.abs(rotY), Math.abs(rotZ)));
	    }

	    public float getRotCompX() {
	        float num = rotX / getDegrees();
	        if (Float.isNaN(num)) {
	            return 0;
	        }
	        else {
	            return num;
	        }
	    }

	    public float getRotCompY() {
	        float num = rotY / getDegrees();
	        if (Float.isNaN(num)) {
	            return 0;
	        }
	        else {
	            return num;
	        }
	    }

	    public float getRotCompZ() {
	        float num = rotZ / getDegrees();
	        if (Float.isNaN(num)) {
	            return 0;
	        }
	        else {
	            return num;
	        }
	    }

	    public void setOrbit(float distance, float speed, String axis) {
	        this.distance = distance;
	        if (axis == "x") {
	            orbitSpeedX = speed;
	            orbitSpeedY = 0;
	            orbitSpeedZ = 0;
	        }
	        else if (axis == "y") {
	            orbitSpeedY = speed;
	            orbitSpeedX = 0;
	            orbitSpeedZ = 0;
	        }
	        else if (axis == "z") {
	            orbitSpeedZ = speed;
	            orbitSpeedX = 0;
	            orbitSpeedY = 0;
	        }
	    }

	    public float getOrbitXComp(double amt) {
	     
	        float rtn = 0;
	        if(orbitSpeedY != 0) {
	            rtn += Math.cos(amt*orbitSpeedY);
	        }
	        if(orbitSpeedZ != 0) {
	            rtn -= Math.sin(amt*orbitSpeedZ);
	        }
	        return rtn;
	    }

	    public float getOrbitYComp(double amt) {
	    
	        float rtn = 0;
	        if(orbitSpeedX != 0) {
	            rtn -= Math.sin(amt*orbitSpeedX);
	        }
	        if(orbitSpeedZ != 0) {
	            rtn += Math.cos(amt*orbitSpeedZ);
	        }
	        return rtn;
	    }

	    public float getOrbitZComp(double amt) {
	       
	        float rtn = 0;
	        if(orbitSpeedX != 0) {
	            rtn += Math.cos(amt*orbitSpeedX);
	        }
	        if(orbitSpeedY != 0) {
	            rtn -= Math.sin(amt*orbitSpeedY);
	        }
	        return rtn;
	    }

	    public void pushToOrbit(MatrixStack mvStack, double time) {
	        mvStack.pushMatrix();
	        mvStack.translate(getOrbitXComp(time) * distance, getOrbitYComp(time) * distance, getOrbitZComp(time) * distance);
	        mvStack.pushMatrix();
	        if (getDegrees() > 0) {
	            mvStack.rotate((System.currentTimeMillis() / 10.0) * getDegrees(), getRotCompX(), getRotCompY(), getRotCompZ());
	        }
	        mMat = mvStack.peek();
	        mvStack.popMatrix();
	    }

	    public void setRotX(float x) { rotX = x; }

	    public void setRotY(float y) { rotY = y; }

	    public void setRotZ(float z) { rotZ = z; }

	    public void setup(GL4 gl, String texture) {
	        this.texture = loadTexture(texture).getTextureObject();

	        gl.glGenBuffers(vbo.length, vbo, 0);

	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
	        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pos);
	        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);

	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
	        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tex);
	        gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);
	    }

	    public Texture loadTexture(String textureFileName)
	    {	Texture tex = null;
	        try { 
	        	tex = TextureIO.newTexture(new File(textureFileName), false); 
	        	}
	        catch (Exception e) { 
	        	e.printStackTrace(); 
	        	}
	        return tex;
	    }

	    private void update() {
	        mMat.translate(sPos.getX(), sPos.getY(), sPos.getZ());
	    }

	    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
	        update();

	        int m_loc = gl.glGetUniformLocation(rendering_program, "m_matrix");
	        int v_loc = gl.glGetUniformLocation(rendering_program, "v_matrix");
	        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

	        gl.glUniformMatrix4fv(m_loc, 1, false, mMat.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(v_loc, 1, false, vMat.getFloatValues(), 0);
	        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);

	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);

	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
	        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);

	        gl.glActiveTexture(GL_TEXTURE0);
	        gl.glBindTexture(GL_TEXTURE_2D, texture);

	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glDepthFunc(GL_LEQUAL);

	        gl.glDrawArrays(GL_TRIANGLES, 0, pos.length/3);
	        mMat.setToIdentity();
	    }
}
