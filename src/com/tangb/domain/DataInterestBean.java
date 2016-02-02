package com.tangb.domain;

/**
 * 日持仓排名数据结构
 * 
 * @author tangbao 2015-11-24下午2:01:15
 */
public class DataInterestBean {
	public String exchange; // 交易所
	public String date; // 日期
	public String rank; // 排名
	public String contract; // 合约

	public String namevolume; // 成交量中文名
	public String volume;// 成交量
	public String rangevolume;// 成交量跌幅

	public String nameholdingbuy; // 持买仓量中文名
	public String holdingbuy;// 持买仓量
	public String rangeholdingbuy;// 持买仓量跌幅

	public String nameholdingsell; // 持卖仓量中文名
	public String holdingsell;// 持卖仓量
	public String rangeholdingsell;// 持卖仓量跌幅
	public DataInterestBean(String exchange, String date, String rank,
			String contract, String namevolume, String volume,
			String rangevolume, String nameholdingbuy, String holdingbuy,
			String rangeholdingbuy, String nameholdingsell, String holdingsell,
			String rangeholdingsell) {
		super();
		this.exchange = exchange;
		this.date = date;
		this.rank = rank;
		this.contract = contract;
		this.namevolume = namevolume;
		this.volume = volume;
		this.rangevolume = rangevolume;
		this.nameholdingbuy = nameholdingbuy;
		this.holdingbuy = holdingbuy;
		this.rangeholdingbuy = rangeholdingbuy;
		this.nameholdingsell = nameholdingsell;
		this.holdingsell = holdingsell;
		this.rangeholdingsell = rangeholdingsell;
	}
	@Override
	public String toString() {
		return "DataInterestBean [exchange=" + exchange + ", date=" + date
				+ ", rank=" + rank + ", contract=" + contract + ", namevolume="
				+ namevolume + ", volume=" + volume + ", rangevolume="
				+ rangevolume + ", nameholdingbuy=" + nameholdingbuy
				+ ", holdingbuy=" + holdingbuy + ", rangeholdingbuy="
				+ rangeholdingbuy + ", nameholdingsell=" + nameholdingsell
				+ ", holdingsell=" + holdingsell + ", rangeholdingsell="
				+ rangeholdingsell + "]";
	}

	
}
