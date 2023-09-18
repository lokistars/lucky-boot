package com.lucky.platform.entity;

/**
 * @program: lucky
 * @description: 移动状态
 * @author: Loki
 * @data: 2023-03-10 11:10
 **/
public class MoveState {
    /**
     * 起始位置 X
     */
    public float fromPosX;

    /**
     * 起始位置 Y
     */
    public float fromPosY;

    /**
     * 目标位置 X
     */
    public float toPosX;

    /**
     * 目标位置 Y
     */
    public float toPosY;

    /**
     * 启程时间
     */
    public long startTime;

    public float getFromPosX() {
        return fromPosX;
    }

    public void setFromPosX(float fromPosX) {
        this.fromPosX = fromPosX;
    }

    public float getFromPosY() {
        return fromPosY;
    }

    public void setFromPosY(float fromPosY) {
        this.fromPosY = fromPosY;
    }

    public float getToPosX() {
        return toPosX;
    }

    public void setToPosX(float toPosX) {
        this.toPosX = toPosX;
    }

    public float getToPosY() {
        return toPosY;
    }

    public void setToPosY(float toPosY) {
        this.toPosY = toPosY;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
