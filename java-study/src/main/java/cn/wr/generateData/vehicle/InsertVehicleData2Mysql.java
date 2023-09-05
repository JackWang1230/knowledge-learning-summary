package cn.wr.generateData.vehicle;

import cn.wr.generateData.eCommerce.GenerateUserUtil;
import cn.wr.generateData.eCommerce.Insert2Mysql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @author : WangRui
 * @date : 2023/8/8
 */

public class InsertVehicleData2Mysql {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final Logger logger = LoggerFactory.getLogger(Insert2Mysql.class);

    private static final String url = "jdbc:mysql://localhost:3306/vehicle_store?useSSL=false";
    private static final String user = "root";
    private static final String password = "12345678";


    public static final String[][] BrandAndModel = {
            {"丰田", "卡罗拉", "雷凌", "凯美瑞", "威驰", "普拉多", "兰德酷路泽"},
            {"本田", "飞度", "雅阁", "思域", "缤智", "冠道", "奥德赛"},
            {"奥迪", "A3", "A4", "A6", "Q3", "Q5", "Q7"},
            {"大众", "高尔夫", "帕萨特", "途观", "速腾", "朗逸", "蔚揽"},
            {"宝马", "3系", "5系", "X3", "X5", "7系"},
            {"奔驰", "A级", "C级", "E级", "GLC", "GLE", "S级"},
            {"福特", "福克斯", "蒙迪欧", "翼虎", "探险者", "F-150", "探险者"},
            {"日产", "轩逸", "骊威", "逍客", "奇骏", "骐达", "蓝鸟"},
            {"雪佛兰", "科鲁兹", "迈锐宝", "科帕奇", "科尔维特"},
            {"雷克萨斯", "ES", "IS", "NX", "RX", "GX", "LS"},
            {"路虎", "揽胜", "发现", "揽运", "揽途", "极光", "发现神行"}
    };

    public static String brandMappingFuelType(String brand) {

        String fuelType = null;
        switch (brand) {
            case "丰田":
            case "本田":
            case "大众":
            case "福特":
            case "日产":
            case "雪佛兰":
                fuelType = "92#";
                break;
            case "奥迪":
            case "宝马":
            case "奔驰":
            case "雷克萨斯":
            case "路虎":
                fuelType = "95#";
                break;
        }
        return fuelType;

    }


    public static HashMap<String, String> provinceMaps() {

        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("北", "京A");
        objectObjectHashMap.put("上", "沪A");
        objectObjectHashMap.put("广", "粤A");
        objectObjectHashMap.put("深", "粤B");
        objectObjectHashMap.put("成", "川A");
        objectObjectHashMap.put("重", "渝A");
        objectObjectHashMap.put("武", "鄂A");
        objectObjectHashMap.put("南", "苏A");
        objectObjectHashMap.put("西", "陕A");
        objectObjectHashMap.put("杭", "浙A");
        objectObjectHashMap.put("苏", "苏E");
        objectObjectHashMap.put("天", "津A");
        objectObjectHashMap.put("青", "鲁B");
        objectObjectHashMap.put("大", "辽B");
        objectObjectHashMap.put("沈", "辽A");
        objectObjectHashMap.put("长", "吉A");
        objectObjectHashMap.put("济", "鲁A");
        objectObjectHashMap.put("哈", "黑A");
        objectObjectHashMap.put("昆", "云A");
        objectObjectHashMap.put("兰", "鲁A");
        objectObjectHashMap.put("银", "宁A");
        objectObjectHashMap.put("乌", "新A");
        objectObjectHashMap.put("拉", "藏A");

        return objectObjectHashMap;

    }

    ;

