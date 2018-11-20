package com.sagra.stylemaker_v1.data;
public class Cameraspinner {
    private String text = "";
    private int Pic = -1;

    public Cameraspinner(String text, int pic) {
        this.text = text;
        this.Pic = pic;

    }

    public String getText() {
        return text;
    }
    public int getPic() {
        return Pic;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setPic(int pic) {
        this.Pic = pic;
    }
    @Override
    public String toString() {
        return text;    }
}
