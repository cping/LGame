package  loon.jni;

import java.nio.ByteBuffer;

import com.google.gwt.typedarrays.shared.ArrayBuffer;

/**
 * Allows us to wrap an existing typed array buffer in a ByteBuffer.
 */
public class TypedArrayHelper {
  private static Wrapper wrapper = (Wrapper) ByteBuffer.allocate(1);

  public static ByteBuffer wrap(ArrayBuffer ab) {
    return wrapper.wrap(ab);
  }
  
  /**
   * Implemented by ByteBuffer in GWT modes. 
   */
  public interface Wrapper {
    ByteBuffer wrap(ArrayBuffer arrayBuffer);
  }
}
