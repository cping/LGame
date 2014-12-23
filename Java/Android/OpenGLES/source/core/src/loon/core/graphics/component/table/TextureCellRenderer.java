package loon.core.graphics.component.table;

import loon.core.geom.Dimension;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

public class TextureCellRenderer implements ICellRenderer
{
	private boolean scaleTexture = true;

	public void paint(GLEx g, Object value, int x, int y, int width, int height)
	{

		if(!(value instanceof LTexture)){
			return;
		}
		
		LTexture textire = (LTexture)value;
		
		g.setColor(LColor.white);
		
		if(scaleTexture){
			g.drawTexture(textire, x, y, width, height);
		}
		else{
			g.drawTexture(textire, x, y);
		}
	}

	public void setScaleTexture(boolean s)
	{
		this.scaleTexture = s;
	}

	public Dimension getCellContentSize(Object value)
	{
		if(value == null){
			return null;
		}
		
		if(!(value instanceof LTexture)){
			return null;
		}
		
		LTexture texture = (LTexture)value;
		
		return new Dimension(texture.getWidth(), texture.getHeight());
	}
}
