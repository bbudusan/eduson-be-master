create table confirmation_tokens
(
    id         int auto_increment
        primary key,
    created_on timestamp default current_timestamp() not null on update current_timestamp(),
    value      varchar(255)                          null,
    user_id    bigint                                not null,
    constraint UK_lgokc3vw1rct83pdwryntacb9
        unique (user_id)
)
    engine = MyISAM;