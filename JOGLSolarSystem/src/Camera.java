import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Camera {
	
	private Point3D cameraPos;
	private Vector3D cameraTarget = new Vector3D(0,0,-1); //n vector
	float pitch = 90, yaw = 0;
	float moveSens = 0.0000000003f;
	float rotateSens = 0.000000003f;
	boolean calc = false;
	
	
	public Camera(float x, float y, float z) {
		cameraPos = new Point3D(x,y,z);
	}
	
	public float getX() {
		return (float) cameraPos.getX();
	}
	
	public void setX(float newX) {
		cameraPos.setX(newX);
	}
	
	public float getY() {
		return (float) cameraPos.getY();
	}
	public void setY(float newY) {
		cameraPos.setY(newY);
	}
	
	public float getZ() {
		return (float) cameraPos.getZ();
	}
	
	public void setZ(float newZ) {
		cameraPos.setZ(newZ);
	}
	
	public Vector3D getTarget() {
		return cameraTarget;
	}
	
	public void move(Vector3D vector) {
        cameraPos.setX(cameraPos.getX() + vector.getX());
        cameraPos.setY(cameraPos.getY() + vector.getY());
        cameraPos.setZ(cameraPos.getZ() + vector.getZ());
    }
	
	public void updateTarget() {
		if(calc) {
			cameraTarget.setX(getRotX());
			cameraTarget.setY(getRotY());
			cameraTarget.setZ(getRotZ());
			cameraTarget.normalize();
		}
	}

	private double getRotZ() {
		return Math.cos(Math.toRadians(yaw)) * -Math.sin(Math.toRadians(pitch));
	}

	private double getRotY() {
		return Math.sin(Math.toRadians(yaw));
	}

	private double getRotX() {
		return Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
	}
	
	public Matrix3D getRotMatrix() {
        updateTarget();
        Vector3D left = cameraTarget.cross(new Vector3D(0,1,0)).normalize();
        Vector3D up = left.cross(cameraTarget).normalize();
        Matrix3D rotMatrix = new Matrix3D(new double[] {left.getX(), up.getX(), -cameraTarget.getX(), 0.0f,
                                        left.getY(), up.getY(), -cameraTarget.getY(), 0.0f,
                                        left.getZ(), up.getZ(), -cameraTarget.getZ(), 0.0f,
                                        -left.dot(new Vector3D(cameraPos)), -up.dot(new Vector3D(cameraPos)), -cameraTarget.mult(-1).dot(new Vector3D(cameraPos)), 1.0f});
        return rotMatrix;
    }
	
	public void moveForward(double time) {
        updateTarget();
        Vector3D move;
        move = cameraTarget.mult(moveSens * time);
        move.setY(0);
        move(move);
    }

    public void moveBackward(double time) {
    	updateTarget();
        Vector3D move;
        move = cameraTarget.mult(-moveSens * time);
        move.setY(0);
        move(move);
    }

    public void strafeLeft(double time) {
    	updateTarget();
        Vector3D left = cameraTarget.cross(new Vector3D(0,-1,0)).mult(moveSens * time);
        move(left);
    }

    public void strafeRight(double time) {
    	updateTarget();
        Vector3D right = cameraTarget.cross(new Vector3D(0,1,0)).mult(moveSens * time);
        move(right);
    }

    public void strafeUp(double time) {
    	updateTarget();
        Vector3D up = new Vector3D(0,1,0).mult(moveSens * time);
        move(up);
    }

    public void strafeDown(double time) {
    	updateTarget();
        Vector3D up = new Vector3D(0,-1,0).mult(moveSens * time);
        move(up);
    }

    public void pitchRight(double time) {
        pitch -= rotateSens * time;
        calc = true;
    }

    public void pitchLeft(double time) {
        pitch += rotateSens * time;
        calc = true;
    }

    public void yawDown(double time) {
        yaw -= rotateSens * time;
        calc = true;
    }

    public void yawUp(double time) {
        yaw += rotateSens * time;
        calc = true;
    }
	
	
}
