package com.lucky.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * <p>
 * 用户登录
 * </p>
 *
 * @author Nuany
 * @since 2020-09-12
 */
@Schema(name = "TUser对象", description = "用户登录")
@TableName("t_user")
public class User extends Model<User>   {

    private static final long serialVersionUID = 1L;

    @Schema(name = "ID自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

     @Schema(name = "用户名")
    @TableField("userName")
    private String userName;

     @Schema(name = "密码")
    private String password;

     @Schema(name = "用户状态,0 正常,1禁用,2删除")
    @TableField(value = "user_stats")
    private Integer userStats;

     @Schema(name = "版本")
    private Integer version;

     @Schema(name = "创建时间")
    @TableField("create_time")
    private Date createTime;

     @Schema(name = "修改时间")
    @TableField("update_time")
    private Date updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserStats() {
        return userStats;
    }

    public void setUserStats(Integer userStats) {
        this.userStats = userStats;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TUser{" +
                "id=" + id +
                ", userName=" + userName +
                ", password=" + password +
                ", userStats=" + userStats +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                "}";
    }
}
