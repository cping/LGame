 namespace Loon.Action.Map {
	
	public class Attribute {
	
		private string name;
	
		private object attribute;
	
		public string GetName() {
			return this.name;
		}
	
		public void SetName(string n) {
			this.name = n;
		}
	
		public object GetAttribute() {
			return this.attribute;
		}
	
		public int GetAttributeInt() {
			return ((int) this.attribute);
		}
	
		public void SetAttribute(object a) {
			this.attribute = a;
		}
	
	}
}
