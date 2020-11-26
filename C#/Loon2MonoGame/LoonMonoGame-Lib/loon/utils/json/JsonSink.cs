using java.lang;

namespace loon.utils.json
{

	public interface JsonSink<I> where I : JsonSink<I>
	{

		I Array(TArray<object> c);

		I Array(loon.Json_Array c);

		I Array(string key, TArray<object> c);

		I Array(string key, loon.Json_Array c);

		I Object(ArrayMap map);

		I Object(loon.Json_Object map);

		I Object(string key, ArrayMap map);

		I Object(string key, loon.Json_Object map);

		I Nul();

		I Nul(string key);

		I Value(object o);

		I Value(string key, object o);

		I Value(string s);

		I Value(bool b);

		I Value(double n);

		I Value(Number n);

		I Value(string key, string s);

		I Value(string key, bool b);

		I Value(string key, double n);

		I Value(string key, Number n);

		I Array();

		I Object();

		I Array(string key);

		I Object(string key);

		I End();
	}

}
