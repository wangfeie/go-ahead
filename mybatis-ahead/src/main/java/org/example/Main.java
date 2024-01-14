package org.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        InputStream xml = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(xml);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        sqlSession.clearCache();
        Configuration configuration = sqlSession.getConfiguration();
        Collection<ResultMap> resultMaps = configuration.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
            System.out.println(resultMap);
        }

        Connection connection = sqlSession.getConnection();
        connection.close();
    }
}