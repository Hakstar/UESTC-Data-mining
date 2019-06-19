package model;

public class Point {
    private double x;
    private double y;
    /**
     * 是否已经被遍历
     */
    private boolean isVisited;
    /**
     * 是否核心点
     */
    private boolean isCore;
    /**
     * 是否噪音点
     */
    private boolean isNoise;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        isVisited = false;
        isCore = false;
        isNoise=false;
    }

    public Point(double x, double y, boolean isVisited, boolean isCore) {
        this.x = x;
        this.y = y;
        this.isVisited = isVisited;
        this.isCore = isCore;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setIsVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public boolean isCore() {
        return isCore;
    }

    public void setIsCore(boolean isCore) {
        this.isCore = isCore;
    }

    public boolean isNoise() {
        return isNoise;
    }

    public void setIsNoise(boolean isNoise) {
        this.isNoise = isNoise;
    }

}
