package com.lucky.platform.server.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏消息识别器
 * @author Loki
 */
public final class GameMsgRecognizer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息代号和消息体字典
     */
    static private final Map<Integer,  GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();

    /**
     * 消息类型和消息编号字典
     */
    static private final Map<Class<?>, Integer> _msgTypeAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private GameMsgRecognizer() {
    }

    /**
     * 初始化
     */
    static public void init() {
        LOGGER.info("=== 完成消息编号与消息体的映射 ===");
        // 得到该类所有的内部类 (包括内部接口),不包含父类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClazz : innerClazzArray) {
            // 如果不是消息体类, 则跳过
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }

            String clazzName = innerClazz.getSimpleName().toLowerCase();

            // 遍历枚举获取所有消息消息编号
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                // 获取消息编号
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info("关联 {} <==> {}", innerClazz.getName(), msgCode.getNumber());

                    _msgCodeAndMsgBodyMap.put(msgCode.getNumber(),(GeneratedMessageV3) returnObj);

                    _msgTypeAndMsgCodeMap.put(innerClazz,msgCode.getNumber());
                } catch (Exception ex) {
                    // 记录错误日志
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * 根据消息编号获取构建器
     *
     * @param msgCode 消息编号
     * @return 消息构建器
     */
    static public Message.Builder getMsgBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);

        if (null == msg) {
            return null;
        }

        return msg.newBuilderForType();
    }

    /**
     * 根据消息类型获取消息编号
     *
     * @param msgType 消息类型
     * @return 消息编号
     */
    static public int getMsgCodeByMsgType(Class<?> msgType) {
        if (null == msgType) {
            return -1;
        }

        Integer msgCode = _msgTypeAndMsgCodeMap.get(msgType);
        if (null != msgCode) {
            return msgCode;
        }

        return -1;
    }
}
