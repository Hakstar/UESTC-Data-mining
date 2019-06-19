package cn.uestc.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Apriori {
    //事务数据库
    private Map<Integer, Set<String>> txDatabase;
    //最小支持度和置信度
    private Float minSup;
    private Float minConf;
    //事务数据库中的事务数
    private Integer txDatabaseCount;
    //频繁项集集合
    //Integer: k项集 Map：<项集,支持度>
    private Map<Integer, Map<Set<String>, Float>> freqItemSet;//代码示例有问题，ss
    //频繁关联规则集合
    //Set<String>->Map<Set<String>, Float>    置信度>min_conf，规则是强规则
    private Map<Set<String>, Set<Map<Set<String>, Float>>> assiciationRules;

    //new HashSet<Map<Set<String>, Float>>
    public Apriori(Map<Integer, Set<String>> txDatabase, Float minSup, Float minConf) {
        this.txDatabase = txDatabase;
        this.minSup = minSup;
        this.minConf = minConf;
        this.txDatabaseCount = this.txDatabase.size();
        freqItemSet = new TreeMap<Integer, Map<Set<String>, Float>>();
        assiciationRules = new HashMap<Set<String>, Set<Map<Set<String>, Float>>>();

    }

    public static void main(String[] args) throws Exception {
        String fn = "D:\\data_minning\\dataminning-1\\src\\data\\data_Apriori.txt";
        float minSup = 0.3f;
        float minConf = 0.6f;
        //文件的读入
        File file = new File(fn);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        Map<Integer, Set<String>> DB = new HashMap<Integer, Set<String>>();
        String line;
        String sp = ",";//分隔符
        int num = 0;
        while ((line = br.readLine()) != null) {
            String[] temp = line.trim().split(sp);//去除字符串两端的字符
            Set<String> set = new TreeSet<String>();

            //将一个事务（1行数据）-> Items 用集合存储

            for (int i = 1; i < temp.length; i++) {
                set.add(temp[i].trim());
            }
            num++;//TID
            DB.put(num, set);//事务数据库
        }
        //实例化
        Apriori apr = new Apriori(DB, minSup, minConf);
        //寻找频繁项集
        apr.findAllFreqItemSet();
        //关联规则挖掘
        apr.findAssociationRules();

    }

    public void findAllFreqItemSet() {
        //1项集
        Map<Set<String>, Float> freqOneItemSet = this.find_Frequent_One_Itemsets();
        freqItemSet.put(1, freqOneItemSet);//bug
        System.out.println("频繁1-项集" + freqOneItemSet);

        //频繁k项集（k>1）
        int k = 2;
        while (true) {
            //寻找频繁K项集 Set<Set<String>>
            Set<Set<String>> candFreItemsets = apriori_Gen(k, freqItemSet.get(k - 1).keySet());
            //加入k
            Map<Set<String>, Float> freqKItemSetMap = getFreqKItemSet(k, candFreItemsets);
            //为什么为空
            if (!freqKItemSetMap.isEmpty()) {
                freqItemSet.put(k, freqKItemSetMap);
            } else {
                break;
            }

            System.out.println("频繁" + k + "-项集" + freqKItemSetMap);
            k++;
        }

    }

    //寻找频繁一项集
    public Map<Set<String>, Float> find_Frequent_One_Itemsets() {
        //<item,支持度>
        Map<Set<String>, Float> L1 = new HashMap<Set<String>, Float>();
        //<item,n> n 支持度计数
        Map<Set<String>, Integer> item1SetMap = new HashMap<Set<String>, Integer>();
        //迭代器 items
        Iterator<Map.Entry<Integer, Set<String>>> it = this.txDatabase.entrySet().iterator();
        //支持度计数 生成候选频繁一项集
        while (it.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = it.next();
            //item集合
            Set<String> itemSet = entry.getValue();
            //遍历
            for (String item : itemSet) {
                Set<String> key = new HashSet<String>();
                key.add(item.trim());
                //一项集不包含item，<item,1> 包含 value++
                if (!item1SetMap.containsKey(key)) {
                    item1SetMap.put(key, 1);
                } else {
                    int value = 1 + item1SetMap.get(key);
                    item1SetMap.put(key, value);
                }
            }

        }
        //找出支持度大于minSup的频繁一项集
        Iterator<Map.Entry<Set<String>, Integer>> iter = item1SetMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Set<String>, Integer> entry = iter.next();
            //计算支持度 txDatabaseCount=7 此处
            Float support = new Float(entry.getValue().toString()) / new Float((txDatabaseCount));
            if (support >= minSup)
                L1.put(entry.getKey(), support);
        }
        return L1;
    }

    //从K项频繁项集产生候选K+1项集函数
    public Set<Set<String>> apriori_Gen(int k, Set<Set<String>> freqKItemSet) {

        Set<Set<String>> candFreqKItemSet = new HashSet<Set<String>>();
        //生成k项频繁项集的迭代器
        Iterator<Set<String>> it1 = freqKItemSet.iterator();

        while (it1.hasNext()) {
            Set<String> itemSet1 = it1.next();
            Iterator<Set<String>> it2 = freqKItemSet.iterator();
            //生成k+1项集
            while (it2.hasNext()) {
                Set<String> itemSet2 = it2.next();
                if (!itemSet1.equals(itemSet2)) {
                    //连接步 k项集中k-2必须相同
                    Set<String> commItems = new HashSet<String>();
                    commItems.addAll(itemSet1);
                    //移除itemSet1 和 itemSet2 相同的部分 大小k-2可连接
                    commItems.retainAll(itemSet2);
                    if (commItems.size() == k - 2) {
                        Set<String> candiItems = new HashSet<String>();
                        //实现连接 求交集 size = k
                        candiItems.addAll(itemSet1);
                        candiItems.removeAll(itemSet2);
                        candiItems.addAll(itemSet2);

                        //剪枝步 查看生成的K项集的任意K-1项集是否都在已获取的频繁k-1项集freqKItemSet中
                        //不包含不频繁项集  保留
                        if (!has_infrequent_subset(candiItems, freqKItemSet)) {
                            candFreqKItemSet.add(candiItems);
                        }
                    }
                }
            }
        }
        return candFreqKItemSet;
    }

    //判断一个候选是否应该剪枝 判断是否含有不频繁项集
    private boolean has_infrequent_subset(Set<String> itemSet, Set<Set<String>> freqKItemSet) {
        //获取itemSet的所有k-1子集
        Set<Set<String>> subItemSet = new HashSet<Set<String>>();
        //item 迭代器
        Iterator<String> itr = itemSet.iterator();
        while (itr.hasNext()) {
            //深拷贝

            Set<String> subItem = new HashSet<String>();
            //item 迭代器
            Iterator<String> it = itemSet.iterator();
            while (it.hasNext()) {
                subItem.add(it.next());
            }

            //去掉一个项即为k-1子集 迭代项
            subItem.remove(itr.next());
            //得到k-1子项集
            subItemSet.add(subItem);
        }

        //判断k频繁项集中是否包含k+1项集中的k项集 不包含返回 false 反之 true
        Iterator<Set<String>> it = subItemSet.iterator();
        while (it.hasNext()) {
            if (!freqKItemSet.contains(it.next())) ;
            return false;
        }
        return true;
    }

    //得到频繁k项集   candFreqKItemSet k 项集
    public Map<Set<String>, Float> getFreqKItemSet(int k, Set<Set<String>> candFreqKItemSet) {
        Map<Set<String>, Integer> candFreqKItemSetMap = new HashMap<Set<String>, Integer>();
        //扫描事务数据库
        Iterator<Map.Entry<Integer, Set<String>>> it = txDatabase.entrySet().iterator();
        //统计支持数
        while (it.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = it.next();
            Iterator<Set<String>> iter = candFreqKItemSet.iterator();
            while (iter.hasNext()) {
                Set<String> s = iter.next();
                //支持度计数的实现
                if (entry.getValue().containsAll(s)) {
                    if (!candFreqKItemSetMap.containsKey(s)) {
                        candFreqKItemSetMap.put(s, 1);
                    } else {
                        int value = 1 + candFreqKItemSetMap.get(s);
                        candFreqKItemSetMap.put(s, value);
                    }
                }

            }

        }
        //计算支持度并生成最终的频繁 k 项集
        Map<Set<String>, Float> freqKItemSetMap = new HashMap<Set<String>, Float>();
        Iterator<Map.Entry<Set<String>, Integer>> itr = candFreqKItemSetMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Set<String>, Integer> entry = itr.next();
            //计算支持du
            float support = new Float(entry.getValue().toString()) / new Float((txDatabaseCount));
            if (support < minSup) {
                itr.remove();
            } else {
                freqKItemSetMap.put(entry.getKey(), support);
            }
        }
        return freqKItemSetMap;
    }


    public void findAssociationRules() {
        //删除频繁一项集 要删除必须增加一个深拷贝
        //freqItemSet.remove(1);

        Iterator<Map.Entry<Integer, Map<Set<String>, Float>>> it = freqItemSet.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Map<Set<String>, Float>> entry = it.next();
//            System.out.println(entry);
            for (Set<String> itemSet : entry.getValue().keySet()) {
//                System.out.println(itemSet);

                //对每个频繁项集进行关联规则挖掘
                int n = itemSet.size() / 2;//根据集合对称性，只需要得到一半的真子集
//                System.out.println(itemSet.size());
                for (int i = 1; i <= n; i++) {
                    //得到频繁项集元素itemSet的作为条件的真子集集合
                    Set<Set<String>> subset = ProperSubsetCombination.getProperSubset(i, itemSet);
//                    System.out.println(subset);
                    //对条件的真子集集合中的每个条件项集，获取到对应的结论项集，从而进一步挖掘关联规则
                    for (Set<String> conditionSet : subset) {
                        Set<String> conclusionSet = new HashSet<String>();
                        conclusionSet.addAll(itemSet);
                        conclusionSet.removeAll((conditionSet));//删除条件中存在的频繁项
//                        System.out.println(conclusionSet);
                        int s1 = conditionSet.size();
                        int s2 = conclusionSet.size();
                        //规则{A,B,C} ->{D}的置信度为support(ABCD)/ support (ABC)
                        float sup1 = freqItemSet.get(s1).get(conditionSet);
                        float sup2 = freqItemSet.get(s2).get(conclusionSet);
                        float sup = freqItemSet.get(s1 + s2).get(itemSet);

                        float conf1 = sup / sup1;
                        float conf2 = sup / sup2;
                        decsion(conditionSet, conclusionSet, conf1);
                        decsion(conditionSet, conclusionSet, conf2);
                    }
                }
            }
        }
        System.out.println("关联规则（强规则）：" + assiciationRules);

    }

    private void decsion(Set<String> conditionSet, Set<String> conclusionSet, float conf2) {
        if (conf2 >= minConf) {
            //如果不存在以该结论平频繁项集为调节的关联规则
            if (assiciationRules.get(conditionSet) == null) {
                //如果不存在以该结论平频繁项集为调节的关联规则
                Set<Map<Set<String>, Float>> conclusionSetSet = new HashSet<Map<Set<String>, Float>>();
                Map<Set<String>, Float> sets = new HashMap<Set<String>, Float>();
                sets.put(conclusionSet, conf2);
                conclusionSetSet.add(sets);
                assiciationRules.put(conditionSet, conclusionSetSet);
            } else {
                Map<Set<String>, Float> sets = new HashMap<Set<String>, Float>();
                sets.put(conclusionSet, conf2);
                assiciationRules.get(conditionSet).add(sets);
            }
        }
    }
}
//package cn.uestc.preprocessing;
//import weka.core.Debug;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.*;
//
//public class AprioriAlgorithm {
//    private Map<Integer,Set<String>>txDatabase;
//    private Float minSup;
//    private Float minConf;
//    private Integer txDatabaseCount;
//    private Map<Integer,Set<Set<String>>>freqItemSet;
//    private Map<Set<String>,Set<Set<String>>>assiciationRules;
//
//    public AprioriAlgorithm(Map<Integer,Set<String>>txDatabase,Float minSup,Float minConf)
//    {
//        this.txDatabase = txDatabase;
//        this.minConf = minConf;
//        this.minSup = minSup;
//        this.txDatabaseCount = this.txDatabase.size();
//        freqItemSet = new TreeMap<Integer, Set<Set<String>>>();
//        assiciationRules = new HashMap<Set<String>, Set<Set<String>>>();
//    }
//    public static void main(){
//        float minSup = 0.5f;
//        float minConf = 0.6f;
//        File file = new File("D:\\data_minning\\dataminning-1\\src\\cn\\uestc\\preprocessing");
//        FileReader fr = new FileReader(file);//???
//        BufferedReader br = new BufferedReader(fr);
//        Map<Integer,Set<String>>DB = new HashMap<Integer, Set<String>>();
//
//        String line;
//        String sp = ",";
//        int num = 0;
//        while ((line = br.readLine())!= null){
//            String[]temp = line.trim().split(sp);
//            Set<String> set = new TreeSet<String>();
//            for (int i= 1;i<temp.length;i++){
//                set.add(temp[i].trim());
//            }
//            num++;
//            DB.put(num,set);
//        }
//
//        AprioriAlgorithm apr = new AprioriAlgorithm(DB,minSup,minConf);
//        apr.findAllFreqItemSet();
//        apr.findAssociationRules();
//
//    }
//    public void findAllFreqItemSet(){
//        Map<Set<String>,Float>freOneItemSet = this.find_Frequent_One_Itemsets();
//        freqItemSet.put(1,freOneItemSet);
//        System.out.println("频繁1-项集："+freqItemSet);
//
//        //
//        int k = 2;
//        while (true){
//            Set<Set<String>>candFreItemsets = apriori_Gen(k,freqItemSet.get(k-1).keySet());
//            Map<Set<String>,Float>freqKItemSetMap = getFreqKItemsets(k,candFreItemsets);
//            if (!freqKItemSetMap.isEmpty()){
//                freqItemSet.put(k,freOneItemSet);
//            }
//            else{
//                break;
//            }
//            System.out.println("频繁"+k+"-项集"+freqKItemSetMap);
//            k++;
//        }
//    }
//    public Map<Set<String>,Float>find_Frequent_One_Itemsets(){
//        Map<Set<String>,Float>L1 = new HashMap<Set<String>,Float>();
//        Map<Set<String>,Integer>item1SetMap  =new HashMap<Set<String>, Integer>();
//        Iterator<Map.Entry<Integer,Set<String>>>it = DB.entrySet().iterator();
//
//        while (it.hasNext()){
//            Map.Entry<Integer,Set<String>>entry = it.next();
//            Set<String>itemSet = entry.getValue();
//            for (String item : itemSet){
//                Set<String>key = new HashSet<String>();
//                key.add(item.trim());
//                if (!item1SetMap.containsKey(key)){
//                    item1SetMap.put(key,1);
//                }
//                else {
//                    int value = 1 + item1SetMap.get(key);
//                    item1SetMap.put(key,value);
//                }
//            }
//        }
//        Iterator<Map.Entry<Set<String>,Integer>>iter = item1SetMap.entrySet().iterator();
//        while (iter.hasNext()){
//            Map.Entry<Set<String>,Integer>entry = iter.next();
//
//            Float support = new Float(entry.getValue().toString())/new Float(num);
//            if (support >= minSup)
//                L1.put(entry.getKey(),support);
//        }
//        return L1;
//    }
//    public Set<Set<String>>apriori_Gen(int k,Set<Set<String>>freqKItemSet){
//        Set<Set<String>>candFreqKItemSet = new HashSet<Set<String>>();
//        Iterator<Set<String>>it1 = freqKItemSet.iterator();
//        while (it1.hasNext()){
//            Set<String>itemSet1 = it1.next();
//            Iterator<Set<String>>it2 = freqKItemSet.iterator();
//            while (it2.hasNext()){
//                Set<String>itemSet2 = it2.next();
//                if (!itemSet1.equals(itemSet2)){
//                    Set<String>commItems = new HashSet<String>();
//                    commItems.addAll(itemSet1);
//                    commItems.retainAll(itemSet2);
//                    if (commItems.size()==k-2){
//                        Set<String>candiItem = new HashSet<String>();
//                        candiItem.addAll(itemSet1);
//                        candiItem.removeAll(itemSet2);
//                        candiItem.addAll(itemSet2);
//                        if (!has_infrequent_subset(candiItem,freqKItemSet)){
//                            candFreqKItemSet.add(candiItem);
//                        }
//                    }
//                }
//            }
//        }
//        return candFreqKItemSet;
//    }
//    public boolean has_infrequent_subset(Set<String>itemSet,Set<Set<String>>freqKItemSet){
//        Set<Set<String>>subItemSet = new HashSet<Set<String>>();
//        Iterator<String>itr = itemSet.iterator();
//        while (itr.hasNext()){
//            Set<String> subItem = new HashSet<String>();
//            Iterator<String>it = itemSet.iterator();
//            while (it.hasNext()){
//                subItem.add(it.next());
//            }
//            subItem.remove(itr.next());
//            subItemSet.add(subItem);
//        }
//        Iterator<Set<String>>it = subItemSet.iterator();
//        while (it.hasNext()){
//            if (!freqKItemSet.contains(it.next()));
//                return true;
//        }
//        return false;
//    }
//    public Map<Set<String>,Float>getFreqKItemsets(int k,Set<Set<String>>candFreqKItemSet){
//        Map<Set<String>,Integer>candFreqKItemSetMap = new HashMap<Set<String>,Integer>();
//        //扫描事务数据库
//        Iterator<Map.Entry<Integer,Set<String>>>it = DB.entry().iterator();
//        //统计支持数
//
//        while (it.hasNext()){
//            Map.Entry<Integer,Set<String>> entry = it.next();
//            Iterator<Set<String>>iter = candFreqKItemSet.iterator();
//            while (iter.hasNext()){
//                Set<String>s = iter.next();
//                if (entry.getValue().containsAll(s)){
//                    if (!candFreqKItemSetMap.containsKey(s)){
//                        candFreqKItemSetMap.put(s,1);
//                    }else {
//                        int value = 1 + candFreqKItemSetMap.get(s);
//                    }
//                }
//            }
//        }
////        System.out.println(candFreqKItemSetMap.size());
////        计算支持度炳胜层最终的频繁K-项集
//        Map<Set<String>,Float>freqKItemSetMap = new HashMap<Set<String>,Float>();
//        Iterator<Map.Entry<Set<String>,Integer>>itr = candFreqKItemSetMap.entrySet().iterator();
//        while (itr.hasNext()){
//            Map.Entry<Set<String>,Integer>entry = itr.next();
//            //计算支持度
//            float support = new Float(entry.getValue().toString())/num;
//            if (support<minSup){
//                itr.remove();
//            }else {
//                freqKItemSetMap.put(entry.getKey(),support);
//            }
//        }
//        return freqKItemSetMap;
//    }
//    public void findAssociationRules(){
////        freqItemSet.remove(1);
//        Iterator<Map.Entry<Integer,Map<Set<String>,Float>>>it = freqItemSet.entrySet().iterator();
//        while (it.hasNext()){
//            Map.Entry<Integer,Map<Set<String>,Float>>entry = it.next();
//            for (Set<String>itemSet: entry.getValue().keySet()){
//                int n = itemSet.size()/2;
//                for (int i = 1;i<=n;i++){
//                    Set<Set<String>>subset = Proper
//                }
//            }
//        }
//    }
//}


