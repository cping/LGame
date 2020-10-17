package org.test;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.TextHitInfo;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;

import javax.swing.JFrame;
import javax.swing.JTextField;


public class MyText  extends JTextField{
    MyInputMethodRequests myRequest = new MyInputMethodRequests();
    
    public InputMethodRequests getInputMethodRequests(){
    	
        InputMethodRequests request = super.getInputMethodRequests();
        myRequest.setInputMethodRequests(request);
        
        return myRequest;
    }
    
    public class MyInputMethodRequests implements InputMethodRequests{
        private InputMethodRequests defaultRequest;

        public void setInputMethodRequests(InputMethodRequests defaultRequest){
            this.defaultRequest = defaultRequest;
        }
        
        public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes){
            return defaultRequest.cancelLatestCommittedText(attributes);
        }
        
        public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes){
            return defaultRequest.getCommittedText(beginIndex, endIndex, attributes);
        }
        
        public int getCommittedTextLength(){
            return defaultRequest.getCommittedTextLength();
        }
        
        public int getInsertPositionOffset(){
            return defaultRequest.getInsertPositionOffset();
        }
        
        public TextHitInfo getLocationOffset(int x, int y){
            return defaultRequest.getLocationOffset(x, y);
        }
        
        public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes){
            return defaultRequest.getSelectedText(attributes);
        }
        
        public Rectangle getTextLocation(TextHitInfo offset){
            Rectangle rect = defaultRequest.getTextLocation(offset);
            
            rect.setLocation(getX(), getY());
            
            return rect;
        }
    }
    
    public static void main(String[] args){
    	 /* InputContext
           
        JFrame frame = new JFrame();
        MyText field = new MyText();
        field.setPreferredSize(new Dimension(240, 24));
        
        frame.add(field);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);*/
    
}
}
