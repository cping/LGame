package loon.opengl;

import loon.BaseIO;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.geom.Vector3f;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.parse.StrTokenizer;

/**这是一个3D对象预览用组件，可以导出3D对象的基本骨骼图，另外，此组件允许直接把3D图像输出在Pixmap上.方便用户静态使用**/
public class PreBoxViewer3D {

	private boolean showGrid = true;

	private LColor gridColor = new LColor(LColor.red);

	public class PreFace {
		int v1, v2, v3;
		int t1, t2, t3;
		int n1, n2, n3;

		PreFace() {
			v1 = 0;
			v2 = 0;
			v3 = 0;
			t1 = 0;
			t2 = 0;
			t3 = 0;
			n1 = 0;
			n2 = 0;
			n3 = 0;
		}

		void setVertices(int v1, int v2, int v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}

		void setTextureVertices(int t1, int t2, int t3) {
			this.t1 = t1;
			this.t2 = t2;
			this.t3 = t3;
		}

		void setNormalVertices(int n1, int n2, int n3) {
			this.n1 = n1;
			this.n2 = n2;
			this.n3 = n3;
		}
	}

	public class PreTextureVertex {
		Vector3f position;

		PreTextureVertex() {
			position = new Vector3f(0.0f, 0.0f, 0.0f);
		}

		void setPosition(float u, float v, float w) {
			position.x = u;
			position.y = v;
			position.z = w;
		}
	}

	public class PreVertex {
		Vector3f position;

		PreVertex() {
			position = new Vector3f(0.0f, 0.0f, 0.0f);
		}

		void setPosition(float x, float y, float z) {
			position.x = x;
			position.y = y;
			position.z = z;
		}
	}

	PreVertex[] vertex;
	PreTextureVertex[] textureVertex;
	PreVertex[] vertexNormal;
	PreFace[] face;

	PreVertex[] rotatedVertex;
	PreVertex[] faceNormal;
	PreVertex[] rotatedFaceNormal;

	LColor color;
	PreVertex ambientLight;

	int vertices;
	int textureVertices;
	int vertexNormals;
	int faces;

	private int zValue[];

	PreBoxViewer3D() {
		vertex = null;
		textureVertex = null;
		vertexNormal = null;
		face = null;

		rotatedVertex = null;
		faceNormal = null;
		rotatedFaceNormal = null;

		ambientLight = new PreVertex();
		setAmbientLight(1.0f, 1.0f, 1.0f, 64.0f);

		color = new LColor(0.5f, 0.5f, 0.5f);

		vertices = 0;
		textureVertices = 0;
		vertexNormals = 0;
		faces = 0;
	}

	private float[][] createRotationMatrix(float x, float y, float z, float ax,
			float ay, float az) {
		float[][] matrix = new float[4][4];

		matrix[0][0] = (MathUtils.cos(ay) * MathUtils.cos(az));
		matrix[1][0] = (MathUtils.cos(ay) * MathUtils.sin(az));
		matrix[2][0] = (-MathUtils.sin(ay));
		matrix[3][0] = 0.0f;
		matrix[0][1] = (MathUtils.sin(ax) * MathUtils.sin(ay)
				* MathUtils.cos(az) - MathUtils.cos(ax) * MathUtils.sin(az));
		matrix[1][1] = (MathUtils.sin(ax) * MathUtils.sin(ay)
				* MathUtils.sin(az) + MathUtils.cos(ax) * MathUtils.cos(az));
		matrix[2][1] = (MathUtils.sin(ax) * MathUtils.cos(ay));
		matrix[3][1] = 0.0f;
		matrix[0][2] = (MathUtils.cos(ax) * MathUtils.sin(ay)
				* MathUtils.cos(az) + MathUtils.sin(ax) * MathUtils.sin(az));
		matrix[1][2] = (MathUtils.cos(ax) * MathUtils.sin(ay)
				* MathUtils.sin(az) - MathUtils.sin(ax) * MathUtils.cos(az));
		matrix[2][2] = (MathUtils.cos(ax) * MathUtils.cos(ay));
		matrix[3][2] = 0.0f;
		matrix[0][3] = x;
		matrix[1][3] = y;
		matrix[2][3] = z;
		matrix[3][3] = 1.0f;

		return matrix;
	}

