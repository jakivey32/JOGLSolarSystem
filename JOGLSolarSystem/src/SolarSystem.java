import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.*;
import java.util.ArrayList;
import java.util.Random;
//import java.util.Scanner;

import javax.swing.*;
//import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.common.nio.Buffers;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
//import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
//import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
//import static com.jogamp.opengl.GL.GL_FLOAT;
//import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
//import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

import graphicslib3D.*;


public class SolarSystem extends JFrame implements GLEventListener, KeyListener {
	private GLCanvas myCanvas;
	private int rendering_program;
	private int axes_program;
	private int vao[] = new int[1];
	private int vbo[] = new int[1];
	private float cameraX, cameraY, cameraZ;
	//private float pLocX, pLocY, pLocZ;
	//private float sLocX, sLocY, sLocZ;
	//private float axisX, axisY, axisZ;
	private MatrixStack mvStack = new MatrixStack(10);
	private GLSLUtils util = new GLSLUtils();
	private boolean axes = true;
	//private FloatBuffer axesColor = FloatBuffer.allocate(4);
	private Camera camera;
	public ArrayList<Key> keyList = new ArrayList<Key>();
	public enum Key { w, a, s, d, q, e, down, left, up, right, shift, space };
	private ArrayList<Shape> planets = new ArrayList<Shape>();
	private Random rand = new Random();
	private Line x;
	private Line y;
	private Line z;
	
