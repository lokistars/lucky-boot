create table areastown
(
    id       int auto_increment comment '主键Id'
        primary key,
    AreaId   int         null comment '行政区划编码',
    ParentId int         null comment '父级编号',
    name     varchar(32) not null comment '地区名称',
    type     int         not null comment '区域等级，1 省 2市 3区'
)
    collate = utf8_bin;

create table city
(
    id   int auto_increment comment 'ID'
        primary key,
    name varchar(32) default '匿名' not null
)
    collate = utf8_bin;

create table englishdict
(
    uid       int          not null,
    chinese   varchar(100) not null,
    english   varchar(20)  not null,
    unumber   int          null,
    dataProcs int          not null,
    dataTime  varchar(8)   not null
);

create table obsnumber
(
    numberPrefix varchar(8) default '' not null comment '号码前缀'
        primary key,
    areaCode     varchar(6) default '' not null comment '区号',
    postalCode   varchar(6) default '' not null comment '邮政编码',
    province     varchar(20)           null comment '省份',
    city         varchar(40)           null comment '城市',
    operators    varchar(20)           null comment '运营商'
)
    comment '号码归属地' charset = utf8mb4;

create table t_dict
(
    csbh int          not null comment '参数编号',
    csmc varchar(100) not null comment '参数名称',
    csz  varchar(200) not null comment '参数值',
    bz   varchar(200) null comment '备注',
    rq   datetime     not null comment '创建日期'
)
    comment '数据字典';

create table t_user
(
    id          int auto_increment comment 'ID自增'
        primary key,
    userName    varchar(10)                                not null comment '用户名',
    password    varchar(32)                                not null comment '密码',
    user_stats  tinyint unsigned default '0'               null comment '用户状态,0 正常,1禁用,2删除',
    version     int unsigned     default '1'               not null comment '版本',
    create_time timestamp        default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time timestamp        default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
)
    comment '用户登录' collate = utf8_bin;

create table t_xxlb
(
    lbbh  int          not null,
    lbmc  varchar(100) not null,
    flbbh int          not null,
    zt    int          not null,
    cjrq  int          not null,
    cjsj  varchar(8)   not null
);

create table t_xxwt
(
    wtbh int           not null,
    wtbt varchar(2000) not null,
    wtnr varchar(2000) null,
    xxlb int           not null,
    gxrq int           null,
    gxsj varchar(8)    null
);

INSERT INTO lucky.t_xxwt (wtbh, wtbt, wtnr, xxlb, gxrq, gxsj) VALUES (1, '融券业务恢复了吗？', '请在这里补充您的问题', 3, 20160224, '16:11:28');
INSERT INTO lucky.t_xxwt (wtbh, wtbt, wtnr, xxlb, gxrq, gxsj) VALUES (2, '给客户电话验证密码，提示没有操作渠道0的权限，请问是需要开通柜台委托吗？', null, 4, 20190301, '15:39:42');
INSERT INTO lucky.t_xxwt (wtbh, wtbt, wtnr, xxlb, gxrq, gxsj) VALUES (3, '登录易网厅提示：没有访问操作渠道【0】，是什么原因？', null, 3, 20190411, '14:05:07');
INSERT INTO lucky.t_xxwt (wtbh, wtbt, wtnr, xxlb, gxrq, gxsj) VALUES (4, '请问网上操作销户，是不是系统自动处理不用再去营业部办理吧？', null, 4, 20190411, '14:40:54');
INSERT INTO lucky.t_xxwt (wtbh, wtbt, wtnr, xxlb, gxrq, gxsj) VALUES (5, '咱们现在有为客户进行推送复牌股票的信息吗？', null, 3, 20190212, '11:14:27');
INSERT INTO lucky.t_xxwt (wtbh, wtbt, wtnr, xxlb, gxrq, gxsj) VALUES (6, '操作转户可以只转股票吗？', null, 4, 20190301, '14:57:13');


INSERT INTO lucky.t_user (id, userName, password, user_stats, version, create_time, update_time) VALUES (1, 'admin', 'admin', 1, 2, '2020-09-12 15:24:48', '2021-10-15 19:44:39');

INSERT INTO lucky.englishdict (uid, chinese, english, unumber, dataProcs, dataTime) VALUES (1, '我', 'I', 0, 20200411, '20:20:00');