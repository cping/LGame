namespace Loon.Utils.Xml {
	
	public class XMLProcessing {
	
		private string text;
	
		public XMLProcessing(string paramString) {
			this.text = paramString;
		}
	
		public string GetText() {
			return this.text;
		}
	
		public override string ToString() {
			return "<?" + this.text + "?>";
		}
	
	}
}
