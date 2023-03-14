package cn.wr.mysql;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static cn.wr.mysql.GenerateUserUtil.sexs;

/**
 * @author : WangRui
 * @date : 2023/3/10
 */

public class Insert2Mysql {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final Logger logger = LoggerFactory.getLogger(Insert2Mysql.class);

    private static final String url = "jdbc:mysql://localhost:3306/cstor_store?useSSL=false";
    private static final String user = "root";
    private static final String password = "12345678";


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
     * 创建模拟用户
     *
     * @return
     */
    public List<User> createUsers() {

        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 1050; i++) {
            User user = new User();
            user.setUserId(DigestUtils.md5Hex("userid" + i));
            user.setUserName(GenerateUserUtil.getChineseName());
            user.setGender(sexs[GenerateUserUtil.getRandom(sexs.length)]);
            user.setAge(new Random().nextInt(30) + 18);
            String[] strings = GenerateUserUtil.place[GenerateUserUtil.getRandom(GenerateUserUtil.place.length)];
            user.setAddress(strings[0] + strings[GenerateUserUtil.getRandom(strings.length)]);
            user.setPhone(GenerateUserUtil.getTel());
            user.setEmail(GenerateUserUtil.getEmail(4, 10));
            user.setCreateTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-15", "2022-10-20").getTime()));

            users.add(user);
        }

