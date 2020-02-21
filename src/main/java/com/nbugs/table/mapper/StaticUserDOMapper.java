package com.nbugs.table.mapper;

import com.nbugs.table.entity.StaticUserDO;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * (StaticUser)表数据库访问层
 */
@Mapper
public interface StaticUserDOMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Select(" select id, parent_id as parentId, msg_id as msgId, count_state as countState,user_id as userId " +
        " from t_static_user where id = #{id} ")
    StaticUserDO queryById(@Param("id") Long id);

    /**
     * 用户的所有部门更新阅读状态
     *
     * @param msgId 消息id、
     * @param userId 用户id
     * @return 影响行数
     */
    @Update(" update t_static_user set count_state = 1, gmt_modify = #{gmtModify} where msg_id = #{msgId} and user_id=#{userId} ")
    int updateReadStateByMsgIdAndUserId(@Param("msgId") Long msgId, @Param("userId") String userId, @Param("gmtModify") Date gmtModify);

    /**
     * 找到用户的所有部门
     *
     * @param msgId 消息id、
     * @param userId 用户id
     * @return 实例对象
     */
    @Select(" select  parent_id as parentId from t_static_user where msg_id = #{msgId} and user_id=#{userId} ")
    List<String> findByMsgIdAndUserId(@Param("msgId") Long msgId, @Param("userId") String userId);

}