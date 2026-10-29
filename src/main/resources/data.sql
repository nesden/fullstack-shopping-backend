-- Create users table
CREATE TABLE users (
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) DEFAULT 'USER',
    PRIMARY KEY (username)
);

INSERT INTO users (first_name, last_name, email, phone, address, username, password, role) VALUES
('Miki', 'miki', 'mikimiki@gmail.com', '0581111111', 'fake 123 St', 'mikimiki', '$2a$10$24P9JHWZJm8yRsJRpP4a.e11OvU9ynMvz6XAKJOrxl8Nhph7mojJ2', 'USER'), -- pass MikiG@23
('nes', 'coffee', 'nesdenon@gmail.com', '0582222222', 'fake 1234 St', 'neskafe', '$2a$10$K78Qy75RrDNQcAolPojuM.sI.otXpP23xhZYJ7p2fXrIMoI.k2ehO', 'USER');-- pass Amitay@27


CREATE TABLE items (-- the list of items in the shop
    id INT AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    image VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO items (name,image, price,amount) VALUES
('goku poster','https://i.pinimg.com/736x/f1/50/68/f1506824eb8b77d75d3ae98c2557f617.jpg',30,3),
('basketball','https://upload.wikimedia.org/wikipedia/commons/7/7a/Basketball.png',20,10),
('al capone rubber duck','https://d3m9l0v76dty0.cloudfront.net/system/photos/10977880/large/5a506c95f6b1221ea4b9a8285a79bd77.jpg',10,14),
('spiked bat','https://www.swordsknivesanddaggers.com/images/products/sorted/9/926795__16881_medium.jpg',70,46),
('flashlight','https://m.media-amazon.com/images/I/51AiwUFOpPL.jpg',34.99,112),
('fake mouse cat toy', 'https://cdn11.bigcommerce.com/s-asivtkjxr8/images/stencil/original/products/2115/7014/bly00acn3mrwvaiceph8__78953.1630111762.jpg?c=1',2.99,59),
('To Kill A Mockingbird book','https://m.media-amazon.com/images/I/81O7u0dGaWL._UF1000,1000_QL80_.jpg',15.75,30),
('Wine Glass','https://www.rewardhospitality.com.au/images/ProductImages/500/1636065.jpg',9,100),
('pointed tip scissors','https://img.lakeshorelearning.com/is/image/OCProduction/ts548?wid=500&hei=375&fmt=pjpeg&pscan=auto&qlt=90,1&op_sharpen=0&resMode=bilin&op_usm=2,0.25,7,0',3.99,76),
('digital alarm clock','https://m.media-amazon.com/images/I/61j17FjPhtL._AC_SX679_.jpg',9.99,40);




CREATE TABLE favorite_to_users (
    id INT AUTO_INCREMENT,
    item_id INT NOT NULL,
    user_username VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(user_username) REFERENCES users(username)
);


CREATE TABLE orders (
    id INT AUTO_INCREMENT,
    order_id INT ,-- will be for each user
    user_username VARCHAR(255) NOT NULL,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    order_shipping_address VARCHAR(255),
    order_status VARCHAR(255) DEFAULT 'TEMP',
    full_price DOUBLE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(user_username) REFERENCES users(username)
);

CREATE TABLE order_items (-- list of items in each order
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    item_id INT,
    amount INT,
    price DOUBLE,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);




