package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

// This class generates the camera, where you can show up to certain area of the worldspace

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position; // This is the camera's position
    private float projectionWidth = 6;    // visible widht of the camera
    private float projectionHeight = 3;    // visible height of the camera
    public Vector4f clearColor = new Vector4f(1, 1,1 ,1);

    private Vector2f projectionSize =new Vector2f(projectionWidth, projectionHeight);
    private float zoom =1.0f; // default zoom state

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    // There are two types of projection orthographic and perspective
    // Configures the projection matrix to use an orthographic projection
    public void adjustProjection() {
        projectionMatrix.identity();
        // first three parameters sets the horizontal, next three vertical and last two sets
        // far and near clipping space (i.e. determines how deep the camera can see).
        projectionMatrix.ortho(0.0f, projectionSize.x * this.zoom,
                0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection); // inverts and stores it on inverseProjection
    }


    public Matrix4f getInverseProjection() {
        return this.inverseProjection;
    }

    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    // this is used to set up the camera. Where the camera is located at, what direction is the camera looking at
    // and which way is the up for the camera(to avoid the view being upside down)
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);
        inverseView = new Matrix4f(this.viewMatrix).invert();

        return this.viewMatrix;
    }
    // this will get the projectionMatrix
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Vector2f getProjectionSize(){
        return this.projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom ){
        this.zoom = zoom;
    }

    public void addZoom(float value){
        this.zoom += value;
    }
}