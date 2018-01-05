package com.whh.middleware.kafka.event.dao;

import com.whh.middleware.kafka.event.model.MsgSend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MsgSendMapper {

    int insert(MsgSend record);

    int updateSending(@Param("msgSendId") Long msgSendId, @Param("status") Byte status, @Param("preStatus") Byte preStatus);

    int updateSend(@Param("msgSendId") Long msgSendId, @Param("status") Byte status);

    int updateFail(@Param("msgSendId") Long msgSendId, @Param("status") Byte status);

    int deletePast(@Param("status") Byte status, @Param("keepSecond") Integer keepSecond);

    List<MsgSend> listUnsend(@Param("status") Byte status,
                             @Param("retryInterval") Integer retryInterval,
                             @Param("statusSending") Byte statusSending,
                             @Param("sendingExpire") Integer sendingExpire,
                             @Param("trys") Short trys,
                             @Param("size") Integer size);

    long countByStatus(@Param("status") Byte status);
}