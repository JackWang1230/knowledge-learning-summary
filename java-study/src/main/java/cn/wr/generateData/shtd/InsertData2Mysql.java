package cn.wr.generateData.shtd;

import cn.wr.generateData.eCommerce.GenerateUserUtil;
import cn.wr.generateData.eCommerce.Insert2Mysql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

import static cn.wr.generateData.eCommerce.GenerateUserUtil.sexs;

/**
 * @author : WangRui
 * @date : 2023/4/17
 */

public class InsertData2Mysql {


    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final Logger logger = LoggerFactory.getLogger(Insert2Mysql.class);

    private static final String url = "jdbc:mysql://localhost:3306/shtd_store?useSSL=false";
    private static final String user = "root";
    private static final String password = "12345678";

    /**
     * 获取连接
     * @param url url
     * @param user user
     * @param passWord passWord
     * @return Connection
     */
    public static Connection getConnection(String url, String user, String passWord) {
        try {
            Class.forName(MYSQL_DRIVER);
            Connection connection = DriverManager.getConnection(url, user, passWord);
            return connection;
        } catch (Exception e) {
            logger.error("Mysqlutil get connection error:{}", e);
        }
        return null;
    }

    /**
     * 获取省份和城市
     * @return
     * @throws SQLException SQLException
     */
    public HashMap<Integer, List<String>> getProvinceCity() throws SQLException {

        HashMap<Integer, List<String>> provinceCityMap = new HashMap<>();
        List<String> beijing = Arrays.asList("北京");
        List<String> tianjin = Arrays.asList("天津");
        List<String> hebei = Arrays.asList("石家庄", "唐山", "秦皇岛", "邯郸", "邢台", "保定", "张家口", "承德", "沧州", "廊坊", "衡水");
        List<String> shanxi = Arrays.asList("太原", "大同", "阳泉", "长治", "晋城", "朔州", "晋中", "运城", "忻州", "临汾", "吕梁");
        List<String> neimenggu = Arrays.asList("呼和浩特", "包头", "乌海", "赤峰", "通辽", "鄂尔多斯", "呼伦贝尔", "巴彦淖尔", "乌兰察布", "兴安盟", "锡林郭勒盟", "阿拉善盟");
        List<String> liaoning = Arrays.asList("沈阳", "大连", "鞍山", "抚顺", "本溪", "丹东", "锦州", "营口", "阜新", "辽阳", "盘锦", "铁岭", "朝阳", "葫芦岛");
        List<String> jilin = Arrays.asList("长春", "吉林", "四平", "辽源", "通化", "白山", "松原", "白城", "延边朝鲜族自治州");
        List<String> heilongjiang = Arrays.asList("哈尔滨", "齐齐哈尔", "鸡西", "鹤岗", "双鸭山", "大庆", "伊春", "佳木斯", "七台河", "牡丹江", "黑河", "绥化", "大兴安岭地区");
        List<String> shanghai = Arrays.asList("黄浦区", "徐汇区", "长宁区", "静安区", "普陀区", "虹口区", "杨浦区", "闵行区", "宝山区", "嘉定区", "浦东新区", "金山区", "松江区", "青浦区", "奉贤区", "崇明区");
        List<String> jiangsu = Arrays.asList("南京", "无锡", "徐州", "常州", "苏州", "南通", "连云港", "淮安", "盐城", "扬州", "镇江", "泰州", "宿迁");
        List<String> zhejiang = Arrays.asList("杭州", "宁波", "温州", "嘉兴", "湖州", "绍兴", "金华", "衢州", "舟山", "台州", "丽水");
        List<String> anhui = Arrays.asList("合肥", "芜湖", "蚌埠", "淮南", "马鞍山", "淮北", "铜陵", "安庆", "黄山", "滁州", "阜阳", "宿州", "巢湖", "六安", "亳州", "池州", "宣城");
        List<String> fujian = Arrays.asList("福州", "厦门", "莆田", "三明", "泉州", "漳州", "南平", "龙岩", "宁德");
        List<String> jiangxi = Arrays.asList("南昌", "景德镇", "萍乡", "九江", "新余", "鹰潭", "赣州", "吉安", "宜春", "抚州", "上饶");
        List<String> shandong = Arrays.asList("济南", "青岛", "淄博", "枣庄", "东营", "烟台", "潍坊", "济宁", "泰安", "威海", "日照", "莱芜", "临沂", "德州", "聊城", "滨州", "菏泽");
        List<String> henan = Arrays.asList("郑州", "开封", "洛阳", "平顶山", "安阳", "鹤壁", "新乡", "焦作", "濮阳", "许昌", "漯河", "三门峡", "南阳", "商丘", "信阳", "周口", "驻马店", "济源");
        List<String> hubei = Arrays.asList("武汉", "黄石", "十堰", "宜昌", "襄阳", "鄂州", "荆门", "孝感", "荆州", "黄冈", "咸宁", "随州", "恩施土家族苗族自治州", "仙桃", "潜江", "天门", "神农架林区");
        List<String> hunan = Arrays.asList("长沙", "株洲", "湘潭", "衡阳", "邵阳", "岳阳", "常德", "张家界", "益阳", "郴州", "永州", "怀化", "娄底", "湘西土家族苗族自治州");
        List<String> guangdong = Arrays.asList("广州", "韶关", "深圳", "珠海", "汕头", "佛山", "江门", "湛江", "茂名", "肇庆", "惠州", "梅州", "汕尾", "河源", "阳江", "清远", "东莞", "中山", "潮州", "揭阳", "云浮");
        List<String> guangxi = Arrays.asList("南宁", "柳州", "桂林", "梧州", "北海", "防城港", "钦州", "贵港", "玉林", "百色", "贺州", "河池", "来宾", "崇左");
        List<String> hainan = Arrays.asList("海口", "三亚", "三沙", "儋州", "五指山", "琼海", "文昌", "万宁", "东方", "定安县", "屯昌县", "澄迈县", "临高县", "白沙黎族自治县", "昌江黎族自治县", "乐东黎族自治县", "陵水黎族自治县", "保亭黎族苗族自治县", "琼中黎族苗族自治县");
        List<String> chongqing = Arrays.asList("万州区", "涪陵区", "渝中区", "大渡口区", "江北区", "沙坪坝区", "九龙坡区", "南岸区", "北碚区", "綦江区", "大足区", "渝北区", "巴南区", "黔江区", "长寿区", "江津区", "合川区", "永川区", "南川区", "璧山区", "铜梁区", "潼南区", "荣昌区", "开州区", "梁平区", "武隆区", "城口县", "丰都县", "垫江县", "忠县", "云阳县", "奉节县", "巫山县", "巫溪县", "石柱土家族自治县", "秀山土家族苗族自治县", "酉阳土家族苗族自治县", "彭水苗族土家族自治县");
        List<String> sichuan = Arrays.asList("成都", "自贡", "攀枝花", "泸州", "德阳", "绵阳", "广元", "遂宁", "内江", "乐山", "南充", "眉山", "宜宾", "广安", "达州", "雅安", "巴中", "资阳", "阿坝藏族羌族自治州", "甘孜藏族自治州", "凉山彝族自治州");
        List<String> guizhou = Arrays.asList("贵阳", "六盘水", "遵义", "安顺", "毕节", "铜仁", "黔西南布依族苗族自治州", "黔东南苗族侗族自治州", "黔南布依族苗族自治州");
        List<String> yunnan = Arrays.asList("昆明", "曲靖", "玉溪", "保山", "昭通", "丽江", "普洱", "临沧", "楚雄彝族自治州", "红河哈尼族彝族自治州", "文山壮族苗族自治州", "西双版纳傣族自治州", "大理白族自治州", "德宏傣族景颇族自治州", "怒江傈僳族自治州", "迪庆藏族自治州");
        List<String> xizang = Arrays.asList("拉萨", "日喀则", "昌都", "林芝", "山南", "那曲", "阿里");
        List<String> shangxi = Arrays.asList("西安", "铜川", "宝鸡", "咸阳", "渭南", "延安", "汉中", "榆林", "安康", "商洛");
        List<String> gansu = Arrays.asList("兰州", "嘉峪关", "金昌", "白银", "天水", "武威", "张掖", "平凉", "酒泉", "庆阳", "定西", "陇南", "临夏回族自治州", "甘南藏族自治州");
        List<String> qinghai = Arrays.asList("西宁", "海东", "海北藏族自治州", "黄南藏族自治州", "海南藏族自治州", "果洛藏族自治州", "玉树藏族自治州", "海西蒙古族藏族自治州");
        List<String> ningxia = Arrays.asList("银川", "石嘴山", "吴忠", "固原", "中卫");
        List<String> xinjiang = Arrays.asList("乌鲁木齐", "克拉玛依", "吐鲁番", "哈密", "昌吉回族自治州", "博尔塔拉蒙古自治州", "巴音郭楞蒙古自治州", "阿克苏地区", "克孜勒苏柯尔克孜自治州", "喀什地区", "和田地区", "伊犁哈萨克自治州", "塔城地区", "阿勒泰地区", "石河子", "阿拉尔", "图木舒克", "五家渠", "北屯", "铁门关", "双河", "可克达拉", "昆玉");
        List<String> taiwan = Arrays.asList("台北", "高雄", "基隆", "台中", "台南", "新竹", "嘉义");
        List<String> hongkong = Arrays.asList("香港");
        List<String> aomen = Arrays.asList("澳门");

        provinceCityMap.put(1, beijing);
        provinceCityMap.put(2, tianjin);
        provinceCityMap.put(3, hebei);
        provinceCityMap.put(4, shanxi);
        provinceCityMap.put(5, neimenggu);
        provinceCityMap.put(6, liaoning);
        provinceCityMap.put(7, jilin);
        provinceCityMap.put(8, heilongjiang);
        provinceCityMap.put(9, shanghai);
        provinceCityMap.put(10, jiangsu);
        provinceCityMap.put(11, zhejiang);
        provinceCityMap.put(12, anhui);
        provinceCityMap.put(13, fujian);
        provinceCityMap.put(14, jiangxi);
        provinceCityMap.put(15, shandong);
        provinceCityMap.put(16, henan);
        provinceCityMap.put(17, hubei);
        provinceCityMap.put(18, hunan);
        provinceCityMap.put(19, guangdong);
        provinceCityMap.put(20, guangxi);
        provinceCityMap.put(21, hainan);
        provinceCityMap.put(22, chongqing);
        provinceCityMap.put(23, sichuan);
        provinceCityMap.put(24, guizhou);
        provinceCityMap.put(25, yunnan);
        provinceCityMap.put(26, xizang);
        provinceCityMap.put(27, shangxi);
        provinceCityMap.put(28, gansu);
        provinceCityMap.put(29, qinghai);
        provinceCityMap.put(30, ningxia);
        provinceCityMap.put(31, xinjiang);
        provinceCityMap.put(32, taiwan);
        provinceCityMap.put(33, hongkong);
        provinceCityMap.put(34, aomen);
        return provinceCityMap;
    }