        return users;
    }

    /**
     * 将用户插入数据库
     *
     * @param userList
     * @throws SQLException
     */
    public void InsertUser(List<User> userList) throws SQLException {

        // DigestUtils.md5Hex(str);

        String insertUser = "insert into users " +
                "(user_id,user_name,gender,age,address,phone,email,create_time) " +
                "values (?,?,?,?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertUser);

            for (int i = 0; i < userList.size(); i++) {
                User user = userList.get(i);
                ps.setString(1, user.getUserId());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getGender());
                ps.setInt(4, user.getAge());
                ps.setString(5, user.getAddress());
                ps.setString(6, user.getPhone());
                ps.setString(7, user.getEmail());
                ps.setTimestamp(8, user.getCreateTime());
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
     * 创建模拟商品数据
     *
     * @return
     */
    public List<Item> createItems() {

        ArrayList<Item> items = new ArrayList<>();
        Item time1 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 0)).category(1).itemName("ipad pro 11").price(new BigDecimal(6699.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time2 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 1)).category(1).itemName("magic mouse").price(new BigDecimal(1046.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time3 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 2)).category(1).itemName("sandisk-1TB硬盘").price(new BigDecimal(949.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time4 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 3)).category(1).itemName("绿联type-c扩展坞").price(new BigDecimal(289.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time5 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 4)).category(1).itemName("飞利浦4k显示器").price(new BigDecimal(1598.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time6 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 5)).category(1).itemName("ipad pro 12.9").price(new BigDecimal(8500.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time7 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 6)).category(1).itemName("倍思Type-c扩展坞").price(new BigDecimal(389.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time8 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 7)).category(1).itemName("ipad pro 10.9").price(new BigDecimal(4147.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time9 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 8)).category(1).itemName("MacBook Pro 14英寸").price(new BigDecimal(15999.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time10 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 9)).category(1).itemName("外星人无线鼠标").price(new BigDecimal(999.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time11 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 10)).category(1).itemName("MacBook Air 13.6英寸").price(new BigDecimal(10897.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time12 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 11)).category(2).itemName("iphone14 pro").price(new BigDecimal(7099.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time13 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 12)).category(2).itemName("iphone13 pro max").price(new BigDecimal(8299.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time14 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 13)).category(2).itemName("华为50Plus").price(new BigDecimal(1698.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time15 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 14)).category(2).itemName("vivo X80").price(new BigDecimal(2549.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time16 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 15)).category(2).itemName("vivo S16Pro").price(new BigDecimal(3599.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time17 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 16)).category(2).itemName("华为Mate30 Pro").price(new BigDecimal(3499.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time18 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 17)).category(2).itemName("华为Mate30 Pro").price(new BigDecimal(3899.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time19 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 18)).category(2).itemName("vivo iQOO 11 pro").price(new BigDecimal(5499.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time20 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 19)).category(2).itemName("荣耀X40").price(new BigDecimal(1639.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time21 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 20)).category(2).itemName("荣耀Magic5 Pro").price(new BigDecimal(5199.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time22 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 21)).category(3).itemName("酸辣粉120g").price(new BigDecimal(12.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time23 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 22)).category(3).itemName("黄桃罐头6罐").price(new BigDecimal(52.90))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time24 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 23)).category(3).itemName("千页豆腐20包").price(new BigDecimal(15.90))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time25 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 24)).category(3).itemName("卫龙魔芋结10包").price(new BigDecimal(18.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time26 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 25)).category(3).itemName("原味瓜子").price(new BigDecimal(11.80))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time27 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 26)).category(3).itemName("蜜汁山核桃").price(new BigDecimal(15.92))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time28 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 27)).category(3).itemName("鸡蛋卷蛋酥").price(new BigDecimal(16.90))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time29 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 28)).category(3).itemName("海苔碎1罐").price(new BigDecimal(18.80))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time30 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 29)).category(3).itemName("葡式蛋挞皮").price(new BigDecimal(21.80))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time31 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 30)).category(3).itemName("鸡胸肉肠10根").price(new BigDecimal(19.90))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time32 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 31)).category(4).itemName("红蜻蜓男士皮鞋").price(new BigDecimal(106.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time33 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 32)).category(4).itemName("回力2023运动鞋").price(new BigDecimal(89.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time34 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 33)).category(4).itemName("回力春夏运动鞋").price(new BigDecimal(79.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time35 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 34)).category(4).itemName("骆驼户外登山鞋").price(new BigDecimal(213.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time36 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 35)).category(4).itemName("骆驼商务休闲鞋").price(new BigDecimal(299.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time37 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 36)).category(4).itemName("骆驼复古板鞋").price(new BigDecimal(269.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time38 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 37)).category(4).itemName("百丽牛皮正装鞋").price(new BigDecimal(629.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time39 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 38)).category(4).itemName("花花公子阿甘男鞋").price(new BigDecimal(192.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time40 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 39)).category(4).itemName("花花公子休闲男鞋").price(new BigDecimal(198.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();
        Item time41 = Item.builder().itemId(DigestUtils.md5Hex("itemid" + 40)).category(4).itemName("耐克运动男鞋").price(new BigDecimal(698.00))
                .createTime(new Timestamp(GenerateUserUtil.randomDate("2022-09-10", "2022-09-12").getTime())).build();

        items.add(time1);
        items.add(time2);
        items.add(time3);
        items.add(time4);
        items.add(time5);
        items.add(time6);
        items.add(time7);
        items.add(time8);
        items.add(time9);
        items.add(time10);
        items.add(time11);
        items.add(time12);
        items.add(time13);
        items.add(time14);
        items.add(time15);
        items.add(time16);
        items.add(time17);
        items.add(time18);
        items.add(time19);
        items.add(time20);
        items.add(time21);
        items.add(time22);
        items.add(time23);
        items.add(time24);
        items.add(time25);
        items.add(time26);
        items.add(time27);
        items.add(time28);
        items.add(time29);
        items.add(time30);
        items.add(time31);
        items.add(time32);
        items.add(time33);
        items.add(time34);
        items.add(time35);
        items.add(time36);
        items.add(time37);
        items.add(time38);
        items.add(time39);
        items.add(time40);
        items.add(time41);
        return items;
    }

    /**
     * 构建商品数据
     * @param itemList
     * @throws SQLException
     */
    public void InsertItem(List<Item> itemList) throws SQLException {


        String insertItem = "insert into items " +
                "(item_id,item_name,category,price,create_time) " +
                "values (?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertItem);

            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                ps.setString(1,item.getItemId() );
                ps.setString(2,item.getItemName());
                ps.setInt(3,item.getCategory());
                ps.setBigDecimal(4,item.getPrice());
                ps.setTimestamp(5,item.getCreateTime());

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
           ps.close();
           connection.close();
        }
    }


    /**
     * 构建订单明细表数据
     * @return
     */
    public List<OrderDetails> createOrderDetails(){

        ArrayList<OrderDetails> orderDetails = new ArrayList<>();
        for (int i = 0; i < 4051; i++) {
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrderId(DigestUtils.md5Hex("order_id" + new Random().nextInt(2000)));
            orderDetail.setSubOrderId(DigestUtils.md5Hex("sub_order_id" + i));
            orderDetail.setItemId(DigestUtils.md5Hex("itemid" + new Random().nextInt(41) ));
            orderDetail.setSubOrderStatus(new Random().nextInt(2));
            orderDetail.setOrderTime(new Timestamp(GenerateUserUtil.randomDate("2022-10-21", "2023-01-20").getTime()));

            orderDetails.add(orderDetail);
        }

        return orderDetails;

    }


    /**
     *  构建订单明细数据
     * @param orderDetailsList
     * @throws SQLException
     */
    public void insertOrderDetails(List<OrderDetails> orderDetailsList)throws SQLException{

        String insertItem = "insert into orders_details " +
                "(order_id,sub_order_id,item_id,sub_order_status,order_time) " +
                "values (?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertItem);

            for (int i = 0; i < orderDetailsList.size(); i++) {

                OrderDetails item = orderDetailsList.get(i);

                ps.setString(1,item.getOrderId());
                ps.setString(2,item.getSubOrderId());
                ps.setString(3,item.getItemId());
                ps.setInt(4,item.getSubOrderStatus());
                ps.setTimestamp(5,item.getOrderTime());

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
            ps.close();
            connection.close();
        }

    }


    /**
     * 基于 items 和 order_details表关联数据更新正确的 order_details.item_amount值
     */
    public void insertItemPrice2OrderDetailsBasedOnRel() throws SQLException{

        String selectQuery = "select\n" +
                "b.sub_order_id as sub_order_id,\n" +
                "a.price as price\n" +
                "from items a left join orders_details b\n" +
                "on a.item_id = b.item_id";

        String updateOrderDetailsAmount = "update orders_details set item_amount=? where sub_order_id=? ";


        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(selectQuery);
            ArrayList<Tuple2<String,BigDecimal>> maps = new ArrayList<>();
            // Tuple2<Object, Object> of = Tuple2.of(resultSet.getString("sub_order_id"),resultSet.getBigDecimal("price"));
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                Tuple2<String, BigDecimal> of = Tuple2.of(resultSet.getString("sub_order_id"),resultSet.getBigDecimal("price"));
                maps.add(of);
            }

            // 批量更新
            ps = connection.prepareStatement(updateOrderDetailsAmount);
            System.out.println("----"+maps.size());
            for (int i = 0; i < maps.size(); i++) {
                Tuple2<String, BigDecimal> stringBigDecimalTuple2 = maps.get(i);
                ps.setBigDecimal(1,stringBigDecimalTuple2.f1);
                ps.setString(2,stringBigDecimalTuple2.f0);

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
            ps.close();
            connection.close();
        }


    }


    /**
     * 构建原始未修正的订单数据
     * @return
     */
    public List<Order> createOrders(){

        ArrayList<Order> orderDetails = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {

            Order order = new Order();

            order.setOrderId(DigestUtils.md5Hex("order_id" + i));
            order.setUserId(DigestUtils.md5Hex("userid"+new Random().nextInt(1050)));
            order.setOrderStatus(0);
            order.setOrderAmount(new BigDecimal("0.00"));
            order.setOrderTime(new Timestamp(GenerateUserUtil.randomDate("2022-10-21", "2023-01-20").getTime()));

            orderDetails.add(order);
        }

        return orderDetails;
    }

    /**
     * 插入原始未修正的订单数据量
     * @param orderList
     */
    public void insertOrders(List<Order> orderList) throws SQLException {

        String insertOrders = "insert into orders (order_id,user_id,order_status,order_amount,order_time) values (?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertOrders);

            for (int i = 0; i < orderList.size(); i++) {

                Order order = orderList.get(i);

                ps.setString(1,order.getOrderId());
                ps.setString(2,order.getUserId());
                ps.setInt(3,order.getOrderStatus());
                ps.setBigDecimal(4,order.getOrderAmount());
                ps.setTimestamp(5,order.getOrderTime());

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
            ps.close();
            connection.close();
        }
    }

    /**
     * 基于order_details中的order_id删除 orders中多余的order_id
     */
    public void deleteOrderIdByOrderDetailsAnd() throws SQLException{

        String queryOrderId = "select order_id\n" +
                "from (select o.order_id\n" +
                "      from orders o\n" +
                "               left join orders_details od on o.order_id = od.order_id\n" +
                "      where od.order_id is null) t;";


        String deleteDupOrderId = "delete from orders where order_id = ?";

        Connection connection = null;
        PreparedStatement ps = null;
        ArrayList<String> orderIdList = new ArrayList<>();

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(queryOrderId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String orderId = resultSet.getString("order_id");
                orderIdList.add(orderId);
            }

            System.out.println("------"+orderIdList.size());

            ps = connection.prepareStatement(deleteDupOrderId);

            for (int i = 0; i < orderIdList.size(); i++) {
                String orderId = orderIdList.get(i);
                ps.setString(1,orderId);
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
            ps.close();
            connection.close();
        }


    }


    /**
     * 修正订单表中的状态和总金额
     */
    public void fixOrdersStatusAndAmount() throws SQLException{

        String queryCorrectData= "select order_id,\n" +
                "       user_id,\n" +
                "       if(su = cn, 1, 0) as order_status,\n" +
                "       order_amount,\n" +
                "       order_time\n" +
                "from (select t.user_id,\n" +
                "             t.order_id,\n" +
                "             order_time,\n" +
                "             sum(t.sub_order_status) as su,\n" +
                "             sum(t.item_amount)      as order_amount,\n" +
                "             count(user_id)             cn\n" +
                "      from (select o.user_id,\n" +
                "                   o.order_id,\n" +
                "                   o.order_time,\n" +
                "                   od.sub_order_status,\n" +
                "                   od.item_amount\n" +
                "            from orders o\n" +
                "                     left join orders_details od on o.order_id = od.order_id) t\n" +
                "      group by user_id,\n" +
                "               order_time,\n" +
                "               order_id) t1";

        // sql 更新语法不熟练 set后面直接逗号 而不是 and
        String fixOrdersData = "update orders set order_status=?, order_amount=?, order_time=? " +
                "where  order_id =? ";

        Connection connection = null;
        PreparedStatement ps = null;

        ArrayList<Order> orderList = new ArrayList<>();

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(queryCorrectData);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            ObjectMapper objectMapper = new ObjectMapper();
            while (rs.next()){
                HashMap<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                  rowData.put(metaData.getColumnLabel(i),rs.getObject(i));
                }
                orderList.add(objectMapper.convertValue(rowData, Order.class));

            }
            System.out.println("--------"+orderList.size());


            ps = connection.prepareStatement(fixOrdersData);
            for (int i = 0; i < orderList.size(); i++) {

                Order order = orderList.get(i);
                ps.setInt(1,order.getOrderStatus());
                ps.setBigDecimal(2,order.getOrderAmount());
                ps.setTimestamp(3,order.getOrderTime());
                ps.setString(4,order.getOrderId());

                // 批量更新
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
            ps.close();
            connection.close();
        }

    }

    /**
     * 修正订单明细表中的下单日期 保持和主订单表下单时间一致
     * @throws SQLException
     */
    public void fixOrderDetailsOrderTime() throws SQLException{

        String queryOrdersGetOrderTime = "select\n" +
                "       od.sub_order_id as sub_order_id,\n" +
                "       o.order_time as order_time\n" +
                "from orders o\n" +
                "         left join orders_details od on o.order_id = od.order_id;";

        String updateOrderDetails= "update orders_details set order_time=? where sub_order_id=?";

        Connection connection = null;
        PreparedStatement ps = null;
        ArrayList<Tuple2<String,Timestamp>> orderDetailsList = new ArrayList<>();

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(queryOrdersGetOrderTime);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String orderId = resultSet.getString("sub_order_id");
                Timestamp orderTime = resultSet.getTimestamp("order_time");
                Tuple2<String, Timestamp> of = Tuple2.of(orderId, orderTime);
                orderDetailsList.add(of);
            }

            System.out.println("------"+orderDetailsList.size());

            //done 将获取到的正确的下单时间更新到 订单明细表
            ps = connection.prepareStatement(updateOrderDetails);
            for (int i = 0; i < orderDetailsList.size(); i++) {
                Tuple2<String, Timestamp> stringTimestampTuple2 = orderDetailsList.get(i);
                ps.setTimestamp(1,stringTimestampTuple2.f1);
                ps.setString(2,stringTimestampTuple2.f0);
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
            ps.close();
            connection.close();
        }



    }

    /**
     * 通过订单主表，订单状态完成的数据 存入payment表中
     * @throws SQLException
     */
    public void insertPaymentByOrders() throws SQLException{

        String queryFinishedOrders = "select order_id,order_amount,order_time\n" +
                "from orders where order_status=1;";

        String insertPayment = "insert into payments (payment_id,order_id,payment_time,payment_method,pay_amount) " +
                "values(?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;
        String[] randomPaymentMethod ={"Wechatpay","Alipay"};

        ArrayList<Tuple3<String, BigDecimal, Timestamp>> orderList = new ArrayList<>();

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(queryFinishedOrders);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String orderId = resultSet.getString("order_id");
                BigDecimal orderAmount = resultSet.getBigDecimal("order_amount");
                Timestamp orderTime = resultSet.getTimestamp("order_time");
                Tuple3<String,BigDecimal, Timestamp> of = Tuple3.of(orderId,orderAmount, orderTime);
                orderList.add(of);
            }

            System.out.println("------"+orderList.size());

