package loon.event;

public interface QueryEvent<T> {
	
	 boolean hit(T t);

}