    public static final String[] engineStatus = {"启动", "熄火", "启动", "启动", "启动"};
    public static final String[] locations = {
            "北京市朝阳区东直门",
            "北京市海淀区中关村",
            "北京市西城区金融街",
            "北京市丰台区方庄",
            "北京市通州区新华大街",
            "上海市黄浦区南京东路",
            "上海市徐汇区复兴西路",
            "上海市浦东新区陆家嘴",
            "广州市天河区珠江新城",
            "深圳市福田区华强北",
            "北京市通州区新华大街",
            "上海市黄浦区南京东路",
            "上海市徐汇区复兴西路",
            "上海市浦东新区陆家嘴",
            "广州市天河区珠江新城",
            "成都市锦江区春熙路",
            "重庆市渝中区解放碑",
            "武汉市江汉区江汉路",
            "南京市玄武区夫子庙",
            "西安市莲湖区钟楼",
            "杭州市上城区西湖",
            "苏州市姑苏区平江路",
            "天津市和平区意大利风情区",
            "青岛市市南区五四广场",
            "大连市中山区星海广场",
            "沈阳市和平区中街",
            "长春市朝阳区南湖公园",
            "济南市历下区千佛山",
            "哈尔滨市道里区中央大街",
            "昆明市五华区滇池",
            "兰州市城关区中山桥",
            "银川市兴庆区沙湖",
            "乌鲁木齐市天山区红山公园",
            "拉萨市城关区布达拉宫",
            "南京市秦淮区夫子庙",
            "南京市鼓楼区中山陵",
            "南京市雨花台区明故宫",
            "南京市江宁区将军山",
            "南京市浦口区汤山",
            "南京市栖霞区鸡鸣寺",
            "南京市六合区雄州",
            "南京市建邺区双塘",
            "南京市溧水区天目湖",
            "南京市高淳区淳化",
            "南京市玄武区中山陵",
            "南京市秦淮区中华门",
            "南京市鼓楼区中山陵",
            "南京市雨花台区明故宫",
            "南京市江宁区将军山",
            "南京市浦口区汤山",
            "南京市栖霞区鸡鸣寺",
            "南京市六合区雄州",
            "南京市建邺区双塘",
            "南京市溧水区天目湖",
            "南京市高淳区淳化",
            "上海市青浦区盈港东路",
            "广州市番禺区长隆欢乐世界",
            "深圳市南山区蛇口工业区",
            "成都市武侯区锦绣路",
            "重庆市九龙坡区巴南区",
            "武汉市江岸区江汉路步行街",
            "南京市秦淮区中华路步行街",
            "西安市碑林区大雁塔",
            "杭州市西湖区断桥残雪",
            "苏州市园区观前街",
            "青岛市崂山区崂山",
            "大连市甘井子区星海广场",
            "沈阳市沈河区中街",
            "长春市朝阳区南湖公园",
            "济南市历下区千佛山",
            "哈尔滨市松北区冰雪大世界",
            "昆明市五华区滇池",
            "兰州市城关区白塔山",
            "银川市兴庆区鼓楼大街",
            "乌鲁木齐市天山区红山公园"
    };

    /**
     * 基于不同的时间区间模拟不同的公里数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static double getDistance(long startTime, long endTime) {

        long diffTime = endTime - startTime;
        if (diffTime <= 3600000L) {

            return getRandomInRange(0, 100) + getDoubleRandom();

        } else if (diffTime <= 7200000L) {

            return getRandomInRange(50, 200) + getDoubleRandom();

        } else if (diffTime <= 10800000L) {

            return getRandomInRange(100, 300) + getDoubleRandom();

        } else if (diffTime <= 14400000L) {

            return getRandomInRange(150, 400) + getDoubleRandom();

        } else if (diffTime <= 18000000L) {

            return getRandomInRange(250, 500) + getDoubleRandom();

        } else if (diffTime <= 21600000L) {

            return getRandomInRange(300, 600) + getDoubleRandom();

        } else if (diffTime <= 28800000L) {

            return getRandomInRange(400, 700) + getDoubleRandom();

        } else if (diffTime <= 32400000L) {

            return getRandomInRange(500, 800) + getDoubleRandom();

        } else if (diffTime <= 36000000L) {

            return getRandomInRange(500, 850) + getDoubleRandom();

        } else if (diffTime <= 39600000L) {

            return getRandomInRange(600, 950) + 1 + getDoubleRandom();

        } else if (diffTime <= 43200000L) {

            return getRandomInRange(650, 1000) + getDoubleRandom();

        } else if (diffTime <= 46800000L) {

            return getRandomInRange(700, 1050) + getDoubleRandom();

        } else if (diffTime <= 50400000L) {

            return getRandomInRange(700, 1100) + getDoubleRandom();

        } else if (diffTime <= 54000000L) {

            return getRandomInRange(700, 1100) + getDoubleRandom();

        } else if (diffTime <= 57600000L) {

            return getRandomInRange(700, 1150) + getDoubleRandom();

        } else if (diffTime <= 61200000L) {

            return getRandomInRange(780, 1150) + getDoubleRandom();

        } else if (diffTime <= 64800000L) {

            return getRandomInRange(800, 1200) + getDoubleRandom();

        } else if (diffTime <= 68400000L) {

            return getRandomInRange(850, 1250) + getDoubleRandom();

        } else if (diffTime <= 72000000L) {

            return getRandomInRange(850, 1300) + getDoubleRandom();

        } else if (diffTime <= 75600000L) {

            return getRandomInRange(850, 1310) + getDoubleRandom();

        } else if (diffTime <= 79200000L) {

            return getRandomInRange(850, 1340) + getDoubleRandom();

        } else if (diffTime <= 82800000L) {

            return getRandomInRange(850, 1400) + getDoubleRandom();

        } else {
            return getRandomInRange(860, 1450) + getDoubleRandom();
        }

    }




    /**
     * 基于不同的行程距离获取对应的燃油消耗量
     * @param distance
     * @return
     */
    public static double getFuelAmount(Double distance){

        if (distance<1){
            return getDoubleRandom();
        }else if(distance <5){
            return getRandomInRange(0,1)+getDoubleRandom();
        }else if (distance<100){
            // `/10` 表示小数点左移一位
            // `%10` 表示 从除以10后的数中提取出个位数和十位数的部分
            int value = (int) (distance/10%10);
            return getRandomInRange(value,value+1)+getDoubleRandom();
        }else {
            int tenDigit= (int)(distance/10);
            int digit = tenDigit%10;
            return getRandomInRange(tenDigit-digit,tenDigit)+getDoubleRandom();
        }

    }



