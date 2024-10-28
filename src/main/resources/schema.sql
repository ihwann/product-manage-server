create table brand_info
(
    brand_id    bigint auto_increment primary key comment '아이디',
    insert_time timestamp    not null default current_timestamp comment '등록 시각',
    update_time timestamp    not null default current_timestamp comment '변경 시각',
    brand_name  varchar(100) not null comment '브랜드 명'
)
;

create table product_info
(
    product_id    bigint auto_increment primary key comment '아이디',
    insert_time   timestamp    not null default current_timestamp comment '등록 시각',
    update_time   timestamp    not null default current_timestamp comment '변경 시각',
    brand_id      bigint       not null comment '브랜드 아이디',
    category      varchar(50)  not null comment '카테고리',
    product_name  varchar(100) not null comment '제품명',
    product_price int          null comment '제품 가격',
    foreign key (brand_id) references brand_info (brand_id)
)
;