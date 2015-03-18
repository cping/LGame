/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;

import java.awt.*;
import java.lang.reflect.*;
import java.nio.DoubleBuffer;
import java.text.*;
import java.util.regex.Pattern;

/**
 * @author tag
 * @version $Id: WWUtil.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class WWUtil
{
    /**
     * Converts a specified string to an integer value. Returns null if the string cannot be converted.
     *
     * @param s the string to convert.
     *
     * @return integer value of the string, or null if the string cannot be converted.
     *
     * @throws IllegalArgumentException if the string is null.
     */
    public static Integer convertStringToInteger(String s)
    {
        if (s == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            if (s.length() == 0)
            {
                return null;
            }

            return Integer.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", s);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }
    }

    /**
     * Converts a specified string to a floating point value. Returns null if the string cannot be converted.
     *
     * @param s the string to convert.
     *
     * @return floating point value of the string, or null if the string cannot be converted.
     *
     * @throws IllegalArgumentException if the string is null.
     */
    public static Double convertStringToDouble(String s)
    {
        if (s == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            if (s.length() == 0)
            {
                return null;
            }

            return Double.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", s);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }
    }

    /**
     * Converts a specified string to a long integer value. Returns null if the string cannot be converted.
     *
     * @param s the string to convert.
     *
     * @return long integer value of the string, or null if the string cannot be converted.
     *
     * @throws IllegalArgumentException if the string is null.
     */
    public static Long convertStringToLong(String s)
    {
        if (s == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            if (s.length() == 0)
            {
                return null;
            }

            return Long.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", s);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }
    }

    /**
     * Converts a specified string to a boolean value. Returns null if the string cannot be converted.
     *
     * @param s the string to convert.
     *
     * @return boolean value of the string, or null if the string cannot be converted.
     *
     * @throws IllegalArgumentException if the string is null.
     */
    public static Boolean convertStringToBoolean(String s)
    {
        if (s == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            s = s.trim();

            if (s.length() == 0)
                return null;

            if (s.length() == 1)
                return convertNumericStringToBoolean(s);

            return Boolean.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", s);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }
    }

    /**
     * Converts a specified string to a boolean value. Returns null if the string cannot be converted.
     *
     * @param s the string to convert.
     *
     * @return boolean value of the string, or null if the string cannot be converted.
     *
     * @throws IllegalArgumentException if the string is null.
     */
    public static Boolean convertNumericStringToBoolean(String s)
    {
        if (s == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            if (s.length() == 0)
            {
                return null;
            }

            Integer i = makeInteger(s);
            return i != null && i != 0;
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", s);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }
    }

    /**
     * Parses a string to an integer value if the string can be parsed as a integer. Does not log a message if the
     * string can not be parsed as an integer.
     *
     * @param s the string to parse.
     *
     * @return the integer value parsed from the string, or null if the string cannot be parsed as an integer.
     */
    public static Integer makeInteger(String s)
    {
        if (WWUtil.isEmpty(s))
        {
            return null;
        }

        try
        {
            return Integer.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Parses a string to a long value if the string can be parsed as a long. Does not log a message if the string can
     * not be parsed as a long.
     *
     * @param s the string to parse.
     *
     * @return the long value parsed from the string, or null if the string cannot be parsed as a long.
     */
    public static Long makeLong(String s)
    {
        if (WWUtil.isEmpty(s))
        {
            return null;
        }

        try
        {
            return Long.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Parses a string to a double value using the current locale if the string can be parsed as a double. Does not log
     * a message if the string can not be parsed as a double.
     *
     * @param s the string to parse.
     *
     * @return the double value parsed from the string, or null if the string cannot be parsed as a double.
     */
    public static Double makeDoubleForLocale(String s)
    {
        if (WWUtil.isEmpty(s))
        {
            return null;
        }

        try
        {
            return NumberFormat.getInstance().parse(s.trim()).doubleValue();
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * Parses a string to a double value if the string can be parsed as a double. Does not log a message if the string
     * can not be parsed as a double.
     *
     * @param s the string to parse.
     *
     * @return the double value parsed from the string, or null if the string cannot be parsed as a double.
     */
    public static Double makeDouble(String s)
    {
        if (WWUtil.isEmpty(s))
        {
            return null;
        }

        try
        {
            return Double.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Returns a sub sequence of the specified {@link CharSequence}, with leading and trailing whitespace omitted. If
     * the CharSequence has length zero, this returns a reference to the CharSequence. If the CharSequence represents
     * and empty character sequence, this returns an empty CharSequence.
     *
     * @param charSequence the CharSequence to trim.
     *
     * @return a sub sequence with leading and trailing whitespace omitted.
     *
     * @throws IllegalArgumentException if the charSequence is null.
     */
    public static CharSequence trimCharSequence(CharSequence charSequence)
    {
        if (charSequence == null)
        {
            String message = Logging.getMessage("nullValue.CharSequenceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int len = charSequence.length();
        if (len == 0)
        {
            return charSequence;
        }

        int start, end;

        for (start = 0; (start < len) && charSequence.charAt(start) == ' '; start++)
        {
        }

        for (end = charSequence.length() - 1; (end > start) && charSequence.charAt(end) == ' '; end--)
        {
        }

        return charSequence.subSequence(start, end + 1);
    }

    public static void alignComponent(Component parent, Component child, String alignment)
    {
        Dimension prefSize = child.getPreferredSize();
        java.awt.Point parentLocation = parent != null ? parent.getLocation() : new java.awt.Point(0, 0);
        Dimension parentSize = parent != null ? parent.getSize() : Toolkit.getDefaultToolkit().getScreenSize();

        int x = parentLocation.x;
        int y = parentLocation.y;

        if (alignment != null && alignment.equals(AVKey.RIGHT))
        {
            x += parentSize.width - 50;
            y += parentSize.height - prefSize.height;
        }
        else if (alignment != null && alignment.equals(AVKey.CENTER))
        {
            x += (parentSize.width - prefSize.width) / 2;
            y += (parentSize.height - prefSize.height) / 2;
        }
        else if (alignment != null && alignment.equals(AVKey.LEFT_OF_CENTER))
        {
            x += parentSize.width / 2 - 1.05 * prefSize.width;
            y += (parentSize.height - prefSize.height) / 2;
        }
        else if (alignment != null && alignment.equals(AVKey.RIGHT_OF_CENTER))
        {
            x += parentSize.width / 2 + 0.05 * prefSize.width;
            y += (parentSize.height - prefSize.height) / 2;
        }
        // else it's left aligned by default

        child.setLocation(x, y);
    }

    /**
     * Generates a random {@link Color} by scaling each of the red, green and blue components of a specified color with
     * independent random numbers. The alpha component is not scaled and is copied to the new color. The returned color
     * can be any value between white (0x000000aa) and black (0xffffffaa).
     * <p/>
     * Unless there's a reason to use a specific input color, the best color to use is white.
     *
     * @param color the color to generate a random color from. If null, the color white (0x000000aa) is used.
     *
     * @return a new color with random red, green and blue components.
     */
    public static Color makeRandomColor(Color color)
    {
        if (color == null)
        {
            color = Color.WHITE;
        }

        float[] cc = color.getRGBComponents(null);

        return new Color(cc[0] * (float) Math.random(), cc[1] * (float) Math.random(), cc[2] * (float) Math.random(),
            cc[3]);
    }

    /**
     * Generates a random {@link Color} by scaling each of the red, green and blue components of a specified color with
     * independent random numbers. The alpha component is not scaled and is copied to the new color. The returned color
     * can be any value between white (0x000000aa) and a specified darkest color.
     * <p/>
     * Unless there's a reason to use a specific input color, the best color to use is white.
     *
     * @param color        the color to generate a random color from. If null, the color white (0x000000aa) is used.
     * @param darkestColor the darkest color allowed. If any of the generated color's components are less than the
     *                     corresponding component in this color, new colors are generated until one satisfies this
     *                     requirement, up to the specified maximum number of attempts.
     * @param maxAttempts  the maximum number of attempts to create a color lighter than the specified darkestColor. If
     *                     this limit is reached, the last color generated is returned.
     *
     * @return a new color with random red, green and blue components.
     */
    public static Color makeRandomColor(Color color, Color darkestColor, int maxAttempts)
    {
        Color randomColor = makeRandomColor(color);

        if (darkestColor == null)
        {
            return randomColor;
        }

        float[] dc = darkestColor.getRGBComponents(null);

        float[] rc = randomColor.getRGBComponents(null);
        for (int i = 0; i < (maxAttempts - 1) && (rc[0] < dc[0] || rc[1] < dc[1] || rc[2] < dc[2]); i++)
        {
            rc = randomColor.getRGBComponents(null);
        }

        return randomColor;
    }

    public static Color makeColorBrighter(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        float[] hsbComponents = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComponents);
        float hue = hsbComponents[0];
        float saturation = hsbComponents[1];
        float brightness = hsbComponents[2];

        saturation /= 3f;
        brightness *= 3f;

        if (saturation < 0f)
        {
            saturation = 0f;
        }

        if (brightness > 1f)
        {
            brightness = 1f;
        }

        int rgbInt = Color.HSBtoRGB(hue, saturation, brightness);

        return new Color(rgbInt);
    }

    public static Color makeColorDarker(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        float[] hsbComponents = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComponents);
        float hue = hsbComponents[0];
        float saturation = hsbComponents[1];
        float brightness = hsbComponents[2];

        saturation *= 3f;
        brightness /= 3f;

        if (saturation > 1f)
        {
            saturation = 1f;
        }

        if (brightness < 0f)
        {
            brightness = 0f;
        }

        int rgbInt = Color.HSBtoRGB(hue, saturation, brightness);

        return new Color(rgbInt);
    }

    public static Color computeContrastingColor(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        float[] compArray = new float[4];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), compArray);
        int colorValue = compArray[2] < 0.5f ? 255 : 0;
        int alphaValue = color.getAlpha();

        return new Color(colorValue, colorValue, colorValue, alphaValue);
    }

    /**
     * Returns the component-wise linear interpolation of <code>color1</code> and <code>color2</code>. Each of the RGBA
     * components in the colors are interpolated according to the function: <code>(1 - amount) * c1 + amount *
     * c2</code>, where c1 and c2 are components of <code>color1</code> and <code>color2</code>, respectively. The
     * interpolation factor <code>amount</code> defines the weight given to each value, and is clamped to the range [0,
     * 1].
     *
     * @param amount the interpolation factor.
     * @param color1 the first color.
     * @param color2 the second color.
     *
     * @return this returns the linear interpolation of <code>color1</code> and <code>color2</code> if <amount> is
     *         between 0 and 1, a color equivalent to color1 if <code>amount</code> is 0 or less, or a color equivalent
     *         to <code>color2</code> if <code>amount</code> is 1 or more.
     *
     * @throws IllegalArgumentException if either <code>color1</code> or <code>color2</code> are <code>null</code>.
     */
    public static Color interpolateColor(double amount, Color color1, Color color2)
    {
        if (color1 == null || color2 == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        float t = (amount < 0 ? 0 : (amount > 1 ? 1 : (float) amount));
        float r = color1.getRed() + t * (color2.getRed() - color1.getRed());
        float g = color1.getGreen() + t * (color2.getGreen() - color1.getGreen());
        float b = color1.getBlue() + t * (color2.getBlue() - color1.getBlue());
        float a = color1.getAlpha() + t * (color2.getAlpha() - color1.getAlpha());

        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    /**
     * Creates a hexadecimal string representation of a {@link Color} in the form 0xrrggbbaa.
     *
     * @param color Color to encode.
     *
     * @return String encoding of the specified color.
     *
     * @throws IllegalArgumentException If the specified color is null.
     * @see #decodeColorRGBA(String)
     * @see #encodeColorABGR(java.awt.Color)
     */
    public static String encodeColorRGBA(java.awt.Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Encode the red, green, blue, and alpha components
        int rgba = (color.getRed() & 0xFF) << 24
            | (color.getGreen() & 0xFF) << 16
            | (color.getBlue() & 0xFF) << 8
            | (color.getAlpha() & 0xFF);
        return String.format("%#08X", rgba);
    }

    /**
     * Creates a hexadecimal string representation of a {@link Color} in the form 0xaabbggrr.
     *
     * @param color Color to encode.
     *
     * @return String encoding of the specified color.
     *
     * @throws IllegalArgumentException If the specified color is null.
     * @see #decodeColorABGR(String)
     * @see #encodeColorRGBA(java.awt.Color)
     */
    public static String encodeColorABGR(java.awt.Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Encode the red, green, blue, and alpha components
        int rgba = (color.getRed() & 0xFF)
            | (color.getGreen() & 0xFF) << 8
            | (color.getBlue() & 0xFF) << 16
            | (color.getAlpha() & 0xFF) << 24;
        return String.format("%#08X", rgba);
    }

    /**
     * Decodes a hexadecimal string in the form <i>rrggbbaa</i>, <i>rrggbbaa</i> or <i>#rrggbbaa</i> to a color.
     *
     * @param encodedString String to decode.
     *
     * @return the decoded color, or null if the string cannot be decoded.
     *
     * @throws IllegalArgumentException If the specified string is null.
     * @see #decodeColorABGR(String) (String)
     * @see #encodeColorRGBA(java.awt.Color)
     */
    public static java.awt.Color decodeColorRGBA(String encodedString)
    {
        if (encodedString == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (encodedString.startsWith("#"))
        {
            encodedString = encodedString.replaceFirst("#", "0x");
        }
        else if (!encodedString.startsWith("0x") && !encodedString.startsWith("0X"))
        {
            encodedString = "0x" + encodedString;
        }

        // The hexadecimal representation for an RGBA color can result in a value larger than
        // Integer.MAX_VALUE (for example, 0XFFFF). Therefore we decode the string as a long,
        // then keep only the lower four bytes.
        Long longValue;
        try
        {
            longValue = Long.parseLong(encodedString.substring(2), 16);
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", encodedString);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }

        int i = (int) (longValue & 0xFFFFFFFFL);
        return new java.awt.Color(
            (i >> 24) & 0xFF,
            (i >> 16) & 0xFF,
            (i >> 8) & 0xFF,
            i & 0xFF);
    }

    /**
     * Decodes a hexadecimal string in the form <i>aabbggrr</i>, <i>0xaabbggrr</i> or <i>#aabbggrr</i> to a color.
     *
     * @param encodedString String to decode.
     *
     * @return the decoded color, or null if the string cannot be decoded.
     *
     * @throws IllegalArgumentException If the specified string is null.
     * @see #decodeColorRGBA(String)
     * @see #encodeColorABGR(java.awt.Color)
     */
    public static java.awt.Color decodeColorABGR(String encodedString)
    {
        if (encodedString == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (encodedString.startsWith("#"))
        {
            encodedString = encodedString.replaceFirst("#", "0x");
        }
        else if (!encodedString.startsWith("0x") && !encodedString.startsWith("0X"))
        {
            encodedString = "0x" + encodedString;
        }

        // The hexadecimal representation for an RGBA color can result in a value larger than
        // Integer.MAX_VALUE (for example, 0XFFFF). Therefore we decode the string as a long,
        // then keep only the lower four bytes.
        Long longValue;
        try
        {
            longValue = Long.parseLong(encodedString.substring(2), 16);
        }
        catch (NumberFormatException e)
        {
            String message = Logging.getMessage("generic.ConversionError", encodedString);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return null;
        }

        int i = (int) (longValue & 0xFFFFFFFFL);
        return new java.awt.Color(
            i & 0xFF,
            (i >> 8) & 0xFF,
            (i >> 16) & 0xFF,
            (i >> 24) & 0xFF);
    }

    /**
     * Determine whether an object reference is null or a reference to an empty string.
     *
     * @param s the reference to examine.
     *
     * @return true if the reference is null or is a zero-length {@link String}.
     */
    public static boolean isEmpty(Object s)
    {
        return s == null || (s instanceof String && ((String) s).length() == 0);
    }

    /**
     * Determine whether an {@link List} is null or empty.
     *
     * @param list the list to examine.
     *
     * @return true if the list is null or zero-length.
     */
    public static boolean isEmpty(java.util.List<?> list)
    {
        return list == null || list.size() == 0;
    }

    /**
     * Creates a two-element array of default min and max values, typically used to initialize extreme values searches.
     *
     * @return a two-element array of extreme values. Entry 0 is the maximum double value; entry 1 is the negative of
     *         the maximum double value;
     */
    public static double[] defaultMinMix()
    {
        return new double[] {Double.MAX_VALUE, -Double.MAX_VALUE};
    }

    /**
     * Normalizes the specified buffer of geographic tuples. The buffer must be organized as pairs of tightly packed
     * geographic tuples in the order <code>(longitude, latitude)</code>. Each geographic tuple is normalized to the
     * range +-90 latitude and +-180 longitude and replaced with its normalized values. Geographic locations are
     * expressed in degrees. Tuples are replaced starting at the buffer's position and ending at its limit.
     *
     * @param buffer the buffer of geographic tuples to convert.
     *
     * @throws IllegalArgumentException if <code>buffer</code> is null, or if the number of remaining elements in
     *                                  <code>buffer</code> is not a multiple of two.
     */
    public static void normalizeGeographicCoordinates(DoubleBuffer buffer)
    {
        if (buffer == null)
        {
            String message = Logging.getMessage("nullValue.BufferIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if ((buffer.remaining() % 2) != 0)
        {
            String message = Logging.getMessage("generic.BufferSize", buffer.remaining());
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        while (buffer.hasRemaining())
        {
            buffer.mark();
            Angle lon = Angle.fromDegrees(buffer.get());
            Angle lat = Angle.fromDegrees(buffer.get());

            buffer.reset();
            buffer.put(Angle.normalizedLongitude(lon).degrees);
            buffer.put(Angle.normalizedLatitude(lat).degrees);
        }
    }

    /**
     * Uses reflection to invoke a <i>set</i> method for a specified property. The specified class must have a method
     * named "set" + propertyName, with either a single <code>String</code> argument, a single <code>double</code>
     * argument, a single <code>int</code> argument or a single <code>long</code> argument. If it does, the method is
     * called with the specified property value argument.
     *
     * @param parent        the object on which to set the property.
     * @param propertyName  the name of the property.
     * @param propertyValue the value to give the property. Specify double, int and long values in a
     *                      <code>String</code>.
     *
     * @return the return value of the <i>set</i> method, or null if the method has no return value.
     *
     * @throws IllegalArgumentException  if the parent object or the property name is null.
     * @throws NoSuchMethodException     if no <i>set</i> method exists for the property name.
     * @throws InvocationTargetException if the <i>set</i> method throws an exception.
     * @throws IllegalAccessException    if the <i>set</i> method is inaccessible due to access control.
     */
    public static Object invokePropertyMethod(Object parent, String propertyName, String propertyValue)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        if (parent == null)
        {
            String message = Logging.getMessage("nullValue.nullValue.ParentIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (propertyName == null)
        {
            String message = Logging.getMessage("nullValue.PropertyNameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String methodName = "set" + propertyName;

        try // String arg
        {
            Method method = parent.getClass().getMethod(methodName, new Class[] {String.class});
            return method != null ? method.invoke(parent, propertyValue) : null;
        }
        catch (NoSuchMethodException e)
        {
            // skip to next arg type
        }

        try // double arg
        {
            Double d = WWUtil.makeDouble(propertyValue);
            if (d != null)
            {
                Method method = parent.getClass().getMethod(methodName, new Class[] {double.class});
                return method != null ? method.invoke(parent, d) : null;
            }
        }
        catch (NoSuchMethodException e)
        {
            // skip to next arg type
        }

        try // int arg
        {
            Integer i = WWUtil.makeInteger(propertyValue);
            if (i != null)
            {
                Method method = parent.getClass().getMethod(methodName, new Class[] {int.class});
                return method != null ? method.invoke(parent, i) : null;
            }
        }
        catch (NoSuchMethodException e)
        {
            // skip to next arg type
        }

        try // boolean arg
        {
            Boolean b = WWUtil.convertStringToBoolean(propertyValue);
            if (b != null)
            {
                Method method = parent.getClass().getMethod(methodName, new Class[] {boolean.class});
                return method != null ? method.invoke(parent, b) : null;
            }
        }
        catch (NoSuchMethodException e)
        {
            // skip to next arg type
        }

        try // long arg
        {
            Long l = WWUtil.makeLong(propertyValue);
            if (l != null)
            {
                Method method = parent.getClass().getMethod(methodName, new Class[] {long.class});
                return method != null ? method.invoke(parent, l) : null;
            }
        }
        catch (NoSuchMethodException e)
        {
            // skip to next arg type
        }

        throw new NoSuchMethodException();
    }

    /**
     * Copies only values of the specified <code>keys</code >from <code>srcList</code> to another <code>destList</code>.
     * The <code>forceOverwrite</code> controls what to do if the destination list already contains values for specified
     * <code>keys</code >. If  <code>forceOverwrite</code> is set to true, the existing value wills be overwritten.
     *
     * @param srcList        The source list. May not be <code>null</code>.
     * @param destList       The destination list. May not be <code>null</code>.
     * @param forceOverwrite Allow overwrite existing values in the destination list
     * @param keys           Array of <code>keys</code >
     */
    public static void copyValues(AVList srcList, AVList destList, String[] keys, boolean forceOverwrite)
    {
        if (WWUtil.isEmpty(srcList) || WWUtil.isEmpty(destList) || WWUtil.isEmpty(keys) || keys.length == 0)
        {
            return;
        }

        for (String key : keys)
        {
            if (WWUtil.isEmpty(key) || !srcList.hasKey(key))
            {
                continue;
            }

            Object o = srcList.getValue(key);
            if (!destList.hasKey(key) || forceOverwrite)
            {
                destList.setValue(key, o);
            }
        }
    }

    /**
     * Eliminates all white space in a specified string. (Applies the regular expression "\\s+".)
     *
     * @param inputString the string to remove white space from.
     *
     * @return the string with white space eliminated, or null if the input string is null.
     */
    public static String removeWhiteSpace(String inputString)
    {
        if (WWUtil.isEmpty(inputString))
        {
            return inputString;
        }

        return inputString.replaceAll("\\s+", "");
    }

    /**
     * Extracts an error message from the exception object
     *
     * @param t Exception instance
     *
     * @return A string that contains an error message
     */
    public static String extractExceptionReason(Throwable t)
    {
        if (t == null)
        {
            return Logging.getMessage("generic.Unknown");
        }

        StringBuilder sb = new StringBuilder();

        String message = t.getMessage();
        if (!WWUtil.isEmpty(message))
            sb.append(message);

        String messageClass = t.getClass().getName();

        Throwable cause = t.getCause();
        if (null != cause && cause != t)
        {
            String causeMessage = cause.getMessage();
            String causeClass = cause.getClass().getName();

            if (!WWUtil.isEmpty(messageClass) && !WWUtil.isEmpty(causeClass) && !messageClass.equals(causeClass))
            {
                if (sb.length() != 0)
                {
                    sb.append(" : ");
                }
                sb.append(causeClass).append(" (").append(causeMessage).append(")");
            }
        }

        if (sb.length() == 0)
        {
            sb.append(messageClass);
        }

        return sb.toString();
    }

    /**
     * Strips leading period from a string (Example: input -> ".ext", output -> "ext")
     *
     * @param s String to test, must not be null
     *
     * @return String without leading period
     */
    public static String stripLeadingPeriod(String s)
    {
        if (null != s && s.startsWith("."))
            return s.substring(Math.min(1, s.length()), s.length());
        return s;
    }

    protected static boolean isKMLTimeShift(String timeString)
    {
        return Pattern.matches(".*[+-]+\\d\\d:\\d\\d$", timeString.trim());
    }

    /**
     * Parse a date/time string and return its equivalent in milliseconds (using same time coordinate system as
     * System.currentTimeMillis()). The following formats are recognized and conform to those defined in KML version
     * 2.2: "1997", "1997-07", "1997-07-16", "1997-07-16T07:30:15Z", "1997-07-16T07:30:15+03:00" and
     * "1997-07-16T07:30:15+0300".
     *
     * @param timeString the date/time string to parse.
     *
     * @return the number of milliseconds since 00:00:00 1970 indicated by the date/time string, or null if the input
     *         string is null or the string is not a recognizable format.
     */
    public static Long parseTimeString(String timeString)
    {
        if (timeString == null)
            return null;

        // KML allows a hybrid time zone offset that does not contain the leading "GMT", e.g. 1997-05-10T09:30:00+03:00.
        // If the time string has this pattern, we convert it to an RFC 822 time zone so that SimpleDateFormat can
        // parse it.
        if (isKMLTimeShift(timeString))
        {
            // Remove the colon from the GMT offset portion of the time string.
            timeString = timeString.trim();
            int colonPosition = timeString.length() - 3;
            String newTimeString = timeString.substring(0, colonPosition);
            timeString = newTimeString + timeString.substring(colonPosition + 1, timeString.length());
        }

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzzzz");
            return df.parse(timeString).getTime();
        }
        catch (ParseException ignored)
        {
        }

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return df.parse(timeString).getTime();
        }
        catch (ParseException ignored)
        {
        }

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(timeString).getTime();
        }
        catch (ParseException ignored)
        {
        }

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM");
            return df.parse(timeString).getTime();
        }
        catch (ParseException ignored)
        {
        }

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy");
            return df.parse(timeString).getTime();
        }
        catch (ParseException ignored)
        {
        }

        return null;
    }
}
