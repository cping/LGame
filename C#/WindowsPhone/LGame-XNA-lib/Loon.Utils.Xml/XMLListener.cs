namespace Loon.Utils.Xml {
	
	public interface XMLListener {
	
		void AddHeader(int line, XMLProcessing xp);
	
		void AddComment(int line, XMLComment c);
	
		void AddData(int line, XMLData data);
	
		void AddAttribute(int line, XMLAttribute a);
	
		void AddElement(int line, XMLElement e);
	
		void EndElement(int line, XMLElement e);
	}
}
