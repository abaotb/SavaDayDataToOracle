package com.tangb.domain;

/**
 * 日行情数据结构
 * @author tangbao
 * 2015-11-24下午2:00:49
 */
public class DataJavaBean {

	public String exchange; // 交易所
	public String contract; // 合约
	public String date;// 日期
	public String prev_Close;// 前收盘
	public String prev_Settle;// 前结算
	public String open_Pri;// 开盘价
	public String high_Pri;// 最高价
	public String low_Pri;// 最低价
	public String close_Pri;// 收盘价
	public String settle_Pri;// 结算价
	public String close_Range;// 涨跌一
	public String settle_Range;// 涨跌二
	public String volume;// 交易量
	public String turnover;// 成交额
	public String oI;// 持仓量
	public DataJavaBean(String exchange, String contract, String date,
			String prev_Close, String prev_Settle, String open_Pri,
			String high_Pri, String low_Pri, String close_Pri,
			String settle_Pri, String close_Range, String settle_Range,
			String volume, String turnover, String oI) {
		super();
		this.exchange = exchange;
		this.contract = contract;
		this.date = date;
		this.prev_Close = prev_Close;
		this.prev_Settle = prev_Settle;
		this.open_Pri = open_Pri;
		this.high_Pri = high_Pri;
		this.low_Pri = low_Pri;
		this.close_Pri = close_Pri;
		this.settle_Pri = settle_Pri;
		this.close_Range = close_Range;
		this.settle_Range = settle_Range;
		this.volume = volume;
		this.turnover = turnover;
		this.oI = oI;
	}
	@Override
	public String toString() {
		return "DataJavaBean [exchange=" + exchange + ", contract=" + contract
				+ ", date=" + date + ", prev_Close=" + prev_Close
				+ ", prev_Settle=" + prev_Settle + ", open_Pri=" + open_Pri
				+ ", high_Pri=" + high_Pri + ", low_Pri=" + low_Pri
				+ ", close_Pri=" + close_Pri + ", settle_Pri=" + settle_Pri
				+ ", close_Range=" + close_Range + ", settle_Range="
				+ settle_Range + ", volume=" + volume + ", turnover="
				+ turnover + ", oI=" + oI + "]";
	}
	

}
