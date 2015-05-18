using UnityEngine;
using System.Collections;

namespace Loon
{

	public abstract class LGame : MonoBehaviour {


		private bool began = false;
		private int fingerId = -1;
		private int counter = 0;
		
		private UnityGameType m_type ;
		
		public UnityGameType GetGameType()
		{
			return m_type;
		}

		public void MaxScreen(int w, int h)
		{
			LSystem.MAX_SCREEN_WIDTH = m_type.setting.width;
			LSystem.MAX_SCREEN_HEIGHT = m_type.setting.height;
		}

		public void SetFPS(int fps){
			QualitySettings.vSyncCount = 0;
			Application.targetFrameRate = fps;
		}

		public int GetFPS(){
			return Application.targetFrameRate;
		}

		void OnPreRender() {
			UnityEngine.GL.wireframe = true;
		}

		void OnPostRender() {
			UnityEngine.GL.wireframe = false;
		}

		void OnPause()
		{
	
		}

		void OnResume()
		{
		}

		void OnDisable()
		{
		}

		void FixedUpdate()
		{

		}

		void OnApplicationFocus( bool focusState )
		{

		}

		void OnApplicationPause( bool pauseState )
		{

		}

		void OnApplicationQuit()
		{
	
		}

		private void OnTouchBegin(Touch touch) {
		
		}

		private void OnTouchStay(Touch touch) { }

		private void OnTouchMove(Touch touch) { }

		private void OnTouchEnd(Touch touch) { }
		
		public void drawString(object label, float x, float y, int fontSize, Color fontColor) {
			GUIStyle style = new GUIStyle(GUI.skin.GetStyle("label"));
			style.fontSize = fontSize;
			style.onFocused.textColor =
				style.onActive.textColor =
					style.onHover.textColor =
					style.onNormal.textColor =
					style.normal.textColor = fontColor;
			GUI.Label (new Rect(x, y, Screen.width, Screen.height), label.ToString (), style);
		}

		private void ResetTouch()
		{
			this.began = false;
			this.fingerId = -1;
		}

		void Start() {
			Screen.autorotateToPortrait = false;
			Screen.autorotateToPortraitUpsideDown = false;
			Screen.orientation = ScreenOrientation.Landscape;
			SetFPS(LSystem.DEFAULT_MAX_FPS);
			ResetTouch();
			this.OnMain();
		}

		public void OnMain(){
		}

		void Awake () {
			#if UNITY_IPHONE
			iPhoneKeyboard.autorotateToLandscapeLeft=false;
			iPhoneKeyboard.autorotateToLandscapeRight=false;
			iPhoneKeyboard.autorotateToPortrait=false;
			iPhoneKeyboard.autorotateToPortraitUpsideDown=false;	
            #endif
			Debug.Log("Awake");

		}

		void Update() {
	
			foreach (Touch touch in Input.touches) {
				if (GetComponent<BoxCollider2D>().OverlapPoint(Camera.main.ScreenToWorldPoint(touch.position))) {
					if (touch.phase == TouchPhase.Began) {
						counter++;
						OnTouchBegin(touch);
						began = true;
						fingerId = touch.fingerId;
					}
				}
				if (began && fingerId == touch.fingerId) {
					switch (touch.phase) {
					case TouchPhase.Stationary:
						OnTouchStay(touch);
						break;
					case TouchPhase.Moved:
						OnTouchMove(touch);
						break;
					case TouchPhase.Ended:
					case TouchPhase.Canceled:
						OnTouchEnd(touch);
						break;
					}
				}
			}
		}

		public void Exit()
		{
			Application.Quit();
		}

	}

}