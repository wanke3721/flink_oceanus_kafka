package com.nbugs.table.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * (StaticNodeDO)表数据库访问层
 *
 * @author wangkj
 * @since 2019-12-25 13:31:01
 */
@Mapper
public interface StaticNodeDOMapper {

    @Select("<script>" +
        " select parent_id as parentId from t_static_node where id in "
        + " <foreach collection=\"ids\" item=\"id\" index=\"index\" open=\"(\" close=\")\" separator=\",\">\n"
        + "            #{id}\n"
        + " </foreach>"
        + "</script>")
    List<String> findParentIdsByIds(@Param("ids") List<String> ids);

    @Update(
        "<script>" +
            " update t_static_node set counted = counted+1, count_rate = concat(round((counted+1)/total_count*100 ),'%'), " +
            " gmt_modify = #{gmtModify} where id in  "
            + " <foreach collection=\"ids\" item=\"id\" index=\"index\" open=\"(\" close=\")\" separator=\",\">\n"
            + "            #{id}\n"
            + " </foreach>"
            + "</script>")
    int updateReadCountAndReadRateByIds(@Param("ids") List<String> ids, @Param("gmtModify") Date gmtModify);

}