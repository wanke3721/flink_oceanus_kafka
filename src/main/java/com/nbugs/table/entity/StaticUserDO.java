package com.nbugs.table.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * (StaticUser)实体类
 *
 * @author wotrd
 * @since 2019-12-25 14:31:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StaticUserDO implements Serializable {

    private static final long serialVersionUID = -66360997547957814L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 消息ID
     */
    private Long msgId;
    /**
     * 节点ID
     */
    private String parentId;
    /**
     * 用户组织ID
     */
    private String orgId;
    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户名字
     */
    private String userName;
    /**
     * 阅读状态（1已读0未读）
     */
    private Integer countState;
    /**
     * 是否删除（1已读0未读）
     */
    private Integer deleted;
    private String createUser;
    private Date gmtCreate;
    private Date gmtModify;
    private String modifyUser;

}