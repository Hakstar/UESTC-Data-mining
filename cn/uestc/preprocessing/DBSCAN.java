package cn.uestc.preprocessing;

import model.Cluster;
import model.Point;
import model.Rectange;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DBSCAN {
    private List<Point> dataSet;
    private double eps;
    private int minPts;

    public DBSCAN(int minPts, double eps) {
        this.minPts = minPts;
        this.eps = eps;
        dataSet=new LinkedList<Point>();
    }

    public static void main(String[] args) throws Exception {
        DBSCAN dbscan = new DBSCAN(4, 0.6);
        dbscan.init("D:\\data_minning\\dataminning-1\\src\\data\\DBSCANdata");
        List<Cluster> clusterList = dbscan.dBScan();
        outPutToConsole(clusterList);
    }

    public static void outPutToConsole(List<Cluster> clusterList) throws IOException {
        if (clusterList == null) return;

        for (Cluster cluster : clusterList) {
            System.out.println("Cluster: " + cluster.getIdentify() + " numbers of members: " + cluster.getMembers().size());
            for (Point point : cluster.getMembers()) {
                System.out.println("x: " + point.getX() + " ,  y: " + point.getY());
            }
            System.out.println("\n");

        }
    }
    public void init(String dataPath) throws IOException {
        if(dataPath==null)
            throw new RuntimeException("缺少测试数据文件！");
        File file = new File(dataPath);
        if (!file.exists()) {
            throw new RuntimeException("测试数据文件不存在！");
        }
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String row = bufferedReader.readLine();
        while (row!=null) {
            String[] rdata = row.split(",");
            dataSet.add(new Point(Double.parseDouble(rdata[1].trim()), Double.parseDouble(rdata[2].trim())));
            row = bufferedReader.readLine();
        }
    }

    public List<Cluster> dBScan() {
        int i = 0;
        List<Cluster> clusterList = new LinkedList<Cluster>();
        for (Point p : dataSet) {
            if (p.isVisited()) continue;
            //设置该点已经被访问
            p.setIsVisited(true);
            List<Point> neighborPts = new LinkedList<Point>();
            boolean isCorePoint = isCorePoint(p, neighborPts);
            if (!isCorePoint)
                //标记为噪音数据
                p.setIsNoise(true);
            else {
                //作为核心点，根据该点创建一个类别
                p.setIsCore(true);
                Cluster cluster = new Cluster("cluset@" + i++, p);
                clusterList.add(cluster);
                expandCluster(p, neighborPts, cluster);
            }
        }
        return clusterList;
    }

    private void expandCluster(Point corePoint, List<Point> neighborPts, Cluster cluster) {
        cluster.addMemberNotExists(corePoint);
        List<Point> nPts;
        Queue<Point> neighborPtQueue = new LinkedList<Point>(neighborPts);
        Point neighborPt=null;
        while (neighborPtQueue.size() > 0) {
            neighborPt = neighborPtQueue.poll();
            cluster.addMemberNotExists(neighborPt);
            //然后针对核心点邻域内的点，如果该点没有被访问
            if (!neighborPt.isVisited()) {
                nPts = new LinkedList<Point>();
                neighborPt.setIsVisited(true);
                boolean isCorePoint = isCorePoint(neighborPt, nPts);
                if (isCorePoint) {
                    neighborPt.setIsCore(true);
                    neighborPtQueue.addAll(nPts);
                }
            }
        }
    }

    private boolean isCorePoint(Point p, List<Point> neighborPts) {
        Rectange outRectange = new Rectange(p, eps);
        List<Point> rectNeighborPts = new LinkedList<Point>();
        //外接矩形内点
        for (Point point : dataSet) {
            if (outRectange.containPoint(point))
                rectNeighborPts.add(point);
        }
        if (rectNeighborPts.size() < minPts) return false;
        //内接水平矩形内的点
        //d表示内接矩形的长的一半
        double d = Math.sin(45.0) * eps;
        double left = p.getX() - d;
        double top = p.getY() + d;
        double right = p.getX() + d;
        double bottom = p.getY() - d;
        Rectange innerRectance = new Rectange(left, right, top, bottom, p);


        //在内接矩形内的点肯定也在圆内
        for (int i = 0; i < rectNeighborPts.size(); i++) {
            Point point = rectNeighborPts.get(i);
            if (innerRectance.containPoint(point)) {
                neighborPts.add(rectNeighborPts.remove(i));
                i--;
            }
        }
        //对于剩余的点再做距离判断
        for (Point point : rectNeighborPts) {
            double distance = Math.sqrt(Math.pow(point.getX() - p.getX(), 2) + Math.pow(point.getY() - p.getY(), 2));
            if (distance <= eps)
                neighborPts.add(point);
        }
        return neighborPts.size() > minPts;
    }

}
