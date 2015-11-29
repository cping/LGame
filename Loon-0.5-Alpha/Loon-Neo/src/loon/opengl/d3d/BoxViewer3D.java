package loon.opengl.d3d;

import loon.canvas.LColor;
import loon.geom.Vector3f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class BoxViewer3D {
	
	public class TextureVector {
		
	    float u, v, w;
	    
	    public TextureVector(){
	    	
	    }

	    public TextureVector(float u, float v, float w) {
	        this.u = 0.0f;
	        this.v = 0.0f;
	        this.w = 0.0f;
	    }
	}

    Vector3f[] vertex;
    TextureVector[] textureVertex;
    Vector3f[] vertexNormal;
    Face[] face;

    Vector3f[] rotatedVertex;
    Vector3f[] faceNormal;
    Vector3f[] rotatedFaceNormal;

    LColor color;
    Vector3f ambientLight;

    int vertices;
    int textureVertices;
    int vertexNormals;
    int faces;

    private int zValue[];

    private float[][] createRotationMatrix(float x, float y, float z, float ax, float ay, float az) {
        float[][] matrix = new float[4][4];

        matrix[0][0] =  (MathUtils.cos(ay) * MathUtils.cos(az));
        matrix[1][0] =  (MathUtils.cos(ay) * MathUtils.sin(az));
        matrix[2][0] =  (-MathUtils.sin(ay));
        matrix[3][0] = 0.0f;
        matrix[0][1] =  (MathUtils.sin(ax) * MathUtils.sin(ay) * MathUtils.cos(az) - MathUtils.cos(ax) * MathUtils.sin(az));
        matrix[1][1] =  (MathUtils.sin(ax) * MathUtils.sin(ay) * MathUtils.sin(az) + MathUtils.cos(ax) * MathUtils.cos(az));
        matrix[2][1] =  (MathUtils.sin(ax) * MathUtils.cos(ay));
        matrix[3][1] = 0.0f;
        matrix[0][2] =  (MathUtils.cos(ax) * MathUtils.sin(ay) * MathUtils.cos(az) + MathUtils.sin(ax) * MathUtils.sin(az));
        matrix[1][2] =  (MathUtils.cos(ax) * MathUtils.sin(ay) * MathUtils.sin(az) - MathUtils.sin(ax) * MathUtils.cos(az));
        matrix[2][2] =  (MathUtils.cos(ax) * MathUtils.cos(ay));
        matrix[3][2] = 0.0f;
        matrix[0][3] = x;
        matrix[1][3] = y;
        matrix[2][3] = z;
        matrix[3][3] = 1.0f;

        return matrix;
    }

    private boolean isPolygonVisible(Vector3f v1, Vector3f v2, Vector3f v3) {
        boolean returnValue = false;
        float dx1 = v3.x - v1.x;
        float dy1 = v3.y - v1.y;
        float dx2 = v3.x - v2.x;
        float dy2 = v3.y - v2.y;

        if ((dx1 * (dy2 - dy1) - (dx2 - dx1) * dy1) > 0) {
            returnValue = true;
        }

        return returnValue;
    }

    private void sort(int top, int bottom) {
        int i, j;
        int x, tmp;

        i = top;
        j = bottom;
        x = zValue[(top + bottom) / 2];
        do {
            while (zValue[i] < x) i++;

            while (x < zValue[j]) j--;

            if (i < j) {
                tmp = zValue[i];
                zValue[i] = zValue[j];
                zValue[j] = tmp;
            }

            if (i <= j) {
                i++;
                j--;
            }
        } while (i <= j);

        if (top < j) sort(top, j);
        if (i < bottom) sort(i, bottom);
    }

    private int limitColor(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > 255) {
            value = 255;
        }

        return value;
    }

    private void initLambertFlat(GLEx g, Vector3f n) {
        int delta = 0;
        int red = 0;
        int green = 0;
        int blue = 0;

        delta = (int) (ambientLight.x * n.x +
                ambientLight.y * n.y +
                ambientLight.z * n.z);

        red = (int) (color.r * 256) + delta;
        green = (int) (color.g * 256) + delta;
        blue = (int) (color.b * 256) + delta;

        g.setColor(new LColor(limitColor(red), limitColor(green), limitColor(blue)));
    }

    private void renderLambertFlat(GLEx g, Vector3f v1, Vector3f v2, Vector3f v3) {
        float[] xPoints = new float[3];
        float[] yPoints = new float[3];

        xPoints[0] =  v1.x;
        yPoints[0] =  v1.y;
        xPoints[1] = v2.x;
        yPoints[1] =  v2.y;
        xPoints[2] = v3.x;
        yPoints[2] =  v3.y;

        g.fillPolygon(xPoints, yPoints, 3);
    }


    BoxViewer3D() {
        vertex = null;
        textureVertex = null;
        vertexNormal = null;
        face = null;

        rotatedVertex = null;
        faceNormal = null;
        rotatedFaceNormal = null;

        ambientLight = new Vector3f();
        setAmbientLight(1.0f, 1.0f, 1.0f, 64.0f);

        color = new LColor(0.5f, 0.5f, 0.5f);

        vertices = 0;
        textureVertices = 0;
        vertexNormals = 0;
        faces = 0;
    }

    void rotate(float x, float y, float z, float ax, float ay, float az) {
        float rz = 0.0f;
        float[][] matrix = null;

        matrix = createRotationMatrix(0, 0, 0, ax, ay, az);
        for (int i = 0; i < getFaces(); i++) {
            rotatedFaceNormal[i].set(
                    matrix[0][0] * faceNormal[i].x +
                            matrix[0][1] * faceNormal[i].y +
                            matrix[0][2] * faceNormal[i].z +
                            matrix[0][3],
                    matrix[1][0] * faceNormal[i].x +
                            matrix[1][1] * faceNormal[i].y +
                            matrix[1][2] * faceNormal[i].z +
                            matrix[1][3],
                    matrix[2][0] * faceNormal[i].x +
                            matrix[2][1] * faceNormal[i].y +
                            matrix[2][2] * faceNormal[i].z +
                            matrix[2][3]);
        }

        matrix = createRotationMatrix(x, y, z, ax, ay, az);
        for (int i = 0; i < getVertices(); i++) {
            rz = matrix[2][0] * vertex[i].x +
                    matrix[2][1] * vertex[i].y +
                    matrix[2][2] * vertex[i].z + matrix[2][3];

            rotatedVertex[i].set(
                    (1024 * (matrix[0][0] * vertex[i].x +
                            matrix[0][1] * vertex[i].y +
                            matrix[0][2] * vertex[i].z +
                            matrix[0][3])) / rz,
                    (1024 * (matrix[1][0] * vertex[i].x +
                            matrix[1][1] * vertex[i].y +
                            matrix[1][2] * vertex[i].z +
                            matrix[1][3])) / rz,
                    rz);
        }
    }

    void render(GLEx g) {
        int n = 0;

        zValue = new int[faces];
        for (int i = 0; i < getFaces(); i++) {
            if (true == isPolygonVisible(
                    rotatedVertex[(int)face[i].vertexIndex.x],
                    rotatedVertex[(int)face[i].vertexIndex.y],
                    rotatedVertex[(int)face[i].vertexIndex.z])) {
                zValue[n] = i;
                zValue[n] += (int) (
                        rotatedVertex[(int) face[i].vertexIndex.x].z +
                                rotatedVertex[(int) face[i].vertexIndex.y].z +
                                rotatedVertex[(int) face[i].vertexIndex.z].z) << 16;
                n++;
            }
        }

        sort(0, n - 1);

        for (int i = 0; i < n; i++) {
            initLambertFlat(g, rotatedFaceNormal[zValue[i] & 65535]);

            renderLambertFlat(g,
                    rotatedVertex[(int)face[zValue[i] & 65535].vertexIndex.x],
                    rotatedVertex[(int)face[zValue[i] & 65535].vertexIndex.y],
                    rotatedVertex[(int)face[zValue[i] & 65535].vertexIndex.z]);
        }
    }

    void allocateVertices(int vertices) {
        vertex = new Vector3f[vertices];
        rotatedVertex = new Vector3f[vertices];

        this.vertices = vertices;
        for (int i = 0; i < vertices; i++) {
            vertex[i] = new Vector3f();
            rotatedVertex[i] = new Vector3f();
        }
    }

    void allocateTextureVertices(int textureVertices) {
        textureVertex = new TextureVector[textureVertices];

        this.textureVertices = textureVertices;
        for (int i = 0; i < textureVertices; i++) {
            textureVertex[i] = new TextureVector();
        }
    }

    void allocateVertexNormals(int vertexNormals) {
        vertexNormal = new Vector3f[vertexNormals];

        this.vertexNormals = vertexNormals;
        for (int i = 0; i < vertexNormals; i++) {
            vertexNormal[i] = new Vector3f();
        }
    }

    void allocateFaces(int faces) {
        face = new Face[faces];
        faceNormal = new Vector3f[faces];
        rotatedFaceNormal = new Vector3f[faces];

        this.faces = faces;
        for (int i = 0; i < faces; i++) {
            face[i] = new Face();
            faceNormal[i] = new Vector3f();
            rotatedFaceNormal[i] = new Vector3f();
        }
    }

    int getVertices() {
        return vertices;
    }

    int getTextureVertices() {
        return textureVertices;
    }

    int getVertexNormals() {
        return vertexNormals;
    }

    int getFaces() {
        return faces;
    }

    void calculateFaceNormals() {
        for (int i = 0; i < faces; i++) {
            float x1 = vertex[(int) face[i].vertexIndex.y].x - vertex[(int) face[i].vertexIndex.x].x;
            float y1 = vertex[(int) face[i].vertexIndex.y].y - vertex[(int) face[i].vertexIndex.x].y;
            float z1 = vertex[(int) face[i].vertexIndex.y].z - vertex[(int) face[i].vertexIndex.x].z;
            float x2 = vertex[(int) face[i].vertexIndex.z].x - vertex[(int) face[i].vertexIndex.x].x;
            float y2 = vertex[(int) face[i].vertexIndex.z].y - vertex[(int) face[i].vertexIndex.x].y;
            float z2 = vertex[(int) face[i].vertexIndex.z].z - vertex[(int) face[i].vertexIndex.x].z;

            float nx = y1 * z2 - z1 * y2;
            float ny = z1 * x2 - x1 * z2;
            float nz = x1 * y2 - y1 * x2;

            float d = MathUtils.sqrt(nx * nx + ny * ny + nz * nz);

            faceNormal[i].set(nx / d, ny / d, nz / d);
        }

        setAmbientLight(
                ambientLight.x,
                ambientLight.y,
                ambientLight.z,
                64.0f);
    }

    void setAmbientLight(float x, float y, float z, float f) {
        float d = MathUtils.sqrt(x * x + y * y + z * z);

        ambientLight.set(x * f / d, y * f / d, z * f / d);
    }

    void setColor(float r, float g, float b) {
        color.setColor(r, g, b);
    }

}