 	public SolarSystem() {
		setTitle("Solar System with JOGL");
		GLProfile profile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(profile);
		myCanvas = new GLCanvas(capabilities);
		myCanvas.addGLEventListener(this);
		myCanvas.addKeyListener(this);
		getContentPane().add(myCanvas);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				getContentPane().remove(myCanvas);
				dispose();
				System.exit(1);
			}
		});
		
		setSize(750, 750);
		setLocation(200,200);
		setVisible(true);
		FPSAnimator animator = new FPSAnimator(myCanvas, 30);
		animator.start();
	}

	public static void main(String[] args) {
		new SolarSystem();
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		Matrix3D pMat = perspective(50.0f, aspect, 0.1f, 1000.0f);
		
		double time = (double)(System.currentTimeMillis())/2000.0;
		moveCamera(time);
		//Matrix3D pMat = camera.setView();
		
		Matrix3D vMat = new Matrix3D();
	    vMat.concatenate(camera.getRotMatrix());
	    vMat.translate(-camera.getX(), -camera.getY(), -camera.getZ());
		
	    gl.glUseProgram(axes_program);
	    
	    if(axes) {
	    	x.display(gl, axes_program, pMat, vMat);
	    	y.display(gl, axes_program, pMat, vMat);
	    	z.display(gl, axes_program, pMat, vMat);
	    }
	    
	    gl.glUseProgram(rendering_program);
	    
	    planets.get(0).pushToOrbit(mvStack, time);
	    planets.get(0).display(gl, rendering_program, pMat, vMat);
	    mvStack.popMatrix();
	    
	    planets.get(1).pushToOrbit(mvStack, time);
	    planets.get(1).display(gl, rendering_program, pMat, vMat);
	    
	    planets.get(2).pushToOrbit(mvStack, time);
	    planets.get(2).display(gl, rendering_program, pMat, vMat);
	    mvStack.popMatrix();
	    mvStack.popMatrix();
	    
	    planets.get(3).pushToOrbit(mvStack, time);
	    planets.get(3).display(gl, rendering_program, pMat, vMat);
	    
	    planets.get(4).pushToOrbit(mvStack, time);
	    planets.get(4).display(gl, rendering_program, pMat, vMat);
	    mvStack.popMatrix();
	    mvStack.popMatrix();
	    
	    planets.get(5).pushToOrbit(mvStack, time);
	    planets.get(5).display(gl, rendering_program, pMat, vMat);
	    //mvStack.popMatrix();
	    
	    planets.get(6).pushToOrbit(mvStack, time);
	    planets.get(6).display(gl, rendering_program, pMat, vMat);
	    mvStack.popMatrix();
	    mvStack.popMatrix();
	}



	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		axes_program = createAxesShaderProgram();
		//setupVertices();
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 12.0f;
		camera = new Camera(cameraX, cameraY, cameraZ);
		planets.add(new Sphere("sun", 2.0f, gl, "8k_sun.jpg"));
		planets.add(new Sphere("earth", 0.7f, gl, "earthmap1k.jpg"));
		planets.add(new Sphere("moon", 0.2f, gl, "moon.jpg"));
		planets.add(new Sphere("ethan", 1.5f, gl, "2k_saturn.jpg"));
		planets.add(new Sphere("butt", 0.5f, gl, "2k_mercury.jpg"));
		planets.add(new Sphere("cryo", 1.3f, gl, "2k_neptune.jpg"));
		planets.add(new PentagonalPrism("jake", 20, gl, "jake.jpg"));
		
		
		
		for(Shape planet : planets) {
			int random = rand.nextInt(20);
			if(random > 14)
				planet.setRotY(rand.nextFloat());
			else if(random > 7)
				planet.setRotX(rand.nextFloat());
			else
				planet.setRotZ(rand.nextFloat());
		}
		
		planets.get(1).setOrbit(10, 0.2f, "y");
		planets.get(2).setOrbit(2, 0.9f, "z");
		planets.get(3).setOrbit(32, 1.4f, "y");
		planets.get(4).setOrbit(3, 1.0f, "z");
		planets.get(5).setOrbit(51, 0.8f, "y");
		planets.get(6).setOrbit(3, 0.3f, "z");
		
		x = new Line(new Vector3D(0,0,0), new Vector3D(25,0,0), gl, "x");
		y = new Line(new Vector3D(0,0,0), new Vector3D(0,25,0), gl, "y");
		z = new Line(new Vector3D(0,0,0), new Vector3D(0,0,25), gl, "z");
	}
	
	public void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		float[] lineCoords = {
				0.0f, 0.0f, 0.0f, 10.0f, 0.0f, 0.0f
		};
	
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		
		gl.glGenBuffers(vbo.length, vbo, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer line1Buf = Buffers.newDirectFloatBuffer(lineCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, line1Buf.limit()*4, line1Buf, GL_STATIC_DRAW);
	
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {}
	@Override
	public void dispose(GLAutoDrawable arg0) {}
	
	@Override
	public void keyPressed(KeyEvent k) {
		int key = k.getKeyCode();
		
		if(key == KeyEvent.VK_W) {
			checkThenAdd(Key.w);
		}
		else if(key == KeyEvent.VK_S) {
			checkThenAdd(Key.s);
		}
		else if(key == KeyEvent.VK_A) {
			checkThenAdd(Key.a);
		}
		else if(key == KeyEvent.VK_D) {
			checkThenAdd(Key.d);
		}
		else if(key == KeyEvent.VK_Q) {
			checkThenAdd(Key.q);
		}
		else if(key == KeyEvent.VK_E) {
			checkThenAdd(Key.e);
		}
		else if(key == 39) {
			checkThenAdd(Key.right);
		}
		else if(key == 37) {
			checkThenAdd(Key.left);
		}
		else if(key == 38) {
			checkThenAdd(Key.up);
		}
		else if(key == 40) {
			checkThenAdd(Key.down);
		}
		else if(key == KeyEvent.VK_SPACE) {
			axes = !axes;
			System.out.println(axes);
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {
		int key = k.getKeyCode();
		
		if(key == KeyEvent.VK_W) {
			keyList.remove(Key.w);
		}
		else if(key == KeyEvent.VK_S) {
			keyList.remove(Key.s);
		}
		else if(key == KeyEvent.VK_A) {
			keyList.remove(Key.a);
		}
		else if(key == KeyEvent.VK_D) {
			keyList.remove(Key.d);
		}
		else if(key == KeyEvent.VK_Q) {
			keyList.remove(Key.q);
		}
		else if(key == KeyEvent.VK_E) {
			keyList.remove(Key.e);
		}
		else if(key == KeyEvent.VK_RIGHT) {
			keyList.remove(Key.right);
		}
		else if(key == KeyEvent.VK_LEFT) {
			keyList.remove(Key.left);
		}
		else if(key == KeyEvent.VK_UP) {
			keyList.remove(Key.up);
		}
		else if(key == KeyEvent.VK_DOWN) {
			keyList.remove(Key.down);
		}
		else if(key == KeyEvent.VK_SPACE) {
			keyList.remove(Key.space);
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		return r;
	}
	
	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("resources/vert.shader");
		String fshaderSource[] = util.readShaderSource("resources/frag.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}
	
	private int createAxesShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("resources/axis_vert.shader");
		String fshaderSource[] = util.readShaderSource("resources/axis_frag.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}

	public Texture loadTexture(String textureFileName)
	{	Texture tex = null;
		try { tex = TextureIO.newTexture(new File(textureFileName), false); }
		catch (Exception e) { e.printStackTrace(); }
		return tex;
	}
	
	private void checkThenAdd(Key key) {
        if (!keyList.contains(key)) {
            keyList.add(key);
        }
    }
	
	public void moveCamera(double time) {
        for(Key key : keyList) {
           // System.out.println(key.name());
            switch(key) {
                case w:
                    camera.moveForward(time);
                    break;
                case a:
                    camera.strafeLeft(time);
                    break;
                case s:
                    camera.moveBackward(time);
                    break;
                case d:
                	camera.strafeRight(time);
                    break;
                case q:
                	camera.strafeUp(time);
                    break;
                case e:
                	camera.strafeDown(time);
                    break;
                case left:
                	camera.pitchLeft(time);
                    break;
                case up:
                	camera.yawUp(time);
                    break;
                case right:
                	camera.pitchRight(time);
                    break;
                case down:
                	camera.yawDown(time);
            }
        }
    }
}
