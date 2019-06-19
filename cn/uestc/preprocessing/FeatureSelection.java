package cn.uestc.preprocessing;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.converters.ConverterUtils.DataSink;
import weka.filters.supervised.attribute.AttributeSelection;

public class FeatureSelection {
    public static void main(String[] args) throws Exception {
        //读取数据
        DataSource source = new DataSource("D:\\WEKA\\Weka-3-8\\data\\iris.arff"); //获取数据源
        Instances instances = source.getDataSet();//导入数据

        int k = 2;
        InfoGainAttributeEval ae = new InfoGainAttributeEval();//选择evaluator为信息增益
        Ranker ranker = new Ranker();//评估函数选择ranker
        ranker.setNumToSelect(k);//设置筛选的最大特征数目
        ranker.setThreshold(0.0);//评估特征值低于该阈值的特征被筛去，在此表示只留下信息增益大于0的特征
        AttributeSelection as = new AttributeSelection();//建立特征筛选对象
        as.setEvaluator(ae);//设置筛选对象所用的评估函数
        as.setSearch(ranker);//设置筛选对象的选择函数
        as.setInputFormat(instances);//为该特征筛选对象传入数据源
        Instances reductData = Filter.useFilter(instances,as);

        System.out.println("Step 3.保存新文件....");
        DataSink.write("D:\\Data_minning—done\\FeatureSeletion.arff",reductData);
        System.out.println("Finished");
    }
}

