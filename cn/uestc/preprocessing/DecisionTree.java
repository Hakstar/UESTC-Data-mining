//package cn.uestc.preprocessing;
//
//import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class DecisionTree {
//    private ArrayList<String>attribute = new ArrayList<String>();
//    private ArrayList<ArrayList<String>> attrobutevalue = new ArrayList<ArrayList<String>>();
//    private ArrayList<String[]>dataSet = new ArrayList<String[]>();
//
//    public static void main(String[] args){
//        DecisionTree d = new DecisionTree("D:\\大学学习！\\数据挖掘\\task3\\数据\\forDecisionTree\\test.arff");
//        HashMap<String,HashMap<String,Object>>t = d.creatTree(d.dataSet,(ArrayList<String>)d.attribute.clone());
//        String label = d.classify(t,new String[]{"yes","no"});
//        System.out.println(label);
//    }
//    public DecisionTree(String filepath){
//        readARFF(new File(filepath));
//    }
//
//    private void readARFF(File file) {
//        String patternString = "@attribute (.*)[{](.*?)[}]";
//        try {
//            FileReader fr = new FileReader(file);
//            BufferedReader br = new BufferedReader(fr);
//            String line;
//            Pattern pattern = Pattern.compile(patternString);
//            while ((line = br.readLine())!=null){
//                if (line.startsWith("%")||line.equals("")){
//                    continue;
//                }
//                Matcher matcher = pattern.matcher(line);
//                if (matcher.find()) {
//                    attribute.add(matcher.group(1).trim());
//                    String[] values = matcher.group(2).split(",");
//                    ArrayList<String> al = new ArrayList<String>(values.length);
//                    for (String value : values) {
//                        al.add(value.trim());
//                    }
//                    attrobutevalue.add(al);
//                }else if(line.startsWith("@data")){
//                    while ((line=br.readLine())!= null){
//                        if (line==""){
//                            continue;
//                        }
//                        line = line.replace("`","");
//                        String[]row = line.split(",");
//                        dataSet.add(row);
//                    }
//                }else {
//                    continue;
//                }
//            }
//            br.close();
//        }catch (IOException e1){
//            e1.printStackTrace();
//        }
//
//    }
//
//
//    static public double log(double value,double base){
//        return Math.log(value)/Math.log(base);
//    }
//
//    public double calcShannonEnt(ArrayList<String[]>data){
//        double shannonEnt = 0;
//        int numEntries = data.size();
//        HashMap<String,Integer>labelCounts = new HashMap<String,Integer>();
//
//        for (int i = 0;i<data.size();i++){
//            String currentLabel = data.get(i)[data.get(i).length - 1];
//            if (!labelCounts.containsKey(currentLabel)){
//                labelCounts.put(currentLabel,1);
//            }else {
//                labelCounts.put(currentLabel,labelCounts.get(currentLabel)+1);
//            }
//        }
//        Iterator<String>iterator = labelCounts.keySet().iterator();
//        while (iterator.hasNext()){
//            String key = iterator.next();
//            double prob = 1.0*labelCounts.get(key)/numEntries;
//            shannonEnt -= prob * log(prob,2);
//        }
//        return shannonEnt;
//    }
//    public ArrayList<String[]>splitDataSet(ArrayList<String[]>data,int feature,String value){
//        ArrayList<String[]>subDataSet = new ArrayList<String[]>();
//        for (int i = 0;i<data.size();i++){
//            String[] currentFeat = data.get(i);
//            if (currentFeat[feature].equals(value)){
//                String[] reducedFeature = new String[currentFeat.length - 1];
//                if (feature == 0){
//                    System.arraycopy(currentFeat,1,reducedFeature,0,currentFeat.length - 1);
//                }else {
//                    System.arraycopy(currentFeat,0,reducedFeature,0,feature);
//                    System.arraycopy(currentFeat,feature + 1 ,reducedFeature,feature,currentFeat.length-1-feature);
//                }
//                subDataSet.add(reducedFeature);
//            }
//        }
//        return subDataSet;
//    }
//    int chooseBestFeatureToSplit(ArrayList<String[]>data){
//        int numFeatures = data.get(0).length - 1;
//        double baseEntrop = calcShannonEnt(data);
//        double bestInfoGain = 0;
//        int bestFeature = -1;
//        for (int i = 0;i<numFeatures;i++){
//            double newEntropy = 0;
//            double infoGain = 0;
//            Set<String>uniqueValsSet = new HashSet<String>();
//
//            for (int j= 0;j<data.size();j++){
//                uniqueValsSet.add(data.get(j)[i]);
//            }
//            for (String value : uniqueValsSet){
//                ArrayList<String[]>subSetData = splitDataSet(data,i,value);
//                double prob = 1.0*subSetData.size()/data.size();
//                newEntropy += prob * calcShannonEnt(subSetData);
//            }
//
//            infoGain = baseEntrop - newEntropy;
//            if (infoGain>bestInfoGain){
//                bestInfoGain = infoGain;
//                bestFeature = i;
//            }
//        }
//        return bestFeature;
//    }
//
//    String majorityCount(Vector<String>classList){
//        HashMap<String,Integer>classCount = new HashMap<String,Integer>();
//        int maxVote = 0;
//        String majorClass = null;
//
//        for (String classType : classList){
//            if (!classCount.containsKey(classType)){
//                classCount.put(classType,1);
//            }else{
//                classCount.put(classType,classCount.get(classType)+1);
//            }
//        }
//        Iterator<String>iterator = classCount.keySet().iterator();
//        while (iterator.hasNext()){
//            String key = iterator.next();
//            if (classCount.get(key)>maxVote){
//                maxVote = classCount.get(key);
//                majorClass = key;
//            }
//        }
//        return majorClass;
//    }
//
//    HashMap<String,HashMap<String,Object>> creatTree(ArrayList<String[]>data,ArrayList<String>attributeLabls){
//        Vector<String>classList = new Vector<String>();
//        int bestFeature = -1;
//        String bestFeatureLabel = null;
//        HashMap<String,HashMap<String,Object>>myTree = new HashMap<String,HashMap<String, Object>>();
//
//        for (int i = 0;i<data.size();i++){
//            String[] ithData = data.get(i);
//            classList.add(ithData[ithData.length - 1]);
//        }
//
//        //判断该分支相下实例的类是否全部相同，相同则停止划分，并返回该类别
//        int count = 0;
//        for (int i = 0;i<classList.size();i++){
//            if (classList.get(i).equals(classList.get(0))){
//                count++;
//            }
//        }
//        if (count == classList.size()){
//            myTree.put(classList.get(0),null);
//            return  myTree;
//        }
//
//        //遍历完所有划分数据集，使用多数表决返回类别
//        if (data.get(0).length == 1){
//            myTree.put(majorityCount(classList),null);
//            return myTree;
//        }
//
//        //选取最佳的划分特征
//        bestFeature = chooseBestFeatureToSplit(data);
//        bestFeatureLabel = attributeLabls.get(bestFeature);
//        myTree.put(bestFeatureLabel,new HashMap<String,Object>());
//        attributeLabls.remove(bestFeature);
//
//        //递归构建决策树
//        Set<String>uniqueValsSet = new HashSet<String>();
//        for (int i= 0;i<data.size();i++){
//            uniqueValsSet.add(data.get(i)[bestFeature]);
//        }
//        for (String value:uniqueValsSet){
//            ArrayList<String>subLabels = new ArrayList<String>(attributeLabls);
//            ArrayList<String[]>subDataSet = splitDataSet(data,bestFeature,value);
//            HashMap<String,HashMap<String,Object>>subTree = creatTree(subDataSet,subLabels);
//            myTree.get(bestFeatureLabel).put(value,subTree);
//        }
//
//        return myTree;
//
//    }
//
//    String classify(HashMap<String,HashMap<String,Object>>tree,String[]testData){
//        String root = tree.keySet().iterator().next();
//        HashMap<String ,Object>secondLayer = tree.get(root);
//        int featureIndex = attribute.indexOf(root);
//        String classLabel = null;
//
//        //叶子节点没有SecondLayer
//        if (secondLayer == null){
//            return root;
//        }
//        for (String key:secondLayer.keySet()){
//            if (testData[featureIndex].equals(key)){
//                classLabel = classify((HashMap<String,HashMap<String,Object>>)secondLayer.get(key),testData);
//            }
//
//        }
//        return classLabel;
//    }
//
//}
package cn.uestc.preprocessing;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecisionTree {
    //储存原始属性的名称
    private ArrayList<String> attribute = new ArrayList<String>(); //
    //储存每个属性的取值
    private ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); //
    //原始数据
    private ArrayList<String[]> dataSet = new ArrayList<String[]>();//

    public static void main(String[] args) throws Exception {
        //读取数据集
        DecisionTree d = new DecisionTree("D:\\大学学习！\\数据挖掘\\task3\\数据\\forDecisionTree\\test.arff");
        //保存树结构，key 父节点 value 子树
        HashMap<String, HashMap<String, Object>> t = d.createTree(d.dataSet, (ArrayList<String>) d.attribute.clone());
        System.out.println();
        String label = d.classify(t, new String[]{"no", "no"});
        System.out.print("lable:    "+label);
    }

    public DecisionTree(String filePath) throws IOException {
        readARFF(new File("D:\\大学学习！\\数据挖掘\\task3\\数据\\forDecisionTree\\test.arff"));
    }


    public void readARFF(File file) throws IOException {
        //匹配属性头部的正则 字符串
        String patternstring = "@attribute(.* )[{](.*)[}]";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            //创建正则表达式
            Pattern pattern = Pattern.compile(patternstring);
            while ((line = br.readLine()) != null) {
                if (line.startsWith("%") || line.equals("")) {
                    continue;
                }
                // matcher实例 提供对正则表达式的分组支持和多次匹配支持
                Matcher matcher = pattern.matcher(line);
                //如果匹配成功 读取属性名
                if (matcher.find()) {
                    attribute.add(matcher.group(1).trim());
//                    line = line.replace("'", "");
                    String[] values = matcher.group(2).split(",");
                    ArrayList<String> al = new ArrayList<String>(values.length);
                    for (String value : values) {
                        al.add(value.trim());
                    }
                    attributevalue.add(al);
                }
                //读取data
                else if (line.startsWith("@data")) {
                    while ((line = br.readLine()) != null) {
                        if (line == "")
                            continue;
                        line = line.replace("'", "");
                        String[] row = line.split(",");
                        dataSet.add(row);
                    }
                } else {
                    continue;
                }
            }
            br.close();
        } catch (IOException el) {
            el.printStackTrace();
        }
    }

    static public double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
    //计算父类节点的信息熵
    public double calcShannonEnt(ArrayList<String[]> data) {
        double shannonEnt = 0;
        int numEntries = data.size();
        HashMap<String, Integer> labelCounts = new HashMap<String, Integer>();
        //为所有可能的分类创建数据字典
        for (int i = 0; i < data.size(); i++) {
            String currentLabel = data.get(i)[data.get(i).length - 1];
            if (!labelCounts.containsKey(currentLabel)) {
                labelCounts.put(currentLabel, 1);
            } else {
                labelCounts.put(currentLabel, labelCounts.get(currentLabel) + 1);
            }
        }
        //计算每个可能分类的支持度
        Iterator<String> iterator = labelCounts.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            double prob = 1.0 * labelCounts.get(key) / numEntries;
            //计算父类节点的信息熵
            shannonEnt -= prob * log(prob, 2);
        }
        return shannonEnt;
    }
    //分割子树 输入：需分裂的数据集 用来分类数据集的特征 数据的类标签
    public ArrayList<String[]> splitDataSet(ArrayList<String[]> data, int feature, String value) {
        ArrayList<String[]> subDataSet = new ArrayList<String[]>();
        //返回数据集子集,包含feature维特征取值为value的数据，删除维度的feature
        for (int i = 0; i < data.size(); i++) {
            String[] currentFeat = data.get(i);
            if (currentFeat[feature].equals(value)) {
                String[] reducedFeature = new String[currentFeat.length - 1];
                if (feature == 0) {
                    System.arraycopy(currentFeat, 1, reducedFeature, 0, currentFeat.length - 1);
                } else {
                    System.arraycopy(currentFeat, 0, reducedFeature, 0, feature);
                    System.arraycopy(currentFeat, feature + 1, reducedFeature, feature, currentFeat.length - 1 - feature);
                }
                subDataSet.add(reducedFeature);
            }
        }
        return subDataSet;
    }
    //选取最佳的分类特征
    int chooseBestFeatureToSplit(ArrayList<String[]> data) {
        int numFeatures = data.get(0).length - 1;
        //计算父类节点的信息熵
        double baseEntrop = calcShannonEnt(data);
        //最大的信息增益
        double bestlnfoGain = 0;
        int bestFeature = -1;
        //计算每个属性的信息增益，返回最佳属性
        for (int i = 0; i < numFeatures; i++) {
            double newEntropy = 0;
            double infoGain = 0;
            //父节点下的子树的label
            Set<String> uniqueValsSet = new HashSet<String>();
            for (int j = 0; j < data.size(); j++) {
                uniqueValsSet.add(data.get(j)[i]);
            }
            //计算在子条件已经发生的情况下父类的信息熵
            for (String value : uniqueValsSet) {
                ArrayList<String[]> subSetData = splitDataSet(data, i, value);
                double prob = 1.0 * subSetData.size() / data.size();
                newEntropy += prob * calcShannonEnt(subSetData);
            }
            //计算信息增益
            infoGain = baseEntrop - newEntropy;
            if (infoGain > bestlnfoGain) {
                bestlnfoGain = infoGain;
                bestFeature = i;
            }
        }
        return bestFeature;
    }

    String majorityCount(Vector<String> classList) {
        HashMap<String, Integer> classCount = new HashMap<String, Integer>();
        int maxVote = 0;
        String majorityClass = null;
        for (String classType : classList) {
            if (!classCount.containsKey(classType)) {
                classCount.put(classType, 1);
            } else {
                classCount.put(classType, classCount.get(classType) + 1);
            }
        }
        Iterator<String> iterator = classCount.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (classCount.get(key) > maxVote) {
                maxVote = classCount.get(key);
                majorityClass = key;
            }
        }
        return majorityClass;
    }

    HashMap<String, HashMap<String, Object>> createTree(ArrayList<String[]> data, ArrayList<String> attributeLabels) throws Exception {
        Vector<String> classList = new Vector<String>();
        int bestFeature = -1;
        String bestFeatureLabel = null;
        HashMap<String, HashMap<String, Object>> myTree = new HashMap<String, HashMap<String, Object>>();
        //classList 储存属性的所有值
        for (int i = 0; i < data.size(); i++) {
            String[] ithData = data.get(i);
            classList.add(ithData[ithData.length - 1]);
        }
        //判断该分枝下的实例类别是否全部相同，相同则停止划分，返回该类别
        int count = 0;
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).equals(classList.get(0))) {
                count++;

            }
        }
        if (count == classList.size()) {
            myTree.put(classList.get(0), null);
            return myTree;
        }
        //遍历完所有划分数据集的属性，使用多数表决返回类别
        if (data.get(0).length == 1) {
            myTree.put(majorityCount(classList), null);
            return myTree;
        }
        //选取最佳的划分特征
        bestFeature = chooseBestFeatureToSplit(data);
        bestFeatureLabel = attributeLabels.get(bestFeature);
        myTree.put(bestFeatureLabel, new HashMap<String, Object>());
        attributeLabels.remove(bestFeature);
        //递归地构建决策树
        Set<String> uniqueValsSet = new HashSet<String>();
        for (int i = 0; i < data.size(); i++) {
            uniqueValsSet.add(data.get(i)[bestFeature]);
        }
        for (String value : uniqueValsSet) {
            ArrayList<String> subLabels = new ArrayList<String>(attributeLabels);
            ArrayList<String[]> subDataSet = splitDataSet(data, bestFeature, value);
            //创建子树
            HashMap<String, HashMap<String, Object>> subTree = createTree(subDataSet, subLabels);
            //加入原树
            myTree.get(bestFeatureLabel).put(value, subTree);
        }
        return myTree;
    }
    //测试集分类
    String classify(HashMap<String, HashMap<String, Object>> tree, String[] testData) {
        String root = tree.keySet().iterator().next();
        HashMap<String, Object> secondLayer = tree.get(root);
        int featurelndex = attribute.indexOf(root);
        String classLabel = null;
        //如果没有子树返回 root
        System.out.println("决策树：   "+secondLayer);

        if (secondLayer == null) {
            return root;
        }
        //递归求所属的类别
        for (String key : secondLayer.keySet()) {
            if (testData[featurelndex].equals(key)) {
                classLabel = classify((HashMap<String,HashMap<String, Object>>)secondLayer.get(key), testData);
                break;
            }
        }
        return classLabel;
    }
}


