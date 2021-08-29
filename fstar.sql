create table changelog
(
    id           int auto_increment
        primary key,
    build_number int not null,
    version      varchar(500) null,
    description  varchar(2000) null,
    download_url varchar(200) null
);

create table course
(
    id             int auto_increment
        primary key,
    student_number varchar(500) not null,
    semester       varchar(500) not null,
    course_id      varchar(500) null,
    course_name    varchar(500) null,
    classroom      varchar(500) null,
    raw_week       varchar(500) null,
    course_row     int null,
    row_span       int null,
    course_column  int null,
    teacher        varchar(500) null,
    default_color  varchar(500) null,
    custom_color   int null,
    top            int null
);

create table fstar_user
(
    id       int auto_increment
        primary key,
    username varchar(500) not null,
    password varchar(500) not null,
    roles    varchar(200) not null
);

create table device
(
    id              int auto_increment
        primary key,
    app_version     varchar(200) null,
    build_number    int null,
    android_id      varchar(500) null,
    android_version varchar(200) null,
    brand           varchar(500) null,
    device          varchar(500) null,
    model           varchar(500) null,
    product         varchar(500) null,
    platform        varchar(200) not null
);

create table message
(
    id                       int auto_increment
        primary key,
    content                  varchar(2000) not null,
    publish_time             datetime null,
    max_visible_build_number int           not null,
    min_visible_build_number int           not null
);

create table parse_config
(
    id           int auto_increment
        primary key,
    school_name  varchar(500)  not null,
    school_url   varchar(200) null,
    user         varchar(500) null,
    author       varchar(500)  not null,
    pre_url      varchar(200) null,
    code_url     varchar(200)  not null,
    publish_time datetime null,
    remark       varchar(1000) null,
    download     int default 0 not null
);

create table score
(
    id                        int auto_increment
        primary key,
    student_number            varchar(500) null,
    no                        varchar(500) null,
    semester                  varchar(500) null,
    score_no                  varchar(500) null,
    name                      varchar(500) null,
    score                     varchar(500) null,
    credit                    varchar(500) null,
    `period`                  varchar(50) null,
    evaluation_mode           varchar(500) null,
    course_property           varchar(500) null,
    course_nature             varchar(500) null,
    alternative_course_number varchar(500) null,
    alternative_course_name   varchar(500) null,
    score_flag                varchar(500) null
);