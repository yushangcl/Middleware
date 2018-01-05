package com.whh.middleware.kafka.event.dao;


import com.whh.middleware.kafka.event.model.MsgRecv;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MsgRecvMapper {

    int insert(MsgRecv record);

    int updateProcessing(@Param("msgRecvId") Long msgRecvId, @Param("status") Byte status, @Param("preStatus") Byte preStatus);

    int updateProcessed(@Param("msgRecvId") Long msgRecvId, @Param("status") Byte status);

    int updateFail(@Param("msgRecvId") Long msgRecvId, @Param("status") Byte status);

    int deletePast(@Param("status") Byte status, @Param("keepSecond") Integer keepSecond);

    List<MsgRecv> listUnprocess(@Param("status") Byte status,
                                @Param("retryInterval") Integer retryInterval,
                                @Param("statusProcessing") Byte statusProcessing,
                                @Param("processingExpire") Integer processingExpire,
                                @Param("trys") Short trys,
                                @Param("size") Integer size);
}