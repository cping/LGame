namespace Loon.Core {
	
	public abstract class Asset {
	
		public string AssetName;
	
		public Asset(string name) {
			this.AssetName = name;
		}
	
		public abstract void Load();
	
	}
}
