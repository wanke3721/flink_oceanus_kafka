package com.nbugs.table.event;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * 消息更新状态事件
 *
 * @author dongweima
 **/
@Data
public class MsgUpdateEvent {

    private Long id;
    private Long msgId;
    private Integer countState;
    private Long countTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static void main(String[] args) {
        MsgUpdateEvent e = new MsgUpdateEvent();
        e.setCountTime(System.currentTimeMillis());
        e.setId(1L);
        e.setCountState(1);
        e.setMsgId(1L);
        System.out.println(e.toString());
    }
}
