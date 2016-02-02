package com.tangb.openinterest;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tangb.db.JdbcConnect;
import com.tangb.domain.DataInterestBean;
import com.tangb.main.Main;
import com.tangb.utils.Utils;

/**
 * 从网上下载数据
 * 
 * @author tangbao 2015-11-13下午4:10:23
 */
public class CffexOpenInterest implements Runnable {

	// 第一次加载
	private boolean firstFlag;
	// 日期
	private String cffexinterestdate;
	// 交易所地址
	private String cffexinterestpath;
	// 数据库对象
	private JdbcConnect cffexjdbc;
	// 时间间隔
	private int INTERVAL = 5000;
	// 交易品种
	private String[] urlpath = new String[] { "IF", "TF", "IC", "IH", "T" };
	// 交易品种的xml文件内容
	private String[] webContents = new String[5];

	public CffexOpenInterest() {
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
	private ArrayList<DataInterestBean> handleWebContent(String[] webcontents,
			String date) {
		ArrayList<DataInterestBean> cffexDataBeanList = new ArrayList<>();
		if (webcontents == null || webcontents.length <= 0) {
			return cffexDataBeanList;
		}
		for (String webcontent : webcontents) {
			Document doc = (Document) Jsoup.parse(webcontent);
			Elements dailydataList = ((Element) doc).getElementsByTag("data");
			// 当天数据还没出来
			if (dailydataList.size() == 0) {
				return cffexDataBeanList;
			}
			for (int i = 0; i < dailydataList.size(); i += 3) {
				Element dailydata = dailydataList.get(i);
				// 取数据
				String exchange = "CFFEX";
				// String date=date;
				String rank = dailydata.getElementsByTag("rank").text().trim(); // 名次
				String contract = dailydata.getElementsByTag("instrumentId")
						.text().trim();
				;
				String namevolume = dailydata.getElementsByTag("shortname")
						.text().trim();
				String volume = dailydata.getElementsByTag("volume").text()
						.trim();
				String rangevolume = dailydata.getElementsByTag("varVolume")
						.text().trim();

				dailydata = dailydataList.get(i + 1);

				String nameholdingbuy = dailydata.getElementsByTag("shortname")
						.text().trim();
				String holdingbuy = dailydata.getElementsByTag("volume").text()
						.trim();
				String rangeholdingbuy = dailydata
						.getElementsByTag("varVolume").text().trim();

				dailydata = dailydataList.get(i + 2);

				String nameholdingsell = dailydata
						.getElementsByTag("shortname").text().trim();
				String holdingsell = dailydata.getElementsByTag("volume")
						.text().trim();
				String rangeholdingsell = dailydata
						.getElementsByTag("varVolume").text().trim();

				DataInterestBean dib = new DataInterestBean(exchange, date,
						rank, contract, namevolume, volume, rangevolume,
						nameholdingbuy, holdingbuy, rangeholdingbuy,
						nameholdingsell, holdingsell, rangeholdingsell);

				cffexDataBeanList.add(dib);
			}
		}

		return cffexDataBeanList;
	}

	/**
	 * 拼接path
	 * 
	 * @param path
	 * @param param
	 * @return
	 */
	private String[] jointPath(String path, String date) {
		String[] result = new String[urlpath.length];
		// http://www.cffex.com.cn/fzjy/ccpm/,201411/19,/TF,.xml
		for (int i = 0; i < urlpath.length; i++) {
			StringBuilder sBuilder = new StringBuilder();

			String[] split = path.split(",");
			sBuilder.append(split[0]);

			sBuilder.append(date.substring(0, 6));
			sBuilder.append("/");

			sBuilder.append(date.substring(6));

			sBuilder.append("/");

			sBuilder.append(urlpath[i]);

			sBuilder.append(split[3]);

			result[i] = sBuilder.toString();
		}
		return result;
	}

	/**
	 * 获取数据
	 * 
	 * @param czcepath
	 * @param date
	 */
	private void CffexExchange(String cffexpath, String date) {
		// System.out.println(czcepath);
		// String[] splitsStrings = czcepath.split("+");
		// 郑州对象
		// CffexOpenInterest cdl = new CffexOpenInterest();

		// 郑州路径
		String[] path = this.jointPath(cffexpath, date);// cdl.jointPath(cffexpath,
														// date);

		// System.out.println(path);

		// 从网上下载数据
		// String webcontent = cdl.ConnectExchange_Get(path);
		String[] webcontent = this.getRequest(path, "utf-8");// Utils.getRequest(path,
																// "gb2312");

		// 去掉特殊字符
		// webcontent = webcontent.replace("&nbsp;", "");
		// System.out.println(webcontent);
		// 解析数据
		ArrayList<DataInterestBean> cffexDataBean = this.handleWebContent(
				webcontent, date); // cdl.handleWebContent(webcontent, date);

		this.HandleJdbc(cffexDataBean, "cffexinterestdate", cffexinterestdate);

	}

	/**
	 * GET 请求交易所
	 * 
	 * @param urlpath2
	 * @param string
	 * @return
	 */
	private String[] getRequest(String[] urlpath, String encoding) {
		for (int i = 0; i < urlpath.length; i++) {
			String path = urlpath[i];
			String webContent = "";
			HttpURLConnection conn = null;
			try {
				URL url = new URL(path);
				// 获取连接对象
				conn = (HttpURLConnection) url.openConnection();
				// 设置连接属性
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(50000);
				conn.setReadTimeout(50000);
				// 建立连接，获取响应吗
				if (conn.getResponseCode() == 200) {
					// 1.拿到服务器返回的输入流
					InputStream is = conn.getInputStream();
					// 字节流转换为字符流
					webContent = Utils.getTextFromStream(is, encoding);
					webContents[i] = webContent;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
		return webContents;
	}

	/**
	 * 操作数据库
	 * 
	 * @param czceDataBean
	 */
	private void HandleJdbc(ArrayList<DataInterestBean> cffexDataBean,
			String exchange, String date) {
		if (cffexDataBean.size() == 0) {
			//System.out.println("CffexOpenInterest没数据");
			return;
		}
		System.out.println("CffexOpenInterest" + ":"
				+ cffexDataBean.get(0).date);
		// for (DataInterestBean dbean : cffexDataBean) {
		// System.out.println(dbean.toString());
		// }
		if (!firstFlag) {
			cffexjdbc = new JdbcConnect();
			// 连接数据库
			cffexjdbc.connetionDB();
			// 先删
			cffexjdbc.deleteDB2(cffexinterestdate, "CFFEX");
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
		// cffexjdbc.insertDB2(cffexDataBean);
		cffexjdbc.insertDB_Batch(cffexDataBean);
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
				cffexinterestdate = Main.properties
						.getProperty("cffexinterestdate");
				// 地址
				cffexinterestpath = Main.properties
						.getProperty("cffexinterestpath");
				// 是否是交易日
				if (Utils.isTradingDay("cffexinterestdate", cffexinterestdate)) { // &&"20151111".compareTo(czcedate)>0

					CffexExchange(cffexinterestpath, cffexinterestdate);
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
