package loon.utils;

/**
 * 全局通用存储类Bundle的接口
 * 
 * @param <T>
 */
public interface Bundle<T> extends IArray {

	void put(String key, T value);

	T get(String key);

	T get(String key, T defaultValue);

	T remove(String key);

	T remove(String key, T defaultValue);
}