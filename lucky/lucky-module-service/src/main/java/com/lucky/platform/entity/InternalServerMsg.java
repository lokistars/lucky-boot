package com.lucky.platform.entity;

import com.google.protobuf.Message;
import io.netty.channel.ChannelId;

/**
 * @program: lucky-module-service
 * @description: 内部服务器消息
 * @author: Loki
 * @data: 2023-03-09 23:51
 **/
public class InternalServerMsg {

    private Integer gatewayServerId;

    private Integer remoteSessionId;

    private ChannelId userId;

    private Short msgCode;

    private Message msgBody;

    public Integer getGatewayServerId() {
        return gatewayServerId;
    }

    public InternalServerMsg setGatewayServerId(Integer gatewayServerId) {
        this.gatewayServerId = gatewayServerId;
        return this;
    }

    public Integer getRemoteSessionId() {
        return remoteSessionId;
    }

    public InternalServerMsg setRemoteSessionId(Integer remoteSessionId) {
        this.remoteSessionId = remoteSessionId;
        return this;
    }

    public ChannelId getUserId() {
        return userId;
    }

    public InternalServerMsg setUserId(ChannelId userId) {
        this.userId = userId;
        return this;
    }

    public Short getMsgCode() {
        return msgCode;
    }

    public InternalServerMsg setMsgCode(Short msgCode) {
        this.msgCode = msgCode;
        return this;
    }

    public Message getMsgBody() {
        return msgBody;
    }

    public InternalServerMsg setMsgBody(Message msgBody) {
        this.msgBody = msgBody;
        return this;
    }
}
