package loon.events;

public interface QueryEvent<T> {
	
	 boolean hit(T t);

}
