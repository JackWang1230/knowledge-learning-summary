package cn.wr.utils;

import cn.wr.model.StockData;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/8/5
 */

public class MongoDBUtil {

    public static Mongo mongo= null;

    public static MongoClientOptions initialMongoProps(){

        MongoClientOptions.Builder builder= new MongoClientOptions.Builder();
        //与目标数据库可以建立的最大链接数
        builder.connectionsPerHost(100);
        //与数据库建立链接的超时时间
        builder.connectTimeout(1000 * 60 * 20);
        //一个线程成功获取到一个可用数据库之前的最大等待时间
        builder.maxWaitTime(100 * 60 * 5);
        builder.threadsAllowedToBlockForConnectionMultiplier(100);
        builder.maxConnectionIdleTime(0);
        builder.maxConnectionLifeTime(0);
        builder.socketTimeout(0);
        builder.socketKeepAlive(true);
       return builder.build();
    }

    /**
     *  获取服务地址list
     * @param addresses
     * @return
     */
    public static List<ServerAddress> getServers(ServerAddress... addresses){
        return Arrays.asList(addresses);
    }

    /**
     *  获取服务地址
     * @param host
     * @param port
     * @return
     */
    public static ServerAddress getServerAddress(String host,int port){

        return new ServerAddress(host,port);
    }

    /**
     * 获取认证list
     * @param credentials
     * @return
     */
    public static List<MongoCredential> getCredentials(MongoCredential...credentials){
        return Arrays.asList(credentials);
    }

    /**
     * 获取认证
     * @param userName
     * @param database
     * @param password
     * @return
     */
    public static MongoCredential getMongoCredential(String userName,String database,String password){
        return MongoCredential.createCredential(userName,database,password.toCharArray());
    }

    /**
     * 连接mongodb客户端
     * @param addresses
     * @param credentials
     * @return
     */
    public static MongoClient getMongoClient(List<ServerAddress> addresses,List<MongoCredential> credentials){

        return new MongoClient(addresses,credentials,initialMongoProps());
    }

    /**
     *  连接mongodb数据库
     * @param mongoClient
     * @param database
     * @return
     */
    public static MongoDatabase getMongoDatabase(MongoClient mongoClient,String database){

        return mongoClient.getDatabase(database);
    }

    /**
     *  连接mongo集
     * @param mongoDatabase
     * @param collectionName
     * @return
     */
    public static MongoCollection<Document> getMongoCollection(MongoDatabase mongoDatabase,String collectionName){
        return mongoDatabase.getCollection(collectionName);
    }

    /**
     * 关闭mongo数据库连接
     * @param mongoClient
     */
    public static void closeMongo(MongoClient mongoClient){
        mongoClient.close();
    }

    /**
     * 获取mongo客户端对象，通过servers和credentials对象创建
     * @param parameterTool
     * @return
     */
    public static MongoClient getMongoClient(ParameterTool parameterTool){

        return  new MongoClient(getServers(getServerAddress(
                parameterTool.get(MONGO_DATABASE_HOST),
                parameterTool.getInt(MONGO_DATABASE_PORT))),
                getCredentials(getMongoCredential(parameterTool.get(MONGO_DATABASE_USER),
                        parameterTool.get(MONGO_DATABASE_DBNAME),
                        parameterTool.get(MONGO_DATABASE_PASSWORD))));
    }

    /**
     * 获取collection对象
     * @param parameterTool
     * @param collectionName
     * @return
     */
    public static MongoCollection<Document> getCollection(ParameterTool parameterTool,String collectionName){

        return getMongoClient(parameterTool).getDatabase(parameterTool.get(MONGO_DATABASE_DBNAME)).getCollection(collectionName);
    }

    public static void main(String[] args) throws Exception {
        String proFilePath = ParameterTool.fromArgs(args).get("conf");
        final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
        String collectionName="";
        MongoCollection<Document> collection = MongoDBUtil.getCollection(parameterTool, collectionName);

        Bson and = Filters.and(Filters.eq("store_internal_id", ""), Filters.eq("goods_internal_id", ""));
        FindIterable<Document> documents = collection.find(and);
        ObjectMapper objectMapper = new ObjectMapper();
        for (Document document : documents) {
            StockData stockData = objectMapper.convertValue(document, StockData.class);
        }

    }


}
