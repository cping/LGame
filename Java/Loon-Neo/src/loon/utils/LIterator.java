package loon.utils;

public interface LIterator<E>{

    boolean hasNext();

    E next();

    void remove();
}
