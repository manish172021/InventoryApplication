-- Clear and reset
DELETE FROM item;

-- Normal stock items
INSERT INTO item (id, sku, available_quantity, reserved_quantity) VALUES
(1, 'IPHONE-15', 100, 0),
(2, 'MACBOOK-PRO', 50, 0);

-- Low stock items
INSERT INTO item (id, sku, available_quantity, reserved_quantity) VALUES
(3, 'IPAD-MINI', 3, 0),
(4, 'AIRPODS', 1, 0);

-- Items with reservations
INSERT INTO item (id, sku, available_quantity, reserved_quantity) VALUES
(5, 'MACBOOK-AIR', 20, 5),
(6, 'IPHONE-14', 10, 2);

-- Out-of-stock items
INSERT INTO item (id, sku, available_quantity, reserved_quantity) VALUES
(7, 'HOMEPOD', 0, 0),
(8, 'MAGIC-MOUSE', 0, 3);

-- Bulk items
INSERT INTO item (id, sku, available_quantity, reserved_quantity) VALUES
(9, 'USB-CABLE', 500, 0),
(10, 'SCREEN-PROTECTOR', 1000, 0);