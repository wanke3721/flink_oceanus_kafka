package com.nbugs.table.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StaticExampleDO implements Serializable {

    private static final long serialVersionUID = -999600907413674105L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 应用ID
     */
    private Integer appId;
    /**
     * 统计ID
     */
    private Integer countId;
    /**
     * 消息ID
     */
    private Integer infoId;
    /**
     * 启动状态（1是0否）
     */
    private Integer status;
    /**
     * 是否删除
     */
    private Integer deleted;

}
