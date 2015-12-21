package loon.utils.res;

public class ResourceItem {
	
	private String _name = null;

	private String _url = null;
	
	private String _type = null;

	public String subkeys = null;

	public ResourceItem(String name, String type, String url)
	{
		_name = name;
		_type = type;
		_url = url;
	}
	
	public String name()
	{
		return _name;
	}

	public String type()
	{
		return _type;
	}

	public String url()
	{
		return _url;
	}

}
