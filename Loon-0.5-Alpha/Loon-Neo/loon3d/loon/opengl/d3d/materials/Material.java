package loon.opengl.d3d.materials;

import java.util.Comparator;
import java.util.Iterator;

import loon.utils.InsertionSorter;
import loon.utils.TArray;

public class Material implements Iterable<Material.Attribute>,
		Comparator<Material.Attribute> {

	private final static InsertionSorter<Material.Attribute> sorter = new InsertionSorter<Material.Attribute>();

	public static abstract class Attribute {

		protected static long register(final String type) {
			return Material.register(type);
		}

		public final long type;

		protected Attribute(final long type) {
			this.type = type;
		}

		public abstract Attribute cpy();

		protected abstract boolean equals(Attribute other);

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (!(obj instanceof Attribute))
				return false;
			final Attribute other = (Attribute) obj;
			if (other.type != other.type)
				return false;
			return equals(other);
		}

		@Override
		public String toString() {
			return Material.getAttributeAlias(type);
		}
	}

	private final static TArray<String> types = new TArray<String>();

	private static int counter = 0;

	protected final static long getAttributeType(final String alias) {
		for (int i = 0; i < types.size; i++) {
			if (types.get(i).compareTo(alias) == 0) {
				return 1L << i;
			}
		}
		return 0;
	}

	protected final static String getAttributeAlias(final long type) {
		int idx = -1;
		while (type != 0 && ++idx < 63 && (((type >> idx) & 1) == 0))
			;
		return (idx >= 0 && idx < types.size) ? types.get(idx) : null;
	}

	protected final static long register(final String alias) {
		long result = getAttributeType(alias);
		if (result > 0) {
			return result;
		}
		types.add(alias);
		return 1L << (types.size - 1);
	}

	public String id;
	protected long mask;
	protected final TArray<Attribute> attributes = new TArray<Attribute>();
	protected boolean sorted = true;

	public Material() {
		this("mater" + (++counter));
	}

	public Material(final String id) {
		this.id = id;
	}

	public Material(final Attribute... attributes) {
		this();
		set(attributes);
	}

	public Material(final String id, final Attribute... attributes) {
		this(id);
		set(attributes);
	}

	public Material(final TArray<Attribute> attributes) {
		this();
		set(attributes);
	}

	public Material(final String id, final TArray<Attribute> attributes) {
		this(id);
		set(attributes);
	}

	public Material(final Material copyFrom) {
		this(copyFrom.id, copyFrom);
	}

	public Material(final String id, final Material copyFrom) {
		this(id);
		for (Attribute attr : copyFrom)
			set(attr.cpy());
	}

	private final void enable(final long mask) {
		this.mask |= mask;
	}

	private final void disable(final long mask) {
		this.mask &= -1L ^ mask;
	}

	public final long getMask() {
		return mask;
	}

	public final boolean has(final long type) {
		return type > 0 && (this.mask & type) == type;
	}

	protected int indexOf(final long type) {
		if (has(type)) {
			for (int i = 0; i < attributes.size; i++) {
				if (attributes.get(i).type == type) {
					return i;
				}
			}
		}
		return -1;
	}

	public final void set(final Attribute attribute) {
		final int idx = indexOf(attribute.type);
		if (idx < 0) {
			enable(attribute.type);
			attributes.add(attribute);
			sorted = false;
		} else {
			attributes.set(idx, attribute);
		}
	}

	public final void set(final Attribute... attributes) {
		for (final Attribute attr : attributes) {
			set(attr);
		}
	}

	public final void set(final TArray<Attribute> attributes) {
		for (final Attribute attr : attributes)
			set(attr);
	}

	public final void remove(final long mask) {
		for (int i = 0; i < attributes.size; i++) {
			final long type = attributes.get(i).type;
			if ((mask & type) == type) {
				attributes.removeIndex(i);
				disable(type);
				sorted = false;
			}
		}
	}

	public final Attribute get(final long type) {
		if (has(type)) {
			for (int i = 0; i < attributes.size; i++) {
				if (attributes.get(i).type == type) {
					return attributes.get(i);
				}
			}
		}
		return null;
	}

	public final TArray<Attribute> get(final TArray<Attribute> out,
			final long type) {
		for (int i = 0; i < attributes.size; i++) {
			if ((attributes.get(i).type & type) != 0) {
				out.add(attributes.get(i));
			}
		}
		return out;
	}

	public final void clear() {
		mask = 0;
		attributes.clear();
	}

	public int size() {
		return attributes.size;
	}

	public final Material cpy() {
		return new Material(this);
	}

	@Override
	public final int compare(final Attribute arg0, final Attribute arg1) {
		return (int) (arg0.type - arg1.type);
	}

	public final void sort() {
		if (!sorted) {
			sorter.sort(attributes, this);
			sorted = true;
		}
	}

	public final boolean same(final Material other) {
		return mask == other.mask;
	}

	public final boolean equals(final Material other) {
		if (other == null){
			return false;
		}
		if (other == this){
			return true;
		}
		if (!same(other)){
			return false;
		}
		sort();
		other.sort();
		for (int i = 0; i < attributes.size; i++) {
			if (!attributes.get(i).equals(other.attributes.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final boolean equals(final Object obj) {
		return obj instanceof Material ? equals((Material) obj) : false;
	}

	@Override
	public final Iterator<Attribute> iterator() {
		return attributes.iterator();
	}
}