    /**
     * 插入省市
     * @throws SQLException SQLException
     */
    public void insertProvinceCity() throws SQLException {


        HashMap<Integer, List<String>> provinceCity = getProvinceCity();
        String insertUser = "insert into base_region " +
                "(`name`,province_id) " +
                "values (?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertUser);
            for (Map.Entry<Integer, List<String>> entry : provinceCity.entrySet()) {
                for (String city : entry.getValue()) {
                    ps.setString(1, city);
                    ps.setInt(2, entry.getKey());
                    ps.addBatch();
                }
            }
            ps.executeBatch();

            // ps.setString(1, skuNo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }

    }

    /**
     * 插入用户
     * @throws SQLException SQLException
     */
    public void insertUser() throws SQLException {

        String insertUser = "insert into user_info " +
                "(user_name,sex,age) " +
                "values (?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertUser);

            for (int i = 0; i <10000; i++) {
                ps.setString(1, GenerateUserUtil.getChineseName());
                ps.setString(2, sexs[GenerateUserUtil.getRandom(sexs.length)]);
                ps.setInt(3, new Random().nextInt(30) + 18);
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
            // ps.setString(1, skuNo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }
    }


    /**
     * 插入sku
     * @throws SQLException SQLException
     */
    public void insertSku() throws SQLException{

        String skuInfo = "insert into sku_info " +
                "(spu_id,category3_id,tm_id,price,weight) " +
                "values (?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(skuInfo);

            for (int i = 0; i <10000; i++) {
                ps.setInt(1, new Random().nextInt(330) + 1);
                ps.setInt(2, new Random().nextInt(3) + 1); // 左闭右开
                ps.setInt(3, new Random().nextInt(100) + 1);
                ps.setDouble(4, Double.parseDouble(String.format("%.2f", 1.0 + Math.random() * (1000.0 - 1.0))));
                ps.setDouble(5, Double.parseDouble(String.format("%.2f", 1.0 + Math.random() * (70.0 - 1.0))));
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
            // ps.setString(1, skuNo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }
    }

    /**
     * 插入订单
     * @throws SQLException SQLException
     */
    public void insertOrderInfo() throws SQLException{

        String skuInfo = "insert into order_info " +
                "(user_id,province_id,region_id,order_status,final_total_amount,operate_time,create_time) " +
                "values (?,?,?,?,?,?,?)";

        String provinceCity = "select province_id,group_concat(id) as city_list from base_region group by province_id";
        HashMap<Integer, List<String>> integerListHashMap = new HashMap<>();
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(provinceCity);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                Integer provinceId = resultSet.getInt("province_id");
                String cityList = resultSet.getString("city_list");
                integerListHashMap.put(provinceId, Arrays.asList(cityList.split(",")));
            }
            System.out.println("------"+integerListHashMap.size());

            ps = connection.prepareStatement(skuInfo);

            for (int i = 0; i <15000; i++) {
                ps.setInt(1, new Random().nextInt(3000) + 4000);

                int provinceId = new Random().nextInt(34) + 1;
                List<String> cityList = integerListHashMap.get(provinceId);
                ps.setInt(2, provinceId);
                ps.setInt(3, Integer.parseInt(cityList.get(new Random().nextInt(cityList.size()))));
                ps.setInt(4, new Random().nextInt(2));
                ps.setDouble(5, Double.parseDouble(String.format("%.2f", 1.0 + Math.random() * (1000.0 - 1.0))));
                Timestamp timestamp = new Timestamp(GenerateUserUtil.randomDate("2020-03-21", "2022-01-20").getTime());
                ps.setTimestamp(6,timestamp);
                ps.setTimestamp(7,timestamp);
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }
    }

    /**
     *
     *
     * 插入订单明细
     * @throws SQLException
     */
    public void insertOrderDetails() throws SQLException{

        String orderDetails = "insert into order_detail " +
                "(order_id,sku_id) " +
                "values (?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(orderDetails);

            for (int i = 0; i <20000; i++) {
                ps.setInt(1, new Random().nextInt(15000) + 1);
                ps.setInt(2, new Random().nextInt(10000) + 1);
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }
    }


    /**
     * s删除冗余数据 保持order_detail 和 order_info 一致
     * @throws SQLException
     */
    public void deleteErrorDataFromOrderInfo() throws SQLException {
        String sql = "select oi.id as id from order_info oi left join  order_detail od on oi.id = od.order_id where order_id is null";

        String deleteSql = "delete from order_info where id =  ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ArrayList<Integer> orderIdList = new ArrayList<>();

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                Integer orderId = resultSet.getInt("id");
                orderIdList.add(orderId);
            }

            System.out.println("------"+orderIdList.size());

            ps = connection.prepareStatement(deleteSql);

            for (int i = 0; i < orderIdList.size(); i++) {
                Integer orderId = orderIdList.get(i);
                ps.setInt(1,orderId);
                ps.addBatch();
                if (i%500==0){
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }
    }


    /**
     * s删除冗余数据 sku_info 和 保持order_detail 一致
     * @throws SQLException
     */
    public void deleteErrorDataFromSkuInfo() throws SQLException {
        String sql = "select si.id as id from sku_info si left join order_detail od on si.id = sku_id where order_id is null;";

        String deleteSql = "delete from sku_info where id =  ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ArrayList<Integer> orderIdList = new ArrayList<>();

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                Integer orderId = resultSet.getInt("id");
                orderIdList.add(orderId);
            }

            System.out.println("------"+orderIdList.size());

            ps = connection.prepareStatement(deleteSql);

            for (int i = 0; i < orderIdList.size(); i++) {
                Integer orderId = orderIdList.get(i);
                ps.setInt(1,orderId);
                ps.addBatch();
                if (i%500==0){
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }
    }


    /**
     * 修复订单明细价格
     * @throws SQLException
     */
    public void fixOrderDetailPrice() throws SQLException{


        String sql = "select distinct  sku_id, price  from sku_info si left join order_detail od on si.id = sku_id";

        String updateSql = "update order_detail set sku_price = ? where sku_id = ?";

        ArrayList<HashMap<Integer,Double>> objects = new ArrayList<>();
        try {
            Connection connection = getConnection(url, user, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                HashMap<Integer, Double> hashMap = new HashMap<>();
                hashMap.put(resultSet.getInt("sku_id"),resultSet.getDouble("price"));
                objects.add(hashMap);
            }

            ps = connection.prepareStatement(updateSql);
            for (int i = 0; i < objects.size(); i++) {
                HashMap<Integer, Double> integerDoubleHashMap = objects.get(i);
                for (Map.Entry<Integer, Double> entry : integerDoubleHashMap.entrySet()) {
                    ps.setDouble(1,entry.getValue());
                    ps.setInt(2,entry.getKey());
                    ps.addBatch();
                    if (i%500==0){
                        ps.executeBatch();
                        ps.clearBatch();
                    }
                }
            }
            ps.executeBatch();
            ps.clearBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复订单明细创建时间
     * @throws SQLException
     */
    public void fixOrderDetailCreateTime() throws SQLException{

        String sql = "select  distinct  oi.id,oi.create_time from order_info oi left join  order_detail od on oi.id=od.order_id";

        String updateSql = "update order_detail set create_time = ? where order_id = ?";

        ArrayList<HashMap<Integer, Timestamp>> hashMaps = new ArrayList<>();
        try {
            Connection connection = getConnection(url, user, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                HashMap<Integer, Timestamp> hashMap = new HashMap<>();
                hashMap.put(resultSet.getInt("id"),resultSet.getTimestamp("create_time"));
                hashMaps.add(hashMap);
            }

            ps = connection.prepareStatement(updateSql);
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<Integer, Timestamp> integerDoubleHashMap = hashMaps.get(i);
                for (Map.Entry<Integer, Timestamp> entry : integerDoubleHashMap.entrySet()) {
                    ps.setTimestamp(1,entry.getValue());
                    ps.setInt(2,entry.getKey());
                    ps.addBatch();
                    if (i%500==0){
                        ps.executeBatch();
                        ps.clearBatch();
                    }
                }
            }
            ps.executeBatch();
            ps.clearBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fixOrderInfoTotalPrice() throws SQLException{

        String sql = "select order_id ,round(sum(sku_price),2) as total_price from order_detail group by order_id";

        String updateSql = "update order_info set final_total_amount = ? where id = ?";

        ArrayList<HashMap<Integer, Double>> hashMaps = new ArrayList<>();
        try {
            Connection connection = getConnection(url, user, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                HashMap<Integer, Double> hashMap = new HashMap<>();
                hashMap.put(resultSet.getInt("order_id"),resultSet.getDouble("total_price"));
                hashMaps.add(hashMap);
            }
            ps = connection.prepareStatement(updateSql);
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<Integer, Double> integerDoubleHashMap = hashMaps.get(i);
                for (Map.Entry<Integer, Double> entry : integerDoubleHashMap.entrySet()) {
                    ps.setDouble(1,entry.getValue());
                    ps.setInt(2,entry.getKey());
                    ps.addBatch();
                    if (i%500==0){
                        ps.executeBatch();
                        ps.clearBatch();
                    }
                }
            }
            ps.executeBatch();
            ps.clearBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) throws SQLException {


        InsertData2Mysql insertData2Mysql = new InsertData2Mysql();

//        insertData2Mysql.insertProvinceCity();
//        insertData2Mysql.insertUser();
//        insertData2Mysql.insertSku();
        insertData2Mysql.fixOrderInfoTotalPrice();
    }
}
