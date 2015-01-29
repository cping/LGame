package loon.gwtref.client;

public class Parameter {
	final String name;
	final CachedTypeLookup type;
	final String jnsi;

	Parameter (String name, Class<?> type, String jnsi) {
		this.name = name;
		this.type = new CachedTypeLookup(type);
		this.jnsi = jnsi;
	}

	public String getName () {
		return name;
	}

	public Type getType () {
		return type.getType();
	}

	public Class<?> getClazz () {
		return type.clazz;
	}

	public String getJnsi () {
		return jnsi;
	}

	@Override
	public String toString () {
		return "Parameter [name=" + name + ", type=" + type + ", jnsi=" + jnsi + "]";
	}
}
