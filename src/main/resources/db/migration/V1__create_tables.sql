do $$
begin
    if not exists (select 1 from pg_type where typname = 'inventory') THEN
        create type inventory as enum
        ('t-shirt', 'cup', 'book', 'pen', 'powerbank', 'hoody', 'umbrella', 'socks', 'wallet', 'pink-hoody');
    end if;
end$$;

create table if not exists users(
    username varchar(256) primary key,
    password varchar(256) not null
);

create table if not exists user_balance(
	username varchar(256) primary key,
	coins bigint default 1000 not null
);

create table if not exists user_inventory(
	username varchar(256) references users(username),
	inventory_type inventory not null,
	quantity bigint not null,
	primary key (username, inventory_type)
);

create table if not exists history(
	id bigserial primary key,
	to_user varchar(256) references users(username),
	from_user varchar(256) references users(username),
	amount bigint not null
);

create index if not exists history_to_user_idx on history using hash(to_user);
create index if not exists history_from_user_idx on history using hash(from_user);
create index if not exists user_inventory_username_idx on user_inventory using hash(username);
