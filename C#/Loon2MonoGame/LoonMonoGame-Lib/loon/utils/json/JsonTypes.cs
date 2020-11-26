namespace loon.utils.json
{
	internal class JsonTypes
	{

		public static bool IsArray(object o)
		{
			return o is loon.Json_Array;
		}

		public static bool IsObject(object o)
		{
			return o is loon.Json_Object;
		}

	}
}