//            Tuple3<String, BigDecimal, Timestamp> stringBigDecimalTimestampTuple3 = orderList.get(1);
//            Timestamp f2 = stringBigDecimalTimestampTuple3.f2;
//            long time = stringBigDecimalTimestampTuple3.f2.getTime();
//            time = time+10000L;
//            Timestamp timestamp = new Timestamp(time);
//            System.out.println(time);

//            for (int i = 0; i < 100; i++) {
//                System.out.println(randomPaymentMethod[new Random().nextInt(randomPaymentMethod.length)]);
//            }


            ps = connection.prepareStatement(insertPayment);
            for (int i = 0; i < orderList.size(); i++) {
                Tuple3<String, BigDecimal, Timestamp> orders = orderList.get(i);

                ps.setString(1,DigestUtils.md5Hex("payment_id"+i));
                ps.setString(2,orders.f0);
                ps.setTimestamp(3,new Timestamp(orders.f2.getTime()+10000L));
                ps.setString(4,randomPaymentMethod[new Random().nextInt(randomPaymentMethod.length)]);
                ps.setBigDecimal(5,orders.f1);
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
            ps.close();
            connection.close();
        }



    }

    /**
     * 通过支付完成表数据 添加物流表信息
     */
    public void insertLogistics() throws SQLException{

        String queryPaymentOrderId="select order_id,payment_time from payments;";

        String insertLogistics =  "insert into logistics (logistics_id,order_id,status,update_time) " +
                "values (?,?,?,?)";

        Connection connection = null;
        PreparedStatement ps = null;

        ArrayList<Tuple2<String,Timestamp>> paymentList = new ArrayList<>();
        long[] randTime={20000000L,50000000L,60000000L,80000000L,100000000L,150000000L,200000000L,250000000L,300000000L,350000000L};

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(queryPaymentOrderId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String orderId = resultSet.getString("order_id");
                Timestamp paymentTime = resultSet.getTimestamp("payment_time");
                Tuple2<String,Timestamp> of = Tuple2.of(orderId,paymentTime);
                paymentList.add(of);
            }
            System.out.println("------"+paymentList.size());


            ps = connection.prepareStatement(insertLogistics);
            for (int i = 0; i < paymentList.size(); i++) {
                Tuple2<String, Timestamp> payment = paymentList.get(i);

                ps.setString(1,DigestUtils.md5Hex("logistics_id"+i));
                ps.setString(2,payment.f0);
                int status = new Random().nextInt(2);
                ps.setInt(3,status); // [)->左闭又开

                if (status==0){
                    // 未发货 则发货时间和支付时间一致
                    ps.setTimestamp(4,payment.f1);
                }else {
                    // 如果状态时已发货，需要设置发货时间
                    ps.setTimestamp(4,new Timestamp(payment.f1.getTime()+randTime[new Random().nextInt(randTime.length)]));
                }
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
            ps.close();
            connection.close();
        }
    }


    public static void main(String[] args) throws SQLException {
        String a = DigestUtils.md5Hex("userid" + 1);
        //d7ba9fa7634764f2fd5bb81e8183ce18
        System.out.println(a);


        Insert2Mysql insert2Mysql = new Insert2Mysql();

        // 订单明细数据
//        List<OrderDetails> orderDetails = insert2Mysql.createOrderDetails();
//        insert2Mysql.insertOrderDetails(orderDetails);
//
//
        // 商品信息数据
//        List<Item> items = insert2Mysql.createItems();
//        insert2Mysql.InsertItem(items);
//
//        // 用户数据
//        List<User> users = insert2Mysql.createUsers();
//        insert2Mysql.InsertUser(users);

        //  基于item 和 orderdetails 修正 item_amount
//        insert2Mysql.insertItemPrice2OrderDetailsBasedOnRel();

        // 主订单原始未修正数据
//        List<Order> orders = insert2Mysql.createOrders();
//        insert2Mysql.insertOrders(orders);

        // 删除主订单不合理数据
//        insert2Mysql.deleteOrderIdByOrderDetailsAnd();

        // 修正orders 主订单表的数据
//        insert2Mysql.fixOrdersStatusAndAmount();

        // 修正子订单表中的下单时间
//        insert2Mysql.fixOrderDetailsOrderTime();

        // 基于订单主表插入支付完成表数据
//        insert2Mysql.insertPaymentByOrders();

        // 基于支付完成表插入物流表数据
//        insert2Mysql.insertLogistics();

        System.out.println("d");




    }


}
