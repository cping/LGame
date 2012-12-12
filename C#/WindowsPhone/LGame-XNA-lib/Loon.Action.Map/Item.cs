namespace Loon.Action.Map {
	
	public class Item {
	
		private string name;
	
		private object value;
	
		public string GetName() {
			return this.name;
		}
	
		public void SetName(string n) {
			this.name = n;
		}
	
		public object GetObject() {
            return this.value;
		}
	
		public void SetObject(object o) {
            this.value = o;
		}
	
	}
}
