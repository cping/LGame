namespace Loon.Utils.Xml {
	
	public class XMLData {
	
		private string text;
	
		public override string ToString() {
			return this.text;
		}
	
		public XMLData(string paramString) {
			this.text = paramString;
		}
	}
}
