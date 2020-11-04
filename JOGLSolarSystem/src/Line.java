import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class Line {
    private float[] pos;
    float[] color;
    Matrix3D mMat;
    private int[] vbo = new int[1];
    

    public Line(Vector3D v1, Vector3D v2, GL4 gl, String axis){
        pos = new float[] {(float)v1.getX(), (float)v1.getY(), (float)v1.getZ(), (float)v2.getX(), (float)v2.getY(), (float)v2.getZ()};
        
        if(axis.equals("x")) {
        	color = new float[] {1.0f, 0.0f, 0.0f, 1.0f};
        }
        else if(axis.equals("y")) {
        	color = new float[] { 0.0f, 0.0f, 1.0f, 1.0f};
        }
        else if(axis.equals("z")) {
        	color = new float[] { 0.0f, 1.0f, 0.0f, 1.0f};
        }
        setup(gl);
        mMat = new Matrix3D();
    }

    public void setup(GL4 gl) {
        gl.glGenBuffers(vbo.length, vbo, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pos);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        int m_loc = gl.glGetUniformLocation(rendering_program, "m_matrix");
        int v_loc = gl.glGetUniformLocation(rendering_program, "v_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
        int color_loc = gl.glGetUniformLocation(rendering_program, "colorVec");

        gl.glUniformMatrix4fv(m_loc, 1, false, mMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(v_loc, 1, false, vMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glUniform4fv(color_loc, 1, Buffers.newDirectFloatBuffer(color));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glLineWidth(2.0f);

        gl.glDrawArrays(GL_LINES, 0, 2);
    }
}