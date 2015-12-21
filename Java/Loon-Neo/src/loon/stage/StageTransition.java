package loon.stage;

public abstract class StageTransition {

    public static enum Dir { UP, DOWN, LEFT, RIGHT; }

    public void init (Stage o, Stage n) {}

    public abstract boolean update (Stage o, Stage n, float elapsed);

    public void complete (Stage o, Stage n) {}
}
