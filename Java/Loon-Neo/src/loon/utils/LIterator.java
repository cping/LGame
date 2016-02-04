package loon.utils;

import java.util.Iterator;

public interface LIterator<E> extends Iterator<E>{

    boolean hasNext();

    E next();

    void remove();
}
