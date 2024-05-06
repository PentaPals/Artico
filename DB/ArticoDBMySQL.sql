CREATE DATABASE IF NOT EXISTS ArticoDBMySQL;
use ArticoDBMySQL;

CREATE TABLE users (
  id integer PRIMARY KEY AUTO_INCREMENT,
  role int NOT NULL DEFAULT 3, -- 1 - admin, 2 - staff, 3 - customer OR enum('admin', 'staff', 'customer')
  username varchar(255) UNIQUE,
  password varchar(255) UNIQUE,
  first_name varchar(255),
  last_name varchar(255),
  email varchar(255) UNIQUE,
  address varchar(255),
  balance decimal(10,2)
);

CREATE TABLE products (
  id integer PRIMARY KEY AUTO_INCREMENT,
  name varchar(255),
  price decimal(10,2),
  min_price decimal(10,2),
  category varchar(255),
  quantity int
);

CREATE TABLE promotions (
  id integer PRIMARY KEY AUTO_INCREMENT,
  name varchar(255),
  percentage int,
  start_date datetime,
  duration_days int
);

CREATE TABLE orders (
  id integer PRIMARY KEY AUTO_INCREMENT,
  user_id integer,
  total decimal(10,2),
  created_at datetime,
  status int -- OR enum('started', 'processing', 'finished')
);

CREATE TABLE product_promotions (
  product_id integer,
  promotion_id integer,
  PRIMARY KEY (product_id, promotion_id)
);

CREATE TABLE order_product (
  order_id int,
  product_id int,
  PRIMARY KEY (order_id, product_id)
);

ALTER TABLE orders ADD FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE order_product ADD FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_product ADD FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_promotions ADD FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_promotions ADD FOREIGN KEY (promotion_id) REFERENCES promotions (id);

INSERT INTO users (role, username, password, first_name, last_name) VALUES ('1', 'admin1', 'admin1', 'Gabriela', 'Varbanova');
