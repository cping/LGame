package loon.component.layout;

import loon.geom.RectBox;

public class LayoutPort {

	  private RectBox box;

	  private LayoutConstraints boxConstraints;

	  public LayoutPort() {
	    this.box = new RectBox();
	    this.boxConstraints = new LayoutConstraints();
	  }

	  public LayoutPort(final RectBox newBox, final LayoutConstraints newBoxConstraints) {
	    this.box = newBox;
	    this.boxConstraints = newBoxConstraints;
	  }

	  public LayoutPort(final LayoutPort src) {
	    this.box = new RectBox(src.getBox());
	    this.boxConstraints = new LayoutConstraints(src.getBoxConstraints());
	  }

	  public final RectBox getBox() {
	    return box;
	  }

	  public final LayoutConstraints getBoxConstraints() {
	    return boxConstraints;
	  }

	  @Override
	  public String toString() {
	    StringBuffer result = new StringBuffer();
	    result.append("box [" + box.getX() + ", " + box.getY() + ", " + box.getWidth() + ", " + box.getHeight() + "] with constraints [" + boxConstraints.getX() + ", " + boxConstraints.getY() + ", " + boxConstraints.getWidth() + ", " + boxConstraints.getHeight() + "]");
	    return result.toString();
	  }
}
