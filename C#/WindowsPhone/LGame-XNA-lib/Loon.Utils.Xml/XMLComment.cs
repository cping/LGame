namespace Loon.Utils.Xml {
	
	public class XMLComment {
	
		private string text;

        public string GetText()
        {
			return this.text;
		}

        public override string ToString()
        {
			return "<!--" + this.text + "-->";
		}

        public XMLComment(string paramString)
        {
			this.text = paramString;
		}
	}
}
