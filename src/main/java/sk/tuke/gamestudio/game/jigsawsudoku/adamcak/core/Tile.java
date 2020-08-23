package sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core;

public class Tile {
    private boolean state;
    private int value;
    private Color defaultColor;
    private Color usableColor;
    public Tile(){
        this.value = 0;
        this.state = false;
        this.usableColor = Color.NONE;
        this.defaultColor = Color.NONE;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
///
    public void setUsableColor(Color usableColor) {
        this.usableColor = usableColor;
    }

    public Color getUsableColor() {
        return usableColor;
    }

    public Color getDefaultColor() { return defaultColor;}

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }
}
