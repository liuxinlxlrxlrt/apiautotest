package com.api.autotest.util;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JDBCUtil {

    public static Map<String, Object> query(String sql, Object... values) {//values是一个可变参数

        Map<String, Object> columnLabelAndValues = null;
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            //1、获取数据库连接
            connection = getConnection();
            //2、获取PreparedStatement对象，此对象提供数据库的操作方法
            preparedStatement = connection.prepareStatement(sql);
            //3、设置条件字段值，实时绑定
            for (int i = 0; i < values.length; i++) {
                //setObject(int parameterIndex, Object x):使用给定的对象设置指定参数的值。
                /**
                 *  PreparedStatement pstmt = con.prepareStatement（“ UPDATE EMPLOYEES
                 *                                      SET SALARY =？WHERE ID =？“）;
                 *    pstmt.setBigDecimal（1，153833.00）
                 *    pstmt.setInt（2，110592）
                 */
                preparedStatement.setObject(i + 1, values[i]);
            }
            //4、调用查询方法，查询结果，并且返回结resultSet果集
            resultSet = preparedStatement.executeQuery();

            //获取结果相关信息,检索此ResultSet对象的列的数量，类型和属性。
            ResultSetMetaData metaData = resultSet.getMetaData();

            //得到查询字段的数量:返回此ResultSet对象中的列数。
            int columnCount = metaData.getColumnCount();

            //5、从结果集去查询数据
            columnLabelAndValues = new HashMap<>();
            //res.next()的大致意思是，记录指针向下移动一个位置，如果其指向一条有效记录，则返回真；否则返回假。
            // 只有使记录指针不断移动，才能不断取出数据库中的数据
            while (resultSet.next()) {
                //循环去除每个查询字段的数据
                //java.sql.SQLException: Parameter index out of range (1 > number of parameters, which is 0).
                for (int i = 1; i <= columnCount; i++) {
                    //获取字段名:获取指定列的建议标题，以用于打印输出和显示。
                    String columnLable = metaData.getColumnLabel(i);
                    //以 Java 对象形式获取当前行中某列的值。
                    String columnValue = resultSet.getObject(columnLable).toString();
                    //将列名和值添加到map集合中
                    columnLabelAndValues.put(columnLable, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.Close(resultSet);
            JDBCUtil.Close(preparedStatement);
            JDBCUtil.Close(connection);
        }
        return columnLabelAndValues;
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Properties properties = PropertiesUtil.getJdbcProp();
        String username = PropertiesUtil.getJdbcProp().getProperty("username");
        String password = PropertiesUtil.getJdbcProp().getProperty("password");
        String url = PropertiesUtil.getJdbcProp().getProperty("url");
        String driver = PropertiesUtil.getJdbcProp().getProperty("driver");
        try {

            //1.加载驱动
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
//                System.out.println("找不到驱动程序类 ，加载驱动失败！");
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("获取连接池异常");
            e.printStackTrace();
        }

        return connection;
    }

    public static void Close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void Close(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void Close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        String sql = "select count(*) as totalNum from member where mobilephone='18813998449'";
//        Connection connection = JDBCUtil.getConnection();
//        PreparedStatement ps= null;
//        ResultSet rs=null;
//        try {
//            connection = JDBCUtil.getConnection();
//            ps = connection.prepareStatement(sql);
//            rs=ps.executeQuery();
//            ResultSetMetaData metaData = rs.getMetaData();
//            System.out.println("metaData：" + metaData);
//            //得到查询字段的数量:返回此ResultSet对象中的列数。
//            int columnCount = metaData.getColumnCount();
//            System.out.println("columnCount：" + columnCount);
//            //5、从结果集去查询数据
//            while (rs.next()) {
//                //循环去除每个查询字段的数据
//                //java.sql.SQLException: Parameter index out of range (1 > number of parameters, which is 0).
//                for (int i = 1; i <= columnCount; i++) {
//                    //获取字段名:获取指定列的建议标题，以用于打印输出和显示。
//                    String columnLable = metaData.getColumnLabel(i);
//                    System.out.println("columnLable：" + columnLable);
//                    //以 Java 对象形式获取当前行中某列的值。
//                    String columnValue = rs.getObject(columnLable).toString();
//                    System.out.println("columnValue：" + columnValue);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
