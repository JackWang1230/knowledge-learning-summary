package cn.wr.bloomfilter;

/**
 * @author : WangRui
 * @date : 2022/10/10
 */

public  class MyBloomFilter {

    //减少哈希冲突优化1：增加过滤器容量为数据3-10倍
    //定义布隆过滤器容量，最好传入2的整次幂数据

    private long cap;


    public MyBloomFilter(long cap) {
        this.cap = cap;
    }

    //传入一个字符串，获取在BitMap中的位置
    public long getOffset(String value){
        long result = 0L;

        //减少哈希冲突优化2：优化哈希算法
        //对字符串每个字符的Unicode编码乘以一个质数31再相加
        for (char c : value.toCharArray()){
            result += result * 31 + c;
        }
        //取模，使用位与运算代替取模效率更高
        return  result & (cap - 1);
    }
}
