package com.nbugs.table.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * (StaticNode)实体类
 *
 * @author wotrd
 * @since 2019-12-25 13:30:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StaticNodeDO implements Serializable {

    private static final long serialVersionUID = -25570832988487424L;
    /**
     * 主键ID
     */
    private String id;
    /**
     * 通知ID
     */
    private Long msgId;
    /**
     * 父节点ID
     */
    private String parentId;
    /**
     * 组织ID
     */
    private String orgId;
    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 部门名字
     */
    private String deptName;
    /**
     * 已读人数
     */
    private Integer counted;
    /**
     * 全部人数
     */
    private Integer totalCount;
    /**
     * 已读率
     */
    private String countRate;
    /**
     * 节点类型
     */
    private Integer nodeType;

    /**
     * 是否删除（1是0否）
     */
    private Integer deleted;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModify;
    /**
     * 修改人
     */
    private String modifyUser;

}