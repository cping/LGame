package loon.gwtref.client;

public interface IReflectionCache {

	public Type forName (String name);

	public Object newArray (Type componentType, int size);

	public int getArrayLength (Type type, Object obj);

	public Object getArrayElement (Type type, Object obj, int i);

	public void setArrayElement (Type type, Object obj, int i, Object value);

	public Object get (Field field, Object obj) throws IllegalAccessException;

	public void set (Field field, Object obj, Object value) throws IllegalAccessException;

	public Object invoke (Method m, Object obj, Object[] params);
}
