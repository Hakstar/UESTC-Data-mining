package model;

import java.util.LinkedList;
import java.util.List;


public class Cluster {
    private String identify;
    private List<Point> members;
    private Point center;

    public Cluster(String identify, Point center) {
        this.identify = identify;
        members = new LinkedList<Point>();
        this.center = center;
    }
    public List<Point> getMembers() {
        return members;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void addMemberNotExists(Point point) {
        if (point != null && !members.contains(point))
            this.members.add(point);
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }
}
