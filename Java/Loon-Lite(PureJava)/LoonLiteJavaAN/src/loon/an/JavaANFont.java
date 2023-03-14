package loon.an;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import loon.font.Font;
import loon.utils.StringUtils;

public class JavaANFont {

    protected static final Map<Font.Style, Integer> TO_ANDROID_STYLE = new EnumMap<Font.Style, Integer>(
            Font.Style.class);

    static {
        TO_ANDROID_STYLE.put(Font.Style.PLAIN, Typeface.NORMAL);
        TO_ANDROID_STYLE.put(Font.Style.BOLD, Typeface.BOLD);
        TO_ANDROID_STYLE.put(Font.Style.ITALIC, Typeface.ITALIC);
        TO_ANDROID_STYLE.put(Font.Style.BOLD_ITALIC, Typeface.BOLD_ITALIC);
    }

    private static final String[] NO_HACKS = {};

    public static final JavaANFont DEFAULT = new JavaANFont(Typeface.DEFAULT, 14, null);

    public final Typeface typeface;
    public final float size;
    public final String[] resources;
    protected Paint paint;

    public JavaANFont(Typeface typeface, float size, String[] res) {
        this.typeface = typeface;
        this.size = size;
        this.resources = (res != null) ? res : NO_HACKS;
    }

    public static Typeface create(Font font) {
        String name = font.name;
        if (StringUtils.isEmpty(name)) {
            String familyName = name.toLowerCase();
            if (familyName.equals("serif") || familyName.equals("timesroman")) {
                name = "serif";
            } else if (familyName.equals("sansserif") || familyName.equals("helvetica")) {
                name = "sans-serif";
            } else if (familyName.equals("monospaced") || familyName.equals("courier") || familyName.equals("dialog")
                    || familyName.equals("黑体")) {
                name = "monospace";
            }
        } else {
            name = "monospace";
        }
        return Typeface.create(name, TO_ANDROID_STYLE.get(font.style));
    }

    public static Typeface load(Context context, String familyPath) {
        return Typeface.createFromAsset(context.getAssets(), familyPath);
    }

    public static Typeface load(Context context, File familyFile) {
        return Typeface.createFromFile(familyFile);
    }
}
