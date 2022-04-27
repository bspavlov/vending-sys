insert into role values (100001, 'ROLE_SELLER');
insert into role values (100002, 'ROLE_BUYER');

insert into user (id, email, password, first_name, last_name, deposit) values (100003, 'admin@vending.com', '$2a$10$vztNH21d5GDf.XWsYFMo7.ZDqa/MH64taoCzVBDY3ScUfR9vgAG02', 'Admin', 'Adminov', 0);
insert into user_role values (100003, 100001);