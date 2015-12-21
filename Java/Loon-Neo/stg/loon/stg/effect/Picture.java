package loon.stg.effect;

import loon.LRelease;
import loon.opengl.GLEx;
import loon.stg.STGPlane;

public interface Picture extends LRelease {

	boolean paint(GLEx g, STGPlane p);

}
