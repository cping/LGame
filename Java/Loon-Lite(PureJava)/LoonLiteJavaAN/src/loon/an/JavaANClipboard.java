package loon.an;

import loon.Clipboard;
import loon.LSystem;

public class JavaANClipboard extends Clipboard {

    private android.content.ClipboardManager clipboard;

    private android.content.Context context;

    public JavaANClipboard(JavaANGame game) {
        context = game.mainPlatform.getContext();
        clipboard = (android.content.ClipboardManager) context
                .getSystemService(android.content.Context.CLIPBOARD_SERVICE);
    }

    @Override
    public String getContent() {
        android.content.ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder(128);
        for (int i = 0; i < clip.getItemCount(); i++) {
            if (buffer.length() > 0) {
                buffer.append(LSystem.LF);
            }
            buffer.append(clip.getItemAt(i).getText());
        }
        return buffer.toString();
    }

    @Override
    public void setContent(final String content) {
        android.content.ClipData data = android.content.ClipData.newPlainText(content, content);
        clipboard.setPrimaryClip(data);
    }

}
