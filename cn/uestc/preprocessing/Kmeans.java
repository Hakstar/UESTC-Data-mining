package cn.uestc.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class Kmeans {
    ArrayList<double[]>dataSet;
    int clusterNum;
    int dim;//data dimension
    Kmeans(int clusterNum){
        this.clusterNum=clusterNum;
        dataSet = new ArrayList<double[]>();
    }
    public static void main(String args[]){
        Kmeans kmean = new Kmeans(3);
        kmean.loadDataSet("D:\\data_minning\\dataminning-1\\src\\data\\data_cluster.txt");
        kmean.cluster();
    }

    private void cluster() {
        Random rand = new Random();
        double[][]clusterMeans = new double[clusterNum][dim];
        //random initalize mean of cluster
        for (int n = 0;n<clusterNum;n++){
            double[]data = new double[dim];
            for (int m = 0 ;m <dim;m++){
                data[m]=rand.nextDouble()*100;
            }
            clusterMeans[n]=data;
        }

        boolean isContinue = true;
        while (isContinue){
            isContinue=false;
            double[][]nextClusterMeans = new double[clusterNum][dim];
            int[]clusterDataNum = new int[clusterNum];
            //assign cluster mean to every data
            for (int n = 0;n<dataSet.size();n++){
                double mindis = Double.MAX_VALUE;
                int whoCluster = -1;
                for (int m = 0;m<clusterNum;m++){
                    double distance = this.getDist(clusterMeans[m],dataSet.get(n));
                    if (distance<mindis){
                        whoCluster = m;
                        mindis = distance;
                    }
                }
                clusterDataNum[whoCluster]++;
                for (int i =0;i<dim;i++){
                    nextClusterMeans[whoCluster][i]+=dataSet.get(n)[i];
                }
            }
            //update cluster means
            for (int i =0 ;i<clusterNum;i++){
                for (int j = 0;j<dim;j++){
                    if (clusterDataNum[i]!=0){
                        nextClusterMeans[i][j] /= clusterDataNum[i];
                    }else {
                        nextClusterMeans[i][j] = Math.random()*100;
                    }
                }
            }
            //if there is no big difference between nextClusterMeans and clusterMeans,stop looping
            for (int i =0;i<clusterNum;i++){
                if (this.getDist(nextClusterMeans[i],clusterMeans[i])!=0){
                    isContinue =true;
                }
            }
            clusterMeans = nextClusterMeans;


            //visualization
            ArrayList<ArrayList<double[]>> clusters = new ArrayList<ArrayList<double[]>>();
            for(int n=0;n<clusterNum;n++){
                clusters.add(new ArrayList<double[]>());
            }
            for (int n = 0; n < dataSet.size(); n++)
            {
                double minDis = Double.MAX_VALUE;
                int whoCluster = -1;
                for (int m = 0; m < clusterNum; m++)
                {
                    double distance = this.getDist(clusterMeans[m],
                            dataSet.get(n));
                    if (distance < minDis)
                    {
                        whoCluster = m;
                        minDis = distance;
                    }
                }
                clusters.get(whoCluster).add(dataSet.get(n));
            }
            double[][][] datas = new double[clusterNum][][];
            for(int n=0;n<clusterNum;n++){
                double[][] cluster = new double[clusters.get(n).size()][];
                for(int m=0;m<cluster.length;m++){
                    cluster[m] = clusters.get(n).get(m);
                }
                datas[n] = cluster;

            }
            System.out.println("cluster mean:");
            for(int n=0;n<clusterMeans.length;n++){
                for(double x : clusterMeans[n]){
                    System.out.print(x+" ");
                }
                System.out.println();
            }

            PicUtility.show(datas, clusterNum);

        }

    }

    private double getDist(double[] test, double[] data) {
        double sum = 0;
        for (int n = 0;n<test.length;n++){
            sum+=(test[n]-data[n])*(test[n]-data[n]);
        }
        return Math.sqrt(sum);
    }

    private void loadDataSet(String s) {
        File file = new File(s);
        FileReader fr;
        try {
            fr = new FileReader(file);
            BufferedReader bis = new BufferedReader(fr);
            String line = null;
            while ((line=bis.readLine())!=null){
                String[] str = line.trim().split(" ");
                double[]data = new double[str.length];
                for (int i =0 ;i <data.length;i++){
                    data[i]=Double.parseDouble(str[i]);
                }
                dataSet.add(data);
            }
            dim = dataSet.get(0).length;
            bis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
