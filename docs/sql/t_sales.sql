CREATE TABLE t_sales(
    sales_date DATE COMMENT '売上日',
    product_code INT COMMENT '商品コード',
    quantity INT NOT NULL COMMENT '数量',
    register_datetime DATETIME NOT NULL COMMENT '登録日時',
    update_datetime DATETIME NOT NULL COMMENT '更新日時',
    PRIMARY KEY (sales_date, product_code)
) COMMENT='売上テーブル';