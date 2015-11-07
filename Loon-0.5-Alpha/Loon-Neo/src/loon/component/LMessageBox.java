package loon.component;

import java.util.List;

import loon.font.LFont;

public class LMessageBox {

	public class Message {
		private String message;
		private String comment;
		private String face;

		public Message() {
			this.message = "";
			this.comment = "";
		}

		public String getMessage() {
			return this.message;
		}

		public String getComment() {
			return this.comment;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
		
		public String getFace(){
			return this.face;
		}
		
		public void setFace(String face){
			this.face = face;
		}
	}

	protected int messageIndex;
	protected List<Message> messageList;
	protected List<String> lines;
	protected int typeDelayTime;
	protected int renderRow;
	protected int renderCol;
	protected boolean finished;
	protected boolean noMessage;
	protected boolean currentNoMessage;
	protected boolean stopMessage;
	protected boolean noPaged;
	protected boolean isPaged;
	protected int pageBlinkTime;
	public static int MESSAGE_TYPE_INTERVAL = 50;
	public static final int MESSAGE_DEFAULT_TYPE_INTERVAL = 50;
	public static int MESSAGE_PAGE_BLINK_TIME = 300;

	public final List<String> _list;

	public LMessageBox(String text, LFont font, int w) {
		this._list = Print.formatMessage(text, font, w);
	}

	  protected void updateType()
	  {
	    if ((this.typeDelayTime <= 0) && (!this.finished)) {
	      this.typeDelayTime = MESSAGE_TYPE_INTERVAL;

	      if (this.renderCol > ((String)this.lines.get(this.renderRow)).length() - 1) {
	        if (this.renderRow >= this.lines.size() - 1) {
	          this.finished = true;
	          this.pageBlinkTime = MESSAGE_PAGE_BLINK_TIME;
	        } else {
	          this.renderRow += 1;
	          this.renderCol = 0;
	        }
	      }
	      else this.renderCol += 1;
	    }
	  }
	  

	  public void nextIndex()
	  {
	    setIndex(++this.messageIndex);
	  }

	  public void setIndex(int index)
	  {
	    this.messageIndex = index;
	  }

	  protected final void postSetIndex()
	  {
	    if (this.messageList == null) return;

	    String str = ((Message)this.messageList.get(this.messageIndex)).getFace();

	    if (str.equals("null")) {
	     // setFaceImage(null);
	    } else {
	      String chara = str.substring(0, 3);
	      String no = str.substring(3, 4);

	      int index = Integer.parseInt(no, 16);

	   //   ForceGateImage img = this.main.getFaceImages(chara)[index];
	   //   this.imgFace = img;
	   //   setFaceImage(this.imgFace);
	    }

	    restart();

	    pauseMessage();
	  }

	  protected void drawMessage()
	  {
	    StringBuilder message = new StringBuilder();

	    if (!this.lines.isEmpty()) {
	      for (int i = 0; i < this.renderRow + 1; i++) {
	        String line = (String)this.lines.get(i);

	        int len = 0;

	        if (i < this.renderRow)
	          len = line.length();
	        else {
	          len = this.renderCol;
	        }

	        String t = line.substring(0, len);
	        if (t.length() != 0) {
	          if (len == line.length()) message.append(t + "\n"); else {
	            message.append(t);
	          }
	        }
	      }
	    }

	    if ((this.finished) && (!this.noPaged)) {
	      if (this.pageBlinkTime > MESSAGE_PAGE_BLINK_TIME) {
	        this.pageBlinkTime = 0;
	        this.isPaged = (!this.isPaged);
	      }
	    }
	    else {
	      this.isPaged = false;
	    }

	 //   this.box.draw(message.toString(), this.isPaged);
	  }
	  

	  protected void restart()
	  {
	    this.renderCol = 0;
	    this.renderRow = 0;
	    this.typeDelayTime = MESSAGE_TYPE_INTERVAL;
	    this.pageBlinkTime = 0;
	    this.finished = false;
	    String message = null;
	    if ((this.messageList != null) && (this.messageList.size() > 0)) {
	      message = ((Message)this.messageList.get(this.messageIndex)).getMessage();
	    }
//	    this.lines = wrapMessage(message, this.font, this.box.getMessageWidth());
	  }

	  protected void showAll()
	  {
	    if (this.lines.isEmpty()) { this.renderRow = (this.renderCol = 0);
	    } else {
	      this.renderRow = (this.lines.size() - 1);
	      this.renderCol = ((String)this.lines.get(this.renderRow)).length();
	      this.finished = true;
	    }
	  }

	  
	  public void pauseMessage()
	  {
	    this.stopMessage = true;
	  }

	  public void resumeMessage()
	  {
	    this.stopMessage = false;
	  }
}
