using System;
namespace Loon.Utils.Xml {

	public class XMLTokenizer {
	
		private string text;
	
		private int pointer;
	
		public XMLTokenizer(string text_0) {
			this.text = text_0;
		}
	
		public bool HasMoreElements() {
			return this.pointer < this.text.Length;
		}
	
		public string NextElement() {
			if (this.text[this.pointer] == '<') {
				return NextTag();
			}
			return NextString();
		}
	
		private string NextTag() {
			int i = 0;
			int j = this.pointer;
			do {
				switch ((int) this.text[this.pointer]) {
				case '"':
					i = (i != 0) ? 0 : 1;
					break;
				}
				this.pointer += 1;
			} while ((this.pointer < this.text.Length)
					&& ((this.text[this.pointer] != '>') || (i != 0)));
			if (this.pointer < this.text.Length) {
				this.pointer += 1;
			} else {
				throw new Exception(
						"Tokenizer error: < without > at end of text");
			}
			return this.text.Substring(j,(this.pointer)-(j));
		}
	
		private string NextString() {
			int i = 0;
			int j = this.pointer;
			do {
				switch ((int) this.text[this.pointer]) {
				case '"':
					i = (i != 0) ? 0 : 1;
					break;
				}
				this.pointer += 1;
			} while ((this.pointer < this.text.Length)
					&& ((this.text[this.pointer] != '<') || (i != 0)));
			return this.text.Substring(j,(this.pointer)-(j));
		}
	
	}
}
