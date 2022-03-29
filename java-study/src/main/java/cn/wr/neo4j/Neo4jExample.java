package cn.wr.neo4j;

import org.junit.Test;
import org.neo4j.driver.*;
import org.neo4j.driver.summary.ResultSummary;

import java.util.List;

import static org.neo4j.driver.Values.ofList;
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

    public void addPerson(final String  name){
        try {
            Session session = driver.session();
            session.writeTransaction( tx -> {
                final ResultSummary name1 = tx.run("CREATE (a:Person {name: $name})", parameters("name", name)).consume();

                return 1;
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public int addEmployees(String companyName){
        int employees =0;
        try {
            Session session = driver.session();
            List<Record> persons = session.readTransaction(tx -> {
                List<Record> list = tx.run("MATCH (a:Person) RETURN a.name as name").list();
                return list;
            });
            for (Record person : persons) {
                employees +=session.writeTransaction( tx -> {
                     Result result = tx.run("MATCH (emp:Person){name:$person_name} " +
                                    "MERGE (com:Company {name:$company_name}) " +
                                    "MERGE (emp)-[:WORK_FOR]->(com)",
                            parameters("person_name", person.get("name").asString(),
                                    "company_name", companyName));
                     result.consume();
                     return 1;
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static void main(String[] args) {
        String url="bolt://192.168.10.248:7687";
        String user = "neo4j";
        String password = "juyin@2020";
        try {
            Neo4jExample neo4jExample = new Neo4jExample(url, user, password);
            neo4jExample.addPerson("xiaoming");
//            neo4jExample.printGreeting("hello,world");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
