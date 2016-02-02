package com.tangb.openinterest;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tangb.db.JdbcConnect;
import com.tangb.domain.DataInterestBean;
import com.tangb.domain.DataJavaBean;
import com.tangb.main.Main;
import com.tangb.utils.Utils;

/**
 * 从网上下载数据
 * 
 * @author tangbao 2015-11-13下午4:10:23
 */
public class CzceOpenInterest implements Runnable {

	// 第一次加载
	private boolean firstFlag;
	// 日期
	private String czceinterestdate;
	// 交易所地址
	private String czceinterestpath;
	// 数据库对象
	private JdbcConnect czcejdbc;
	// 时间间隔
	private int INTERVAL = 5000;

	public CzceOpenInterest() {
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
	private ArrayList<DataInterestBean> handleWebContent(String webcontent,
			String date) {
		ArrayList<DataInterestBean> czceDataBeanList = new ArrayList<>();
		// javabean
		// 郑州
		// String webcontent =
		// dfw.ConnectExchange("http://www.czce.com.cn/portal/DFSStaticFiles/Future/2015/20151112/FutureDataDaily.htm",
		// null);
		// System.out.println(webcontent);
		// webcontent = webcontent.

		Document doc = (Document) Jsoup.parse(webcontent);
		Elements tableList = ((Element) doc).select("table");
		// 当天数据还没出来
		if (tableList == null || "".equals(webcontent)) {
			return czceDataBeanList;
		}

		Elements trList = ((Element) doc).getElementsByTag("tr");
		// System.out.println(trList.get(1).toString());
		String contract = "";
		for (int i = 1; i < trList.size(); i++) {
			Elements tdList = trList.get(i).getElementsByTag("td");
			if (tdList.size() == 1) {
				// 新表，取品种
				String contracttemp = tdList.get(0).text().trim();
				// System.out.println("123adf*_&^".replaceAll("[^\\w]|_",""));
				// //123adf
				int pos = contracttemp.indexOf("日期");
				// int pos2 = contracttemp.indexOf(" ", pos1);
				// System.out.println("sdsd"+contracttemp.indexOf(2));
				contract = contracttemp.substring(0, pos).replaceAll(
						"[^\\w]|_", "");
				// System.out.println(contract);
				continue;
			} else if ("".equals(tdList.get(1).text())
					|| "名次".equals(tdList.get(0).text())) {
				// 不用取
				continue;
			} else {
				// 取数据
				String exchange = "CZCE";
				// String date=date;
				String rank = tdList.get(0).text().trim(); // 名次
				// String contract;
				String namevolume = tdList.get(1).text().trim();
				String volume = tdList.get(2).text().trim();
				String rangevolume = tdList.get(3).text().trim();

				String nameholdingbuy = tdList.get(4).text().trim();
				String holdingbuy = tdList.get(5).text().trim();
				String rangeholdingbuy = tdList.get(6).text().trim();

				String nameholdingsell = tdList.get(7).text().trim();
				String holdingsell = tdList.get(8).text().trim();
				String rangeholdingsell = tdList.get(9).text().trim();

				DataInterestBean dib = new DataInterestBean(exchange, date,
						rank, contract, namevolume, volume, rangevolume,
						nameholdingbuy, holdingbuy, rangeholdingbuy,
						nameholdingsell, holdingsell, rangeholdingsell);
				
				czceDataBeanList.add(dib);
			}
		}
		return czceDataBeanList;
	}

	/**
	 * 拼接path
	 * 
	 * @param path
	 * @param param
	 * @return
	 */
	private String jointPath(String path, String date) {
		// String result =
		// "http://www.czce.com.cn/portal/DFSStaticFiles/Future/,2015/20151112,/FutureDataDaily.htm";
		StringBuilder sBuilder = new StringBuilder();
		String[] split = path.split(",");
		sBuilder.append(split[0]);

		sBuilder.append(date.substring(0, 4));
		sBuilder.append("/");

		sBuilder.append(date);
		sBuilder.append(split[2]);

		return sBuilder.toString();
	}

	/**
	 * 获取数据
	 * 
	 * @param czcepath
	 * @param date
	 */
	private void CzceExchange(String czcepath, String date) {
		// System.out.println(czcepath);
		// String[] splitsStrings = czcepath.split("+");
		// 郑州对象
		//CzceOpenInterest cdl = new CzceOpenInterest();

		// 郑州路径
		String path = this.jointPath(czcepath, date); //cdl.jointPath(czcepath, date);

		// System.out.println(path);

		// 从网上下载数据
		// String webcontent = cdl.ConnectExchange_Get(path);
		String webcontent = Utils.getRequest(path, "gb2312");

		// 去掉特殊字符
		webcontent = webcontent.replace("&nbsp;", "");
		// System.out.println(webcontent);
		// 解析数据
		ArrayList<DataInterestBean> czceDataBean = this.handleWebContent(
				webcontent, date);				//cdl.handleWebContent(webcontent, date);

		this.HandleJdbc(czceDataBean,"czceinterestdate", czceinterestdate);

	}

	/**
	 * 操作数据库
	 * 
	 * @param czceDataBean
	 */
	private void HandleJdbc(ArrayList<DataInterestBean> czceDataBean,String exchange, String date) {
		if (czceDataBean.size() == 0) {
			//System.out.println("CzceOpenInterest没数据");
			return;
		}
		System.out.println("CzceOpenInterest" + ":"
				+ czceDataBean.get(0).date);
//		for (DataInterestBean dbean : czceDataBean) {
//			 System.out.println(dbean.toString());
//		}
	
		if (!firstFlag) {
			czcejdbc = new JdbcConnect();
			// 连接数据库
			czcejdbc.connetionDB();
			// 先删
			czcejdbc.deleteDB2(czceinterestdate, "CZCE");
			// 关闭数据库
			czcejdbc.closeDB();
			firstFlag = true;
		}
		if (czcejdbc == null) {
			czcejdbc = new JdbcConnect();
		}
		// 连接数据库
		czcejdbc.connetionDB();
		// 插入数据库
//		czcejdbc.insertDB2(czceDataBean);
		czcejdbc.insertDB_Batch(czceDataBean);
		// 查询数据库
		// jdbc.queryDB();
		// 关闭数据库
		czcejdbc.closeDB();
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
				czceinterestdate = Main.properties
						.getProperty("czceinterestdate");
				// 地址
				czceinterestpath = Main.properties
						.getProperty("czceinterestpath");
				// 是否是交易日
				if (Utils.isTradingDay("czceinterestdate", czceinterestdate)) { // &&"20151111".compareTo(czcedate)>0

					CzceExchange(czceinterestpath, czceinterestdate);
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
