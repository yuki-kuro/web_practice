package model;

import java.util.Date;

/**
 * 商品マスタ（m_product）および売上テーブル（t_sales）の情報を保持するエンティティクラス。
 */
public class Product {
	private int productCode;
	private String productName;
	private int price;
	// 楽観的排他制御のために使用。DBから文字列で取得し、更新前後を比較する。
	private String updateDatetime;
	// 売上登録時の数量。m_product には存在しないが、売上画面での一時保持に使用する。
	private int quantity;
	private Date salesDate;

	public Date getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(String updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	public int getProductCode() {
		return productCode;
	}

	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}
