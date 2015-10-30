package loon;

public interface Platform {

	public static enum Orientation {
		Portrait, Landscape;
	}
	
	public abstract void close();

	public abstract int getContainerWidth();

	public abstract int getContainerHeight();

	public abstract Orientation getOrientation() ;

	public abstract LGame getGame() ;
}
