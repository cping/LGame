namespace Loon.Core {
	
	public sealed class RefObject<T> {
		public T argvalue;
	
		public RefObject(T refarg) {
			argvalue = refarg;
		}
	}
}
