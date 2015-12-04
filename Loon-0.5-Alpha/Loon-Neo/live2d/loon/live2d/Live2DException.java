package loon.live2d;

public class Live2DException extends RuntimeException
{
    static final long serialVersionUID = -1L;
    String a;
    
    public Live2DException() {
    }
    
    public Live2DException(final Exception e) {
        super(e);
    }
    
    public Live2DException(final Exception e, final String live2d_message) {
        super(e);
        this.a = live2d_message;
    }
    
    public Live2DException(final String live2d_message) {
        this.a = live2d_message;
    }
    
    @Override
    public String toString() {
        return String.valueOf(super.toString()) + " / " + this.a;
    }
}
