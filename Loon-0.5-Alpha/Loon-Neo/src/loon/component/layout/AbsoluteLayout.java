package loon.component.layout;

import loon.geom.RectBox;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class AbsoluteLayout implements LayoutManager {
	
	  private LayoutProcess post;

	  public AbsoluteLayout() {
	    this.post = new DefaultPostProcess();
	  }

	  public AbsoluteLayout(final LayoutProcess post) {
	    this.post = post;
	  }

	  public void layoutElements(
	      final LayoutPort rootElement,
	      final TArray < LayoutPort > elements) {

	    if (rootElement == null || elements == null || elements.size == 0) {
	      return;
	    }

	    int rootBoxX = getRootBoxX(rootElement);
	    int rootBoxY = getRootBoxY(rootElement);
	    int rootBoxWidth = getRootBoxWidth(rootElement);
	    int rootBoxHeight = getRootBoxHeight(rootElement);

	    for (int i = 0; i < elements.size; i++) {
	      LayoutPort p = elements.get(i);
	      RectBox box = p.getBox();
	      LayoutConstraints cons = p.getBoxConstraints();

	      if (cons != null) {
	        if (cons.getX() != null) {
	          box.setX(rootBoxX + cons.getX().getValueAsInt(rootBoxWidth));
	        }

	        if (cons.getY() != null) {
	          box.setY(rootBoxY + cons.getY().getValueAsInt(rootBoxHeight));
	        }

	        if (cons.getWidth() != null && cons.getWidth().hasHeightSuffix()) {
	          if (cons.getHeight() != null) {
	            box.setHeight(cons.getHeight().getValueAsInt(rootBoxHeight));
	          }
	          box.setWidth(cons.getWidth().getValueAsInt(box.getHeight()));
	        } else if (cons.getHeight() != null && cons.getHeight().hasWidthSuffix()) {
	          if (cons.getWidth() != null) {
	            box.setWidth(cons.getWidth().getValueAsInt(rootBoxWidth));
	          }
	          box.setHeight(cons.getHeight().getValueAsInt(box.getWidth()));
	        } else {
	          if (cons.getWidth() != null) {
	            box.setWidth(cons.getWidth().getValueAsInt(rootBoxWidth));
	          }
	          if (cons.getHeight() != null) {
	            box.setHeight(cons.getHeight().getValueAsInt(rootBoxHeight));
	          }
	        }

	        post.process(rootBoxX, rootBoxY, rootBoxWidth, rootBoxHeight, box);
	      }
	    }
	  }

	  public final SizeValue calculateConstraintWidth(final LayoutPort root, final TArray < LayoutPort > children) {
	    return null;
	  }

	  public final SizeValue calculateConstraintHeight(final LayoutPort root, final TArray < LayoutPort > children) {
	    return null;
	  }

	  private int getRootBoxX(final LayoutPort root) {
	    return root.getBox().x() + root.getBoxConstraints().getPaddingLeft().getValueAsInt(root.getBox().getWidth());
	  }

	  private int getRootBoxY(final LayoutPort root) {
	    return root.getBox().y() + root.getBoxConstraints().getPaddingTop().getValueAsInt(root.getBox().getHeight());
	  }

	  private int getRootBoxWidth(final LayoutPort root) {
	    return root.getBox().width() - root.getBoxConstraints().getPaddingLeft().getValueAsInt(root.getBox().getWidth()) - root.getBoxConstraints().getPaddingRight().getValueAsInt(root.getBox().getWidth());
	  }

	  private int getRootBoxHeight(final LayoutPort root) {
	    return root.getBox().height() - root.getBoxConstraints().getPaddingTop().getValueAsInt(root.getBox().getHeight()) - root.getBoxConstraints().getPaddingBottom().getValueAsInt(root.getBox().getHeight());
	  }

	  public interface LayoutProcess {
	    void process(int rootBoxX, int rootBoxY, int rootBoxWidth, int rootBoxHeight, RectBox box);
	  }

	  public static class DefaultPostProcess implements LayoutProcess {
	    @Override
	    public void process(final int rootBoxX, final int rootBoxY, final int rootBoxWidth, final int rootBoxHeight, final RectBox box) {
	    }
	  }

	  public static class KeepInsidePostProcess implements LayoutProcess {
	    @Override
	    public void process(final int rootBoxX, final int rootBoxY, final int rootBoxWidth, final int rootBoxHeight, final RectBox box) {
	      int width = rootBoxWidth - box.width();
	      int height = rootBoxHeight - box.height();
	      if (box.getX() < rootBoxX) {
	        box.setX(rootBoxX);
	      }
	      if (box.getX() > width) {
	        box.setX(width);
	      }
	      if (box.getY() < rootBoxY) {
	        box.setY(rootBoxY);
	      }
	      if (box.getY() > height) {
	        box.setY(height);
	      } 
	    }
	  }
	}
