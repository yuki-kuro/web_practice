CREATE TABLE m_product(
    product_code INT AUTO_INCREMENT COMMENT '商品コード',
    product_name VARCHAR(50) NOT NULL COMMENT '商品名',
    price INT NOT NULL COMMENT '単価',
    register_datetime DATETIME NOT NULL COMMENT '登録日時',
    update_datetime DATETIME NOT NULL COMMENT '更新日時',
    delete_datetime DATETIME COMMENT '削除日時',
    PRIMARY KEY (product_code)
) COMMENT='商品マスタ';