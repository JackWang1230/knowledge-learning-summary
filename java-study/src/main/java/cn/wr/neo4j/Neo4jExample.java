package cn.wr.neo4j;

import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

/**
 * @author RWang
 * @Date 2022/3/28
 */

public class Neo4jExample implements AutoCloseable{

    private final Driver driver;

    public Neo4jExample(String url,String user,String password){
        driver = GraphDatabase.driver(url, AuthTokens.basic(user,password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void printGreeting(final String message){
        try (Session session = driver.session()){
            // writeTransaction 也是基于lambda 8 语法 ，有一个入参 ，也有返回值
            // 所以此处基于一个入参 一个返回值的语法编写
            session.writeTransaction( tx->{
                Result result = tx.run( "CREATE (a:Greeting) " +
                                "SET a.message = $message " +
                                "RETURN a.message + ', from node ' + id(a)",
                        parameters( "message", message ) );
                return result.single().get( 0 ).asString();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url="bolt://192.168.10.248:7687";
        String user = "neo4j";
        String password = "juyin@2020";
        try {
            Neo4jExample neo4jExample = new Neo4jExample(url, user, password);
            neo4jExample.printGreeting("hello,world");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
