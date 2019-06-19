package cn.uestc.preprocessing;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class TestNormalize {
    public static void main(String[] args) throws Exception {
        //读取数据
        DataSource source = new DataSource("D:\\WEKA\\Weka-3-8\\data\\iris.arff"); //获取数据源
        Instances instances = source.getDataSet();//导入数据

//归一化
        System.out.println("Step 2. 归一化...");
        Normalize norm = new Normalize();//建立一个归一化filter
        norm.setInputFormat(instances);//为filter导入数据
        Instances newInstances = Filter.useFilter(instances, norm);//得到归一化后的数据

//打印结果（printAttribute函数在后面给出）
        printAttribute(newInstances);

    }
    public static void printAttribute(Instances instances)
    {
        int numOfAttributes = instances.numAttributes();
        for(int i = 0; i < numOfAttributes ;++i)
        {
            Attribute attribute = instances.attribute(i);
            System.out.print(attribute.name() + "     ");
        }
        System.out.println();
        //打印实例
        int numOfInstance = instances.numInstances();
        for(int i = 0; i < numOfInstance; ++i)
        {
            Instance instance = instances.instance(i);
            System.out.print(instance.toString() + "     "+ '\n');
        }
    }
}
