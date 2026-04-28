	-- 商品マスタ
	INSERT INTO m_product (product_code, product_name, price, register_datetime, update_datetime)
	VALUES ('001', 'iPhone17', 130000, current_timestamp, current_timestamp);

	INSERT INTO m_product (product_code, product_name, price, register_datetime, update_datetime)
	VALUES ('002', 'Galaxy S26', 136400, current_timestamp, current_timestamp);

	INSERT INTO m_product (product_code, product_name, price, register_datetime, update_datetime)
	VALUES ('003', 'Pixel 10', 128900, current_timestamp, current_timestamp);

	INSERT INTO m_product (product_code, product_name, price, register_datetime, update_datetime)
	VALUES ('004', 'Xiaomi 15T Pro', 109800, current_timestamp, current_timestamp);

	INSERT INTO m_product (product_code, product_name, price, register_datetime, update_datetime)
	VALUES ('005', 'AQUOS sense10', 67430, current_timestamp, current_timestamp);


	-- 売上テーブル
	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-03-25', '003', 700, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-05-14', '001', 200, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-06-21', '005', 1000, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-07-02', '002', 500, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-08-30', '004', 100, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-09-28', '003', 900, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-10-03', '002', 30, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-11-06', '005', 80, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2025-12-19', '001', 600, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2026-01-08', '004', 400, current_timestamp, current_timestamp);

	INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime)
	VALUES ('2026-02-14', '002', 1300, current_timestamp, current_timestamp);