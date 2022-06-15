package cn.wr.annotation;

import java.lang.reflect.Field;

/**
 * @author RWang
 * @Date 2022/3/7
 */

@TableName(name = "USERS")
public class Users {
    @TableColumn(ColumnName="id",ColumnType = ColumnType.Int)
    @Constraints(primarykey = true)
    public int id;

    @TableColumn(ColumnName ="name",ColumnType = ColumnType.String,value = 50)
    public String name;
    @TableColumn(ColumnName = "password", ColumnType = ColumnType.String, value = 50)
    public String password;

    @TableColumn(ColumnName = "height", ColumnType = ColumnType.Double)
    public double height;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }


    public static void main(String[] args) {

//        String name = Users.class.getName();
//        System.out.println(name);

        StringBuilder sb = new StringBuilder();
        TableName tableName = Users.class.getAnnotation(TableName.class);
        if (tableName != null && !"".equals(tableName.name())) {
            sb.append("CREATE TABLE " + tableName.name() + "(\n");
        } else {
            String[] name = Users.class.getName().split("\\.");
            sb.append("CREATE TABLE "
                    + (name.length == 0 ? "" : name[name.length - 1]) + "(\n");
        }

        Field[] declaredFields = Users.class.getDeclaredFields();

        for (Field field : Users.class.getDeclaredFields()) {
            TableColumn tc = field.getAnnotation(TableColumn.class);
            // 这里就不考虑没有加字段的情况了，如果要加方法类似表名
            if (tc != null) {
                sb.append(tc.ColumnName());
                switch (tc.ColumnType()) {
                    case String:
                        sb.append(" VARCHAR2(" + tc.value() + ")");
                        break;
                    case Double:
                        sb.append(" NUMBER");
                        break;
                    case Int:
                        sb.append(" INT");
                }
            }
            Constraints con = field.getAnnotation(Constraints.class);
            if (con != null) {
                if (con.primarykey()) {
                    sb.append(" PRIMARY KEY");
                }else if (!con.allowNull()) {
                    sb.append(" NOT NULL");
                }
                if (con.unique()) {
                    sb.append(" UNIQUE");
                }
            }
            sb.append(",\n");
        }
        sb.delete(sb.lastIndexOf(",\n"), sb.lastIndexOf(",\n")+",\n".length());
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(")\n");
        System.out.println(new String(sb));
    }
}
