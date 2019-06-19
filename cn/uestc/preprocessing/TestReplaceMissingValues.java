package cn.uestc.preprocessing;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class TestReplaceMissingValues {
    public static void main(String[] args) throws Exception {
        //读取数据
        DataSource source = new DataSource("D:\\WEKA\\Weka-3-8\\data\\labor.arff"); //获取数据源
        Instances instances = source.getDataSet();//导入数据

        //数据缺失值处理
        System.out.println("Step 2. 数据缺失值处理...");
        ReplaceMissingValues rmw = new ReplaceMissingValues();
        rmw.setInputFormat(instances);
        Instances newInstances = Filter.useFilter(instances, rmw);


        //打印结果（printAttribute函数在后面给出）
        printAttribute(newInstances);

        System.out.println("Step 3.保存新文件....");
        ConverterUtils.DataSink.write("D:\\Data_minning—done\\RePlaceMissingValues.arff",instances);
        System.out.println("Finished");
    }

    public static void printAttribute(Instances instances) {
        int numOfAttributes = instances.numAttributes();
        for (int i = 0; i < numOfAttributes; ++i) {
            Attribute attribute = instances.attribute(i);
            System.out.print(attribute.name() + "     ");
        }
        System.out.println();
        //打印实例
        int numOfInstance = instances.numInstances();
        for (int i = 0; i < numOfInstance; ++i) {
            Instance instance = instances.instance(i);
            System.out.print(instance.toString() + "     " + '\n');
        }
    }
}
