package loon.utils;

public interface Flip<T> {

	public T setFlipX(boolean x) ;

	public T setFlipY(boolean y);

	public T setFlipXY(boolean x, boolean y);
	
	public boolean isFlipX();
	
	public boolean isFlipY();
}
