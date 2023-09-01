package loon.an;

import loon.Asyn;
import loon.LSystem;
import loon.Log;
import loon.utils.reply.Act;

public class JavaANAsyn extends Asyn.Default {

    private final JavaANGame game;

    public JavaANAsyn(Log log, Act<? extends Object> frame, JavaANGame game) {
        super(log, frame);
        this.game = game;
    }

    protected boolean isPaused() {
        return LSystem.PAUSED;
    }

    @Override
    public void invokeLater(Runnable action) {
        if (isPaused()) {
            game.mainPlatform.runOnUI(action);
        } else {
            super.invokeLater(action);
        }
    }

    @Override
    public boolean isAsyncSupported() {
        return true;
    }

    @Override
    public void invokeAsync(final Runnable action) {
        game.mainPlatform.runOnUI(new Runnable() {
            public void run() {
                   action.run();
            }
        });
    }
}
