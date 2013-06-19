using Loon.Core;
using System.Text;
namespace Loon.Foundation {

	public class NSRange : NSObject {
		public int start;
		public int end;
	
		public NSRange(int start_0, int end_1) {
			this.start = 0;
			this.end = 0;
			if (start_0 < end_1) {
				this.start = start_0;
				this.end = end_1;
			}
		}
	
		protected internal override void AddSequence(StringBuilder sbr, string indent) {
			sbr.Append(indent);
			sbr.Append("<range>");
			sbr.Append(LSystem.LS);
			sbr.Append("<start>");
			sbr.Append(start);
			sbr.Append("</start>");
			sbr.Append("<end>");
			sbr.Append(end);
			sbr.Append("</end>");
			sbr.Append(LSystem.LS);
			sbr.Append("</range>");
		}

	}
}
