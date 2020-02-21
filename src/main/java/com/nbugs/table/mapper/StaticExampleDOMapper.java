package com.nbugs.table.mapper;

import com.nbugs.table.entity.StaticExampleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * (StaticExample)表数据库访问层
 */
@Mapper
public interface StaticExampleDOMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Select(" select id, status from t_static_example where id = #{id} ")
    StaticExampleDO queryById(@Param("id") Long id);

}