    /**
     * 保养类型描述映射
     *
     * @param value
     * @return
     */
    public static String mappingDesc(int value) {

        String maintenanceDesc = null;
        switch (value) {
            case 0:
                maintenanceDesc = "车辆保养";
                break;
            case 1:
                maintenanceDesc = "车辆故障维修";
                break;
            case 2:
                maintenanceDesc = "车辆预防性维修检测";
                break;
            case 3:
                maintenanceDesc = "车辆紧急维修";
                break;
            case 4:
                maintenanceDesc = "车辆车身损坏维修";
                break;
        }
        return maintenanceDesc;
    }


    /**
     * 基于名字生成12位字符串
     *
     * @param name
     * @return
     */
    public static String stringNameConvert(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(name.getBytes());
            // Convert the hash bytes to a positive BigInteger
            String value = String.valueOf(Math.abs(new BigInteger(1, bytes).longValue()));
            return value.substring(0, Math.min(12, value.length()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获取连接
     *
     * @param url      url
     * @param user     user
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

    public static int getRandom(int length) {
        Random random = new Random();
        return random.nextInt(length);
    }

    /**
     * 获取0-1之间小数
     * @return
     */
    public static double getDoubleRandom() {
        Random random = new Random();
        return Math.round(random.nextDouble() * 100.0) / 100.0;
    }

    /**
     * 获取两个数之间的数字
     * @param min
     * @param max
     * @return
     */
    public static int getRandomInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static int getModelIndex(int length) {
        Random random = new Random();
        int value = random.nextInt(length);
        return value == 0 ? 1 : value;
    }

    /**
     * 创建模拟车辆
     *
     * @return List<Vehicle>
     */
    public List<Vehicle> createVehicle() {

        ArrayList<Vehicle> vehicles = new ArrayList<>();
        for (int i = 1; i <= 2000; i++) {
            int length = getRandom(BrandAndModel.length);
            String[] brandAndModels = BrandAndModel[length];
            String location = locations[getRandom(locations.length)];
            String preProvince = location.substring(0, 1);
            Vehicle vehicle = Vehicle.builder()
                    .vehicleId(i)
                    .brand(brandAndModels[0])
                    .model(brandAndModels[getModelIndex(brandAndModels.length)])
                    .licensePlate(provinceMaps().get(preProvince) + 10000 + i)
                    .location(location)
                    .fuelLevel(getRandom(4) + 1)
                    .engineStatus(engineStatus[getRandom(engineStatus.length)])
                    .build();
            vehicles.add(vehicle);
        }
        return vehicles;
    }

    /**
     * 插入车辆信息到数据库
     *
     * @param vehicleList vehicleList
     */
    public void insertVehicle(List<Vehicle> vehicleList) {

        String insertVehicle = "insert into " +
                "vehicle (vehicle_id,brand,model,license_plate,location,fuel_level,engine_status) " +
                "values (?,?,?,?,?,?,?)";
        Connection connection = null;
        PreparedStatement ps = null;

        connection = getConnection(url, user, password);

        try {
            ps = connection.prepareStatement(insertVehicle);

            for (int i = 0; i < vehicleList.size(); i++) {
                Vehicle vehicle = vehicleList.get(i);
                ps.setInt(1, vehicle.getVehicleId());
                ps.setString(2, vehicle.getBrand());
                ps.setString(3, vehicle.getModel());
                ps.setString(4, vehicle.getLicensePlate());
                ps.setString(5, vehicle.getLocation());
                ps.setInt(6, vehicle.getFuelLevel());
                ps.setString(7, vehicle.getEngineStatus());
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造告警模拟数据
     *
     * @return List
     */
    public List<Alert> createAlert() {
        ArrayList<Alert> alerts = new ArrayList<>();
        for (int i = 0; i < 10001; i++) {
            long time = GenerateUserUtil.randomDate("2023-01-10", "2023-09-20").getTime();
            Alert alert = Alert.builder()
                    .alertId(i)
                    .vehicleId(getRandom(2000) + 1)
                    .alertType(getRandom(6))
                    .timeStamp(time)
                    .createTime(new Timestamp(time))
                    .updateTime(new Timestamp(time))
                    .build();
            alerts.add(alert);
        }
        return alerts;
    }

    /**
     * 插入告警数据
     *
     * @param alertList alertList
     */
    public void insertAlert(List<Alert> alertList) {

        Connection connection = null;
        PreparedStatement ps = null;
        String insertAlert = "insert into alert (alert_id,vehicle_id,alert_type,time_stamp,create_time,update_time) values " +
                "(?,?,?,?,?,?)";

        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertAlert);
            for (int i = 0; i < alertList.size(); i++) {
                Alert alert = alertList.get(i);
                ps.setInt(1, alert.getAlertId());
                ps.setInt(2, alert.getVehicleId());
                ps.setInt(3, alert.getAlertType());
                ps.setLong(4, alert.getTimeStamp());
                ps.setTimestamp(5, alert.getCreateTime());
                ps.setTimestamp(6, alert.getUpdateTime());
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
        }

    }

    /**
     * 构建模拟司机用户信息
     *
     * @return list
     */
    public List<Driver> createDriver() {
        ArrayList<Driver> drivers = new ArrayList<>();

        for (int i = 0; i <= 1850; i++) {

            String chineseName = GenerateUserUtil.getChineseName();
            Driver driver = Driver.builder()
                    .driverId(i)
                    .name(chineseName)
                    .licenseNumber(stringNameConvert(chineseName))
                    .contactInfo(GenerateUserUtil.getTel())
                    .build();
            drivers.add(driver);
        }
        return drivers;
    }

    /**
     * 插入司机信息
     *
     * @param driverList
     */
    public void insertDriver(List<Driver> driverList) {

        Connection connection;
        PreparedStatement ps;
        String insertDriver = "insert into driver (driver_id,`name`,license_number,contact_info) values " +
                "(?,?,?,?)";
        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertDriver);
            for (int i = 0; i < driverList.size(); i++) {
                Driver driver = driverList.get(i);
                ps.setInt(1, driver.getDriverId());
                ps.setString(2, driver.getName());
                ps.setString(3, driver.getLicenseNumber());
                ps.setString(4, driver.getContactInfo());
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建模拟行程信息
     *
     * @return
     */
    public List<Trip> createTrip() {

        ArrayList<Trip> trips = new ArrayList<>();
        for (int i = 0; i < 25001; i++) {

            long starTime = GenerateUserUtil.randomDate("2023-01-10", "2023-09-20").getTime();
            long endTime = starTime + getRandom(86400000);
            int vehicleId = getRandom(2000) + 1;
            // 86400000
            Trip trip = Trip.builder()
                    .tripId(i)
                    .vehicleId(vehicleId)
                    .driverId(vehicleId > 1850 ? getRandom(1850) : vehicleId)
                    .startTime(new Timestamp(starTime))
                    .endTime(new Timestamp(endTime))
                    .distance(getDistance(starTime, endTime))
                    .createTime(new Timestamp(starTime))
                    .updateTime(new Timestamp(endTime))
                    .build();
            trips.add(trip);
        }
        return trips;
    }

    /**
     * 插入行程信息
     *
     * @param trips
     * @throws SQLException
     */
    public void insertTrip(List<Trip> trips) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        String insertTrip = "insert into trip (trip_id,vehicle_id,driver_id,start_time,end_time,distance,create_time,update_time) " +
                "values (?,?,?,?,?,?,?,?)";
        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertTrip);
            for (int i = 0; i < trips.size(); i++) {
                Trip trip = trips.get(i);
                ps.setInt(1, trip.getTripId());
                ps.setInt(2, trip.getVehicleId());
                ps.setInt(3, trip.getDriverId());
                ps.setTimestamp(4, trip.getStartTime());
                ps.setTimestamp(5, trip.getEndTime());
                ps.setDouble(6, trip.getDistance());
                ps.setTimestamp(7, trip.getCreateTime());
                ps.setTimestamp(8, trip.getUpdateTime());
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }

    }

    /**
     * 创建维护数据
     *
     * @return return
     */
    public List<Maintenance> createMaintenance() throws SQLException {


        ArrayList<Maintenance> maintenances = new ArrayList<>();
        for (int i = 0; i < 15000; i++) {

            long starTime = GenerateUserUtil.randomDate("2023-01-15", "2023-08-30").getTime();
            int maintenanceType = getRandom(5);
            Maintenance maintenance = Maintenance.builder()
                    .maintenanceId(i)
                    .vehicleId(getRandom(2000) + 1)
                    .maintenanceType(maintenanceType)
                    .description(mappingDesc(maintenanceType))
                    .createTime(new Timestamp(starTime))
                    .updateTime(new Timestamp(starTime))
                    .build();
            maintenances.add(maintenance);
        }
        return maintenances;
    }

    /**
     * 插入维护保养数据
     *
     * @param maintenances
     * @throws SQLException
     */
    public void insertMaintenance(List<Maintenance> maintenances) throws SQLException {

        Connection connection = null;
        PreparedStatement ps = null;
        String insertMaintenance = "insert into maintenance (maintenance_id,vehicle_id,maintenance_type,description,create_time,update_time) " +
                "values (?,?,?,?,?,?)";
        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertMaintenance);
            for (int i = 0; i < maintenances.size(); i++) {
                Maintenance maintenance = maintenances.get(i);
                ps.setInt(1, maintenance.getMaintenanceId());
                ps.setInt(2, maintenance.getVehicleId());
                ps.setInt(3, maintenance.getMaintenanceType());
                ps.setString(4, maintenance.getDescription());
                ps.setTimestamp(5, maintenance.getCreateTime());
                ps.setTimestamp(6, maintenance.getUpdateTime());
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }

    }


    /**
     * 燃油添加记录
     *
     * @return
     * @throws SQLException
     */
    @Deprecated
    public List<FuelConsumption> createFuelConsumption() throws SQLException {

        String queryBrand = "select brand from vehicle where vehicle_id= ?";

        ArrayList<FuelConsumption> fuelConsumptions = new ArrayList<>();


        for (int i = 0; i < 30000; i++) {
            Connection connection = null;
            PreparedStatement ps = null;
            String brand = null;
            try {

                long starTime = GenerateUserUtil.randomDate("2023-01-15", "2023-08-30").getTime();
                int vehicleId = getRandom(2000) + 1;
                connection = getConnection(url, user, password);
                ps = connection.prepareStatement(queryBrand);
                ps.setInt(1, vehicleId);

                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    brand = resultSet.getString(1);
                }
                String fuelType = brandMappingFuelType(brand);
                double fuelAmount = getRandomInRange(20, 70) + getDoubleRandom();
                double fuelFee = fuelType.equals("95#") ? BigDecimal.valueOf(8 * fuelAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                        : BigDecimal.valueOf(7.5 * fuelAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                FuelConsumption fuelConsumption = FuelConsumption.builder()
                        .fuelId(i)
                        .vehicleId(vehicleId)
                        .fuelType(fuelType)
                        .fuelAmount(fuelAmount)
                        .fuelFee(fuelFee)
                        .createTime(new Timestamp(starTime))
                        .updateTime(new Timestamp(starTime))
                        .build();
                fuelConsumptions.add(fuelConsumption);
            } catch (Exception e) {

            } finally {
                connection.close();
                ps.close();
            }
        }
        return fuelConsumptions;
    }

    /**
     * 燃油添加记录-绑定对应的行程
     * 表示 该行程使用的燃料以及费用等
     *
     * @return
     * @throws SQLException
     */
    public List<FuelConsumption> createFuelConsumptionV1() throws SQLException {

        String queryBrand = "select brand from vehicle where vehicle_id= ?";
        String queryRelInfo = "select trip_id, t.vehicle_id, distance, brand, t.update_time\n" +
                "from trip t\n" +
                "         left join vehicle v on t.vehicle_id = v.vehicle_id\n";

        ArrayList<FuelConsumption> fuelConsumptions = new ArrayList<>();
        ArrayList<FuelConsumeBase> fuelConsumeBases = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        Integer tripId = null;
        Integer vehicleId = null;
        Double distance = null;
        String brand = null;
        Timestamp updateTime = null;
        try {

            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(queryRelInfo);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                tripId = resultSet.getInt(1);
                vehicleId = resultSet.getInt(2);
                distance = resultSet.getDouble(3);
                brand = resultSet.getString(4);
                updateTime = resultSet.getTimestamp(5);
                FuelConsumeBase fuelConsumeBase = FuelConsumeBase.builder()
                        .tripId(tripId)
                        .vehicleId(vehicleId)
                        .distance(distance)
                        .brand(brand)
                        .updateTime(updateTime)
                        .build();
                fuelConsumeBases.add(fuelConsumeBase);

            }
            for (FuelConsumeBase fuelConsumeBase : fuelConsumeBases) {

                String fuelType = brandMappingFuelType(fuelConsumeBase.getBrand());
                double fuelAmount = getFuelAmount(fuelConsumeBase.getDistance());
                double fuelFee = fuelType.equals("95#") ? BigDecimal.valueOf(8 * fuelAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                        : BigDecimal.valueOf(7.5 * fuelAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                FuelConsumption fuelConsumption = FuelConsumption.builder()
                        .fuelId(fuelConsumeBase.getTripId())
                        .tripId(fuelConsumeBase.getTripId())
                        .vehicleId(fuelConsumeBase.getVehicleId())
                        .fuelType(fuelType)
                        .fuelAmount(fuelAmount)
                        .fuelFee(fuelFee)
                        .createTime(new Timestamp(fuelConsumeBase.getUpdateTime().getTime()))
                        .updateTime(new Timestamp(fuelConsumeBase.getUpdateTime().getTime()))
                        .build();
                fuelConsumptions.add(fuelConsumption);
            }} catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return fuelConsumptions;}


    public void insertFuelConsumption(List<FuelConsumption> fuelConsumptions) throws SQLException {

        Connection connection = null;
        PreparedStatement ps = null;
        String insertFuelConsumption = "insert into fuel_consumption (fuel_id,trip_id,vehicle_id,fuel_type,fuel_amount,fuel_fee,create_time,update_time) " +
                "values (?,?,?,?,?,?,?,?)";
        try {
            connection = getConnection(url, user, password);
            ps = connection.prepareStatement(insertFuelConsumption);
            for (int i = 0; i < fuelConsumptions.size(); i++) {
                FuelConsumption fuelConsumption = fuelConsumptions.get(i);
                ps.setInt(1, fuelConsumption.getFuelId());
                ps.setInt(2, fuelConsumption.getTripId());
                ps.setInt(3, fuelConsumption.getVehicleId());
                ps.setString(4, fuelConsumption.getFuelType());
                ps.setDouble(5, fuelConsumption.getFuelAmount());
                ps.setDouble(6, fuelConsumption.getFuelFee());
                ps.setTimestamp(7, fuelConsumption.getCreateTime());
                ps.setTimestamp(8, fuelConsumption.getUpdateTime());
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
        }

    }


    public static void main(String[] args) throws SQLException {

        InsertVehicleData2Mysql insertVehicleData2Mysql = new InsertVehicleData2Mysql();

        // 插入vehicle数据
//        List<Vehicle> vehicle = insertVehicleData2Mysql.createVehicle();
//        insertVehicleData2Mysql.insertVehicle(vehicle);

        // 插入告警信息
//        List<Alert> alert = insertVehicleData2Mysql.createAlert();
//        insertVehicleData2Mysql.insertAlert(alert);

        // 插入司机信息
//        List<Driver> driver = insertVehicleData2Mysql.createDriver();
//        insertVehicleData2Mysql.insertDriver(driver);

        // 插入行程信息
//        List<Trip> trip = insertVehicleData2Mysql.createTrip();
//        insertVehicleData2Mysql.insertTrip(trip);

        // 插入维修保养信息
//        List<Maintenance> maintenance = insertVehicleData2Mysql.createMaintenance();
//        insertVehicleData2Mysql.insertMaintenance(maintenance);

        // 插入燃料添加记录信息
        List<FuelConsumption> fuelConsumption = insertVehicleData2Mysql.createFuelConsumptionV1();
        insertVehicleData2Mysql.insertFuelConsumption(fuelConsumption);


//        double bigDecimal = BigDecimal.valueOf(7.5 * 20.13).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        System.out.println(bigDecimal);




    }
}
