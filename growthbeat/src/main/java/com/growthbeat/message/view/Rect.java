package com.growthbeat.message.view;

public class Rect {

    private int left;
    private int top;
    private int width;
    private int height;

    public Rect() {
        super();
    }

    public Rect(int left, int top, int width, int height) {
        this();
        setLeft(left);
        setTop(top);
        setWidth(width);
        setHeight(height);
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
