package loon;

public interface Bundle<T> {
    void put(String key, T value);
    T get(String key);
    T get(String key, T defaultValue);
    T remove(String key);
    T remove(String key, T defaultValue);
}