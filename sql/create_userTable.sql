-- auto-generated definition
create table user
(
    id          bigint auto_increment comment 'id'
        primary key,
    username    varchar(256)                       null comment '用户昵称',
    userAccount varchar(256)                       null comment '登录账号',
    password    varchar(512)                       null comment '登录密码',
    email       varchar(256)                       null comment '用于注册邮箱或者登录账号',
    avatarUrl   varchar(256)                       null comment '头像地址',
    gender      tinyint                            null comment '性别',
    userStatus  int      default 0                 not null comment '0-正常  1-封号',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null comment '修改日期',
    isDelete    tinyint  default 0                 not null comment '0-正常  1-删除',
    userRole    int      default 0                 not null comment '用户角色  0-普通用户  1-管理员',
    plantCode   varchar(512)                       null comment '星球编号'
)
    comment '用户';
alter table user add COLUMN tags varchar(1024) null comment '标签列表';
alter table user add COLUMN userDescription varchar(1024) null comment '用户描述';


show create table user;

alter database hh character set utf8;

alter table user character set utf8;

alter table user modify tags varchar(1024) character set utf8;