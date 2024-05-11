# 数据库初始化

-- 创建库
create database if not exists oj;

-- 切换库
use oj;





-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json 数组）',
    judgeConfig text                               null comment '判题配置（json 对象）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交' collate = utf8mb4_unicode_ci;

create table if not exists user_comments
(
    id               bigint auto_increment comment '评论ID' primary key ,
    userId           bigint not null COMMENT '用户ID（外键，引用 user 表的 id）',
    questionId       bigint not null COMMENT '题目ID（外键，引用 题目 表的 id）',
    commentContent   text   not null COMMENT '评论内容',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint default 0 not null comment '是否删除',

    -- 喜欢和收藏计数字段
    likesCount       int default 0 not null comment '喜欢数量',
    collectsCount    INT DEFAULT 0 not null comment '收藏数量',

    index idx_questionId (questionId)
) comment '用户评论' collate = utf8mb4_unicode_ci;


create table if not exists comment_like (
                                            id bigint not null auto_increment comment 'id',
                                            commentId bigint not null comment '评论id',
                                            userId bigint not null comment '用户id',
                                            isLike tinyint(1) not null default 0,
                                            primary key (id),
                                            foreign key (commentId) references user_comments(id),
                                            foreign key (userId) references user(id), -- 假设用户表是user
                                            createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
                                            updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
                                            isDelete   tinyint  default 0                 not null comment '是否删除'
)comment '评论点赞表'collate = utf8mb4_unicode_ci;
# -- 添加外键约束，关联到 user 表
# ALTER TABLE user_comments
#     ADD CONSTRAINT fk_user_comments_user
#         FOREIGN KEY (userId)
#             REFERENCES user(id)
#             ON DELETE CASCADE;

