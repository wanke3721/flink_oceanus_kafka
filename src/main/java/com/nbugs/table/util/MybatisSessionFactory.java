package com.nbugs.table.util;

import com.nbugs.table.entity.StaticNodeDO;
import com.nbugs.table.mapper.StaticExampleDOMapper;
import com.nbugs.table.mapper.StaticNodeDOMapper;
import com.nbugs.table.mapper.StaticUserDOMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 *
 *  SqlSession factory 单例  事务设置为手动提交
 */
public class MybatisSessionFactory {

    private static SqlSessionFactory factory = null;

    static{
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        InputStream reader = MybatisSessionFactory.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        factory = builder.build(reader);
        factory.getConfiguration().addMapper(StaticExampleDOMapper.class);
        factory.getConfiguration().addMapper(StaticNodeDOMapper.class);
        factory.getConfiguration().addMapper(StaticUserDOMapper.class);
    }

    private static final ThreadLocal<SqlSession> tol = new ThreadLocal<>();

    public static SqlSession openSession(){
        SqlSession session = tol.get();
        if(session == null){
            session = factory.openSession();
            tol.set(session);
        }
        return session;
    }

    public static void close(){
        SqlSession session = openSession();
        tol.remove();
        session.close();
    }

    public static void commit(){
        SqlSession session = openSession();
        session.commit();
        close();
    }

    public static void rollback(){
        SqlSession session = openSession();
        session.rollback();
        close();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getMapper(Class a){
        SqlSession session = openSession();
        return session.getMapper(a);
    }

}
