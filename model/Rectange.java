package model;


public class Rectange {

    private double left;
    private double right;
    private double top;
    private double bottom;

    private Point centerPoint;

    public Rectange(Point center, double r) {
        left = center.getX() - r;
        right = center.getX() + r;
        top = center.getY() + r;
        bottom = center.getY() - r;

        centerPoint = center;
    }
    public Rectange(double left, double right, double top, double bottom, Point centerPoint) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.centerPoint = centerPoint;
    }

    public boolean containPoint(Point point) {
        if (point.getX() < left) return false;
        if (point.getX() > right) return false;
        if (point.getY() > top) return false;
        if (point.getY() < bottom) return false;
        return true;
    }


}
