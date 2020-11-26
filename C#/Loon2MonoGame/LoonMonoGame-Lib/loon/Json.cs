using java.lang;
using java.util;
using loon.utils.json;

namespace loon
{
	public interface Json_TypedArray<T>
	{

		int Length();

		T Get(int index);

		T Get(int index, T dflt);

		Iterator<T> Iterator();
	}


	public interface Json_Object
	{

		bool GetBoolean(string key);

		bool GetBoolean(string key, bool dflt);

		float GetNumber(string key);

		float GetNumber(string key, float dflt);

		double GetDouble(string key);

		double GetDouble(string key, double dflt);

		int GetInt(string key);

		int GetInt(string key, int dflt);

		long GetLong(string key);

		long GetLong(string key, long dflt);

		string GetString(string key);

		string GetString(string key, string dflt);

		Json_Object GetObject(string key);

		Json_Object GetObject(string key, Json_Object dflt);

		Json_Array GetArray(string key);

		Json_Array GetArray(string key, Json_Array dflt);

		bool ContainsKey(string key);

		Json_TypedArray<string> Keys();

		bool IsArray(string key);

		bool IsBoolean(string key);

		bool IsNull(string key);

		bool IsNumber(string key);

		bool IsString(string key);

		bool IsObject(string key);

		Json_Object Put(string key, object value);

		Json_Object Remove(string key);

		JsonSink<T> Write<T>(JsonSink<T> sink) where T : JsonSink<T>;


	}

	public interface Json_Array
	{

		int Length();

		bool GetBoolean(int index);

		bool GetBoolean(int index, bool dflt);

		float GetNumber(int index);

		float GetNumber(int index, float dflt);

		double GetDouble(int index);

		double GetDouble(int index, double dflt);

		int GetInt(int index);

		int GetInt(int index, int dflt);

		long GetLong(int index);

		long GetLong(int index, long dflt);

		string GetString(int index);

		string GetString(int index, string dflt);

		Json_Object GetObject(int index);

		Json_Object GetObject(int index, Json_Object dflt);

		Json_Array GetArray(int index);

		Json_Array GetArray(int index, Json_Array dflt);

		bool IsArray(int index);

		bool IsBoolean(int index);

		bool IsNull(int index);

		bool IsNumber(int index);

		bool IsString(int index);

		bool IsObject(int index);

		Json_Array Add(object value);

		Json_Array Add(int index, object value);

		Json_Array Remove(int index);

		Json_Array Set(int index, object value);

		JsonSink<T> Write<T>(JsonSink<T> sink) where T : JsonSink<T>;
	}

}
