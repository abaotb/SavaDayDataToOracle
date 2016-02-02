package com.tangb.download;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tangb.db.JdbcConnect;
import com.tangb.domain.DataJavaBean;
import com.tangb.main.Main;
import com.tangb.utils.Utils;

/**
 * 从网上下载数据
 * 
 * @author tangbao 2015-11-13下午4:10:23
 */
public class CffexDownLoad implements Runnable {

	// 第一次加载
	private boolean firstFlag;
	// 日期
	private String cffexdate;
	// 交易所地址
	private String cffexpath;
	// 数据库对象
	private JdbcConnect cffexjdbc;
	//时间间隔
	private int INTERVAL =  Main.interval;
	
	public CffexDownLoad() {
		super();
	}

	/**
	 * 处理网络内容
	 * 
	 * @param date
	 * 
	 * @param webcontent网络内容
	 * @return javaBean
	 */
	private ArrayList<DataJavaBean> handleWebContent(String webcontent,
			String date) {
		ArrayList<DataJavaBean> cffexDataBeanList = null;
		Document doc = (Document) Jsoup.parse(webcontent);

		Elements dailydataList = ((Element) doc).getElementsByTag("dailydata");
		// 当天数据还没出来
		if (dailydataList.size() == 0) {
			return cffexDataBeanList;
		}
		cffexDataBeanList = new ArrayList<DataJavaBean>();
		for (Element dailydata : dailydataList) {

			// System.out.println(dailydata.toString());
			// System.out.println("------------------------------");
			String exchange = "CFFEX";
			String contract = dailydata.getElementsByTag("instrumentid").text()
					.trim();
			// String date = "0";
			String prev_Close = "0";// 前收盘

			String open_Pri = dailydata.getElementsByTag("openprice").text()
					.trim();// 开盘价
			String high_Pri = dailydata.getElementsByTag("highestprice").text()
					.trim();// 最高价
			String low_Pri = dailydata.getElementsByTag("lowestprice").text()
					.trim();// 最低价
			String close_Pri = dailydata.getElementsByTag("closeprice").text()
					.trim();// 收盘价
			String prev_Settle = dailydata
					.getElementsByTag("presettlementprice").text().trim();// 前结算
			String settle_Pri = dailydata.getElementsByTag("settlementprice")
					.text().trim();// 结算价
			String close_Range = ""
					+ (Float.parseFloat(close_Pri) - Float
							.parseFloat(prev_Settle));// 涨跌一
			String settle_Range = ""
					+ (Float.parseFloat(settle_Pri) - Float
							.parseFloat(prev_Settle));// 涨跌二

			String volume = dailydata.getElementsByTag("volume").text().trim();// 交易量
			String turnover = dailydata.getElementsByTag("turnover").text()
					.trim();// 成交额
			String oI = dailydata.getElementsByTag("openinterest").text()
					.trim();// 持仓量
			DataJavaBean cbd = new DataJavaBean(exchange, contract, date,
					prev_Close, prev_Settle, open_Pri, high_Pri, low_Pri,
					close_Pri, settle_Pri, close_Range, settle_Range, volume,
					turnover, oI);
			cffexDataBeanList.add(cbd);
		}
		return cffexDataBeanList;
	}

	/**
	 * 拼接path
	 * 
	 * @param path
	 * @param date
	 * @return
	 */
	// 中金所
	// http://www.cffex.com.cn/fzjy/mrhq/201511/12/index.xml

	private String jointPath(String path, String date) {
		// String result =
		// "http://www.cffex.com.cn/fzjy/mrhq/,201511/12,/index.xml";
		StringBuilder sBuilder = new StringBuilder();
		String[] split = path.split(",");
		sBuilder.append(split[0]);
		sBuilder.append(date.substring(0, 6));
		sBuilder.append("/");

		sBuilder.append(date.substring(6));
		sBuilder.append(split[2]);

		String content = sBuilder.toString();
		sBuilder = null;
		return content;
	}

	/**
	 * 获取数据
	 * @param cffexpath
	 * @param date
	 */
	private void CffexExchange(String cffexpath, String date) {
		// System.out.println(czcepath);
		// String[] splitsStrings = czcepath.split("+");

		// 郑州路径
		String path = this.jointPath(cffexpath, date);

		// System.out.println(path);

		// 从网上下载数据
		// String webcontent =
		// DceDownLoad.ConnectExchange_Post("http://www.dce.com.cn/PublicWeb/MainServlet",
		// "action=Pu00011_result&Pu00011_Input.trade_date=20151112&Pu00011_Input.variety=all&Pu00011_Input.trade_type=0");
		// String webcontent = cfdl.ConnectExchange_Get(path);
		String webcontent = Utils.getRequest(path, "gb2312");

		// 去掉特殊字符
		webcontent = webcontent.replace("&nbsp;", "");

		// System.out.println(webcontent);

		// 解析数据
		ArrayList<DataJavaBean> cffexDataBean = this.handleWebContent(
				webcontent, date);

		this.HandleJdbc(cffexDataBean,"cffexdate", cffexdate);
	}

	/**
	 * 操作数据库
	 * @param cffexDataBean
	 */
	private void HandleJdbc(ArrayList<DataJavaBean> cffexDataBean,String exchange, String date) {
		if (cffexDataBean == null) {
			//System.out.println("CffexDownLoad没数据");
			return;
		}
		// for (DataJavaBean dbean : databean) {
		System.out.println("中金所:"
				+ cffexDataBean.get(0).date);
		// System.out.println(databean.toString());
		// }
		if (!firstFlag) {
			 cffexjdbc = new JdbcConnect();
			// 连接数据库
			cffexjdbc.connetionDB();
			// 先删
			cffexjdbc.deleteDB(cffexdate, "CFFEX");

			// 关闭数据库
			cffexjdbc.closeDB();
			firstFlag = true;
		}
		if (cffexjdbc == null) {
			cffexjdbc = new JdbcConnect();
		}
		// 连接数据库
		cffexjdbc.connetionDB();
		// 插入数据库
		cffexjdbc.insertDB(cffexDataBean);
		// 查询数据库
		// jdbc.queryDB();
		// 关闭数据库
		cffexjdbc.closeDB();
		// 修改日期
		String dateString = Utils.addDate(date);
		Utils.writePropertiesFile(exchange, dateString);
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				// 日期
				cffexdate = Main.properties.getProperty("cffexdate");
				// 地址
				cffexpath = Main.properties.getProperty("cffexpath");
				// 是否是交易日
				if (Utils.isTradingDay("cffexdate", cffexdate)) {

					CffexExchange(cffexpath, cffexdate);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 1000, INTERVAL);
	}

}
