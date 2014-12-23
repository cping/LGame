package loon.core.graphics.component.table;

import loon.core.geom.Dimension;
import loon.core.graphics.opengl.GLEx;

public interface ICellRenderer
{

	public void paint(GLEx g, Object value, int x, int y, int width, int height);

	public Dimension getCellContentSize(Object value);
}
