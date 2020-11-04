import java.util.ArrayList;

import com.jogamp.opengl.GL4;

import graphicslib3D.Vector3D;

public class PentagonalPrism extends Shape {
	
	private float height;
	private ArrayList<Vector3D> vertices;
	int i = 0;
	int t = 0;
	//private float posX, posY, posZ;
	
	PentagonalPrism(String name, float height, GL4 gl, String texture){
		this.height = height;
		//posX = posY = posZ = 0.0f;
		vertices = new ArrayList<Vector3D>();
		pos = new float[144];
		tex = new float[96];
		getCoords();
		setup(gl, texture);
		
	}
	
	public float[] getPos() {
		return pos;
	}
	
	public float[] getTex() {
		return tex;
	}
	
	public void getCoords() {
		float inc = 360f/5f;
		float theta = 18f;
		
		for(int i = 0; i < 5; i++) {
			vertices.add(new Vector3D(Math.cos(Math.toRadians(theta))*height, 0.5f*height, -Math.sin(Math.toRadians(theta))*height));
			theta += inc;
		}
		
		theta = 18f;
		for(int i = 0; i < 5; i++) {
			vertices.add(new Vector3D(Math.cos(Math.toRadians(theta))*height, -0.5f*height, -Math.sin(Math.toRadians(theta))*height));
			theta += inc;
		}
		
		 calcFace(0);
	     calcSquare(0);
	     calcSquare(1);
	     calcSquare(2);
	     calcSquare(3);
	     calcSquare(4);
	     calcFace(5);
		
	}
	
	private void calcFace(int i) {
		addPoint(vertices.get(0 + i));
        addPoint(vertices.get(1 + i));
        addPoint(vertices.get(2 + i));
        addPoint(vertices.get(0 + i));
        addPoint(vertices.get(2 + i));
        addPoint(vertices.get(3 + i));
        addPoint(vertices.get(0 + i));
        addPoint(vertices.get(3 + i));
        addPoint(vertices.get(4 + i));

        double up = vertices.get(0 + i).getZ()/height/2.0f+.5;
        double down = vertices.get(4 + i).getZ()/height/2.0f+.5;
        double sideSmall = vertices.get(4 + i).getX()/height/2.0f+.5;
        double sideLarge = vertices.get(0 + i).getX()/height/2.0f+.5;

        addTexPoint(sideLarge,1-up);
        addTexPoint(0.5,1);
        addTexPoint(1 - sideLarge,1-up);
        addTexPoint(sideLarge,1-up);
        addTexPoint(1 - sideLarge,1-up);
        addTexPoint(1 - sideSmall, 1-down);
        addTexPoint(sideLarge,1-up);
        addTexPoint(1 - sideSmall, 1-down);
        addTexPoint(sideSmall, 1-down);
	}
	
	private void calcSquare(int i) {
        int v1, v2, v3, v4;
        if(i < 4) {
            v1 = i + 5;
            v2 = i + 6;
            v3 = i + 1;
            v4 = i;
        } else {
            v1 = i + 5;
            v2 = 5;
            v3 = 0;
            v4 = i;
        }

        addPoint(vertices.get(v1));
        addPoint(vertices.get(v2));
        addPoint(vertices.get(v3));
        addPoint(vertices.get(v1));
        addPoint(vertices.get(v3));
        addPoint(vertices.get(v4));

        addTexPoint(0,0);
        addTexPoint(1,0);
        addTexPoint(1,1);
        addTexPoint(0,0);
        addTexPoint(1,1);
        addTexPoint(0,1);
    }
	
	 private void addPoint(Vector3D point) {
	        pos[i] = (float)point.getX();
	        pos[i+1] = (float)point.getY();
	        pos[i+2] = (float)point.getZ();

	        i += 3;
	    }
	 
	 private void addTexPoint(double v1, double v2) {
	        tex[t] = (float)v1;
	        tex[t+1] = (float)v2;
	        
	        t += 2;
	    }
}