	private boolean isPolygonVisible(PreVertex v1, PreVertex v2, PreVertex v3) {
		boolean returnValue = false;
		float dx1 = v3.position.x - v1.position.x;
		float dy1 = v3.position.y - v1.position.y;
		float dx2 = v3.position.x - v2.position.x;
		float dy2 = v3.position.y - v2.position.y;

		if ((dx1 * (dy2 - dy1) - (dx2 - dx1) * dy1) > 0) {
			returnValue = true;
		}

		return returnValue;
	}

	private void sort(int top, int bottom) {
		int i, j;
		int x, tmp;
		i = top;
		if (i < 0) {
			i = 0;
		}
		j = bottom;
		if (j < 0) {
			j = 0;
		}
		x = zValue[(top + bottom) / 2];
		do {
			while (zValue[i] < x) {
				i++;
			}

			while (x < zValue[j]) {
				j--;
			}

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

		if (top < j)
			sort(top, j);
		if (i < bottom)
			sort(i, bottom);
	}

	private int limitColor(int value) {
		if (value < 0) {
			value = 0;
		} else if (value > 255) {
			value = 255;
		}

		return value;
	}

	private void initLambertFlat(GLEx g, PreVertex n) {
		int delta = 0;
		int red = 0;
		int green = 0;
		int blue = 0;

		delta = (int) (ambientLight.position.x * n.position.x
				+ ambientLight.position.y * n.position.y + ambientLight.position.z
				* n.position.z);

		red = (int) (color.r * 255f) + delta;
		green = (int) (color.g * 255f) + delta;
		blue = (int) (color.b * 255f) + delta;

		g.setColor(limitColor(red), limitColor(green), limitColor(blue));
	}

	private void initLambertFlat(Pixmap g, PreVertex n) {
		int delta = 0;
		int red = 0;
		int green = 0;
		int blue = 0;

		delta = (int) (ambientLight.position.x * n.position.x
				+ ambientLight.position.y * n.position.y + ambientLight.position.z
				* n.position.z);

		red = (int) (color.r * 255f) + delta;
		green = (int) (color.g * 255f) + delta;
		blue = (int) (color.b * 255f) + delta;

		g.setColor(limitColor(red), limitColor(green), limitColor(blue));
	}

	final float[] xPoints = new float[3];
	final float[] yPoints = new float[3];

	private void drawLambertFlat(GLEx g, PreVertex v1, PreVertex v2,
			PreVertex v3) {

		xPoints[0] = v1.position.x;
		yPoints[0] = v1.position.y;
		xPoints[1] = v2.position.x;
		yPoints[1] = v2.position.y;
		xPoints[2] = v3.position.x;
		yPoints[2] = v3.position.y;

		if (showGrid) {
			g.setColor(gridColor);
			g.drawPolygon(xPoints, yPoints, 3);
		} else {
			g.fillPolygon(xPoints, yPoints, 3);
		}
	}

	final int[] ixPoints = new int[3];
	final int[] iyPoints = new int[3];

	private void drawLambertFlat(Pixmap g, PreVertex v1, PreVertex v2,
			PreVertex v3) {

		ixPoints[0] = (int) v1.position.x;
		iyPoints[0] = (int) v1.position.y;
		ixPoints[1] = (int) v2.position.x;
		iyPoints[1] = (int) v2.position.y;
		ixPoints[2] = (int) v3.position.x;
		iyPoints[2] = (int) v3.position.y;

		if (showGrid) {
			g.setColor(gridColor);
			g.drawPolygon(ixPoints, iyPoints, 3);
		} else {
			g.fillPolygon(ixPoints, iyPoints, 3);
		}
	}

	public void rotate(float x, float y, float z, float ax, float ay, float az) {
		float rz = 0.0f;
		float[][] matrix = null;

		matrix = createRotationMatrix(0, 0, 0, ax, ay, az);
		for (int i = 0; i < getFaces(); i++) {
			rotatedFaceNormal[i].setPosition(matrix[0][0]
					* faceNormal[i].position.x + matrix[0][1]
					* faceNormal[i].position.y + matrix[0][2]
					* faceNormal[i].position.z + matrix[0][3], matrix[1][0]
					* faceNormal[i].position.x + matrix[1][1]
					* faceNormal[i].position.y + matrix[1][2]
					* faceNormal[i].position.z + matrix[1][3], matrix[2][0]
					* faceNormal[i].position.x + matrix[2][1]
					* faceNormal[i].position.y + matrix[2][2]
					* faceNormal[i].position.z + matrix[2][3]);
		}

		matrix = createRotationMatrix(x, y, z, ax, ay, az);
		for (int i = 0; i < getVertices(); i++) {
			rz = matrix[2][0] * vertex[i].position.x + matrix[2][1]
					* vertex[i].position.y + matrix[2][2]
					* vertex[i].position.z + matrix[2][3];

			rotatedVertex[i].setPosition((1024 * (matrix[0][0]
					* vertex[i].position.x + matrix[0][1]
					* vertex[i].position.y + matrix[0][2]
					* vertex[i].position.z + matrix[0][3]))
					/ rz, (1024 * (matrix[1][0] * vertex[i].position.x
					+ matrix[1][1] * vertex[i].position.y + matrix[1][2]
					* vertex[i].position.z + matrix[1][3]))
					/ rz, rz);
		}
	}

	public void draw(GLEx g) {
		int n = 0;

		zValue = new int[faces];
		for (int i = 0; i < getFaces(); i++) {
			if (true == isPolygonVisible(rotatedVertex[face[i].v1],
					rotatedVertex[face[i].v2], rotatedVertex[face[i].v3])) {
				zValue[n] = i;
				zValue[n] += (int) (rotatedVertex[face[i].v1].position.z
						+ rotatedVertex[face[i].v2].position.z + rotatedVertex[face[i].v3].position.z) << 16;
				n++;
			}
		}

		sort(0, n - 1);

		int tmp = g.color();

		for (int i = 0; i < n; i++) {
			initLambertFlat(g, rotatedFaceNormal[zValue[i] & 65535]);

			drawLambertFlat(g, rotatedVertex[face[zValue[i] & 65535].v1],
					rotatedVertex[face[zValue[i] & 65535].v2],
					rotatedVertex[face[zValue[i] & 65535].v3]);
		}
		g.setColor(tmp);
	}

	void draw(Pixmap g) {
		int n = 0;

		zValue = new int[faces];
		for (int i = 0; i < getFaces(); i++) {
			if (true == isPolygonVisible(rotatedVertex[face[i].v1],
					rotatedVertex[face[i].v2], rotatedVertex[face[i].v3])) {
				zValue[n] = i;
				zValue[n] += (int) (rotatedVertex[face[i].v1].position.z
						+ rotatedVertex[face[i].v2].position.z + rotatedVertex[face[i].v3].position.z) << 16;
				n++;
			}
		}

		sort(0, n - 1);

		int tmp = g.color();

		for (int i = 0; i < n; i++) {
			initLambertFlat(g, rotatedFaceNormal[zValue[i] & 65535]);

			drawLambertFlat(g, rotatedVertex[face[zValue[i] & 65535].v1],
					rotatedVertex[face[zValue[i] & 65535].v2],
					rotatedVertex[face[zValue[i] & 65535].v3]);
		}
		g.setColor(tmp);
	}

	void allocateVertices(int vertices) {
		vertex = new PreVertex[vertices];
		rotatedVertex = new PreVertex[vertices];

		this.vertices = vertices;
		for (int i = 0; i < vertices; i++) {
			vertex[i] = new PreVertex();
			rotatedVertex[i] = new PreVertex();
		}
	}

	void allocateTextureVertices(int textureVertices) {
		textureVertex = new PreTextureVertex[textureVertices];
		this.textureVertices = textureVertices;
		for (int i = 0; i < textureVertices; i++) {
			textureVertex[i] = new PreTextureVertex();
		}
	}

	void allocateVertexNormals(int vertexNormals) {
		vertexNormal = new PreVertex[vertexNormals];
		this.vertexNormals = vertexNormals;
		for (int i = 0; i < vertexNormals; i++) {
			vertexNormal[i] = new PreVertex();
		}
	}

	void allocateFaces(int faces) {
		face = new PreFace[faces];
		faceNormal = new PreVertex[faces];
		rotatedFaceNormal = new PreVertex[faces];

		this.faces = faces;
		for (int i = 0; i < faces; i++) {
			face[i] = new PreFace();
			faceNormal[i] = new PreVertex();
			rotatedFaceNormal[i] = new PreVertex();
		}
	}

	int getVertices() {
		return vertices;
	}

	int getTextureVertices() {
		return textureVertices;
	}

	public int getVertexNormals() {
		return vertexNormals;
	}

	public int getFaces() {
		return faces;
	}

	public void calculateFaceNormals() {
		for (int i = 0; i < faces; i++) {
			float x1 = vertex[face[i].v2].position.x
					- vertex[face[i].v1].position.x;
			float y1 = vertex[face[i].v2].position.y
					- vertex[face[i].v1].position.y;
			float z1 = vertex[face[i].v2].position.z
					- vertex[face[i].v1].position.z;
			float x2 = vertex[face[i].v3].position.x
					- vertex[face[i].v1].position.x;
			float y2 = vertex[face[i].v3].position.y
					- vertex[face[i].v1].position.y;
			float z2 = vertex[face[i].v3].position.z
					- vertex[face[i].v1].position.z;

			float nx = y1 * z2 - z1 * y2;
			float ny = z1 * x2 - x1 * z2;
			float nz = x1 * y2 - y1 * x2;

			float d = MathUtils.sqrt(nx * nx + ny * ny + nz * nz);

			faceNormal[i].setPosition(nx / d, ny / d, nz / d);
		}

		setAmbientLight(ambientLight.position.x, ambientLight.position.y,
				ambientLight.position.z, 64.0f);
	}

	public void setAmbientLight(float x, float y, float z, float f) {
		float d = MathUtils.sqrt(x * x + y * y + z * z);

		ambientLight.setPosition(x * f / d, y * f / d, z * f / d);
	}

	public void setColor(float r, float g, float b) {
		color.setColor(r, g, b);
	}

	private static void parseVertex(String line, float[] vertex) {
		StrTokenizer stk = new StrTokenizer(line, " ");
		float w = 1.0f;

		if (true == stk.hasMoreTokens() && 0 == stk.nextToken().compareTo("v")) {
			if (true == stk.hasMoreTokens()) {
				vertex[0] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				vertex[1] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				vertex[2] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				w = Float.valueOf(stk.nextToken());
			}

			vertex[0] = vertex[0] / w;
			vertex[1] = vertex[1] / w;
			vertex[2] = vertex[2] / w;
		}

	}

	private static void parseTextureVertex(String line, float[] vertex) {
		StrTokenizer stk = new StrTokenizer(line, " ");
		if (true == stk.hasMoreTokens() && 0 == stk.nextToken().compareTo("vt")) {
			if (true == stk.hasMoreTokens()) {
				vertex[0] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				vertex[1] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				vertex[2] = Float.valueOf(stk.nextToken());
			} else {
				vertex[2] = 0.0f;
			}
		}

	}

	private static void parseVertexNormal(String line, float[] vertex) {
		StrTokenizer stk = new StrTokenizer(line, " ");
		float w = 1.0f;

		if (true == stk.hasMoreTokens() && 0 == stk.nextToken().compareTo("vn")) {
			if (true == stk.hasMoreTokens()) {
				vertex[0] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				vertex[1] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				vertex[2] = Float.valueOf(stk.nextToken());
			}
			if (true == stk.hasMoreTokens()) {
				w = Float.valueOf(stk.nextToken());
			}

			vertex[0] = vertex[0] / w;
			vertex[1] = vertex[1] / w;
			vertex[2] = vertex[2] / w;
		}

	}

	private static void parseFace(String line, int[] face) {
		StrTokenizer stk = new StrTokenizer(line, " ");
		if (stk.hasMoreTokens() && stk.nextToken().startsWith("f")) {
			for (int i = 0; i < 3; i++) {
				if (stk.hasMoreTokens()) {
					String s[] = stk.nextToken().split("/");
					face[i] = Integer.valueOf(s[0]) - 1;
					if (2 == s.length) {
						face[i + 3] = Integer.valueOf(s[1]) - 1;
					} else if (3 == s.length) {
						if (0 != s[1].length()) {
							face[i + 3] = Integer.valueOf(s[1]) - 1;
						}
						face[i + 6] = Integer.valueOf(s[2]) - 1;
					}
				}
			}
		}
	}

	private static boolean isVertexList(String line) {
		boolean returnValue = false;

		if (true == line.startsWith("v ")) {
			returnValue = true;
		}

		return returnValue;
	}

	private static boolean isTextureVertexList(String line) {
		boolean returnValue = false;

		if (true == line.startsWith("vt ")) {
			returnValue = true;
		}

		return returnValue;
	}

	private static boolean isVertexNormalList(String line) {
		boolean returnValue = false;

		if (true == line.startsWith("vn ")) {
			returnValue = true;
		}

		return returnValue;
	}

	private static boolean isFaceList(String line) {
		boolean returnValue = false;

		if (true == line.startsWith("f ")) {
			returnValue = true;
		}

		return returnValue;
	}

	private static PreBoxViewer3D parseObj(String text) {
		TArray<Float> vertexList = null;
		TArray<Float> textureVertexList = null;
		TArray<Float> vertexNormalList = null;
		TArray<Integer> faceList = null;
		String[] lines = StringUtils.split(text, '\n');
		PreBoxViewer3D mesh = null;
		float[] vertex = new float[3];
		int[] face = new int[9];
		int vertices = 0;
		int textureVertices = 0;
		int vertexNormals = 0;
		int faces = 0;

		for (String line : lines)

			if (null != line) {
				if (true == isVertexList(line)) {
					parseVertex(line, vertex);

					if (null == vertexList) {
						vertexList = new TArray<Float>();
					}

					vertexList.add(vertex[0]);
					vertexList.add(vertex[1]);
					vertexList.add(vertex[2]);

					vertices++;
				}

				if (true == isTextureVertexList(line)) {
					parseTextureVertex(line, vertex);

					if (null == textureVertexList) {
						textureVertexList = new TArray<Float>();
					}

					textureVertexList.add(vertex[0]);
					textureVertexList.add(vertex[1]);
					textureVertexList.add(vertex[2]);

					textureVertices++;
				}

				if (true == isVertexNormalList(line)) {
					parseVertexNormal(line, vertex);

					if (null == vertexNormalList) {
						vertexNormalList = new TArray<Float>();
					}

					vertexNormalList.add(vertex[0]);
					vertexNormalList.add(vertex[1]);
					vertexNormalList.add(vertex[2]);

					vertexNormals++;
				}

				if (true == isFaceList(line)) {
					parseFace(line, face);

					if (null == faceList) {
						faceList = new TArray<Integer>();
					}

					faceList.add(face[0]);
					faceList.add(face[1]);
					faceList.add(face[2]);
					faceList.add(face[3]);
					faceList.add(face[4]);
					faceList.add(face[5]);
					faceList.add(face[6]);
					faceList.add(face[7]);
					faceList.add(face[8]);

					faces++;
				}
			}

		mesh = new PreBoxViewer3D();
		mesh.allocateVertices(vertices);
		if (0 != textureVertices) {
			mesh.allocateTextureVertices(textureVertices);
		}
		if (0 != vertexNormals) {
			mesh.allocateVertexNormals(vertexNormals);
		}
		mesh.allocateFaces(faces);

		for (int i = 0; i < vertices; i++) {
			mesh.vertex[i].setPosition(vertexList.get(i * 3),
					vertexList.get(i * 3 + 1), vertexList.get(i * 3 + 2));
		}

		for (int i = 0; i < vertexNormals; i++) {
			mesh.vertexNormal[i].setPosition(vertexNormalList.get(i * 3),
					vertexNormalList.get(i * 3 + 1),
					vertexNormalList.get(i * 3 + 2));
		}

		for (int i = 0; i < textureVertices; i++) {
			mesh.textureVertex[i].setPosition(textureVertexList.get(i * 3),
					textureVertexList.get(i * 3 + 1),
					textureVertexList.get(i * 3 + 2));
		}

		for (int i = 0; i < faces; i++) {
			mesh.face[i].setVertices(faceList.get(i * 9),
					faceList.get(i * 9 + 1), faceList.get(i * 9 + 2));

			mesh.face[i].setTextureVertices(faceList.get(i * 9 + 3),
					faceList.get(i * 9 + 4), faceList.get(i * 9 + 5));

			mesh.face[i].setNormalVertices(faceList.get(i * 9 + 6),
					faceList.get(i * 9 + 7), faceList.get(i * 9 + 8));
		}
		return mesh;
	}

	public static PreBoxViewer3D load(String filename) {

		PreBoxViewer3D mesh = null;

		String text = BaseIO.loadText(filename);

		if (text != null) {
			mesh = parseObj(text);
		}

		return mesh;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public LColor getGridColor() {
		return gridColor;
	}

	public void setGridColor(LColor gridColor) {
		this.gridColor = gridColor;
	}
}
