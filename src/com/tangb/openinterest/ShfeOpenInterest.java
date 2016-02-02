package com.tangb.openinterest;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class ShfeOpenInterest implements Runnable {

	// 第一次加载
	private boolean firstFlag;
	// 日期
	private String shfeinterestdate;
	// 交易所地址
	private String shfeinterestpath;
	// 数据库对象
	private JdbcConnect shfejdbc;
	// 时间间隔
	private int INTERVAL = 5000;

	public ShfeOpenInterest() {
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
		ArrayList<DataInterestBean> shfeDataBeanList = new ArrayList<>();

		// 拿到Json对象
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(webcontent);
			// 拿到数组
			JSONArray jsonList = jsonObject.getJSONArray("o_cursor");
			for (int i = 0; i < jsonList.length(); i++) {
				// System.out.println(jsonList.get(i).toString());
				// System.out.println("-----------------------------");
				JSONObject object = (JSONObject) jsonList.get(i);

				if (Integer.parseInt((object.get("RANK").toString().trim())) <= 0
						|| Integer.parseInt((object.get("RANK").toString()
								.trim())) > 20) { // 排名不可能小于0,大于20
					continue;
				}
				String exchange = "SHFE";
				// String date=date;
				String rank = object.get("RANK").toString().trim(); // 名次
				String contract = object.get("INSTRUMENTID").toString().trim();

				String namevolume = object.get("PARTICIPANTABBR1").toString()
						.trim();
				String volume = object.get("CJ1_CHG").toString().trim();
				String rangevolume = object.get("RANK").toString().trim();

				String nameholdingbuy = object.get("PARTICIPANTABBR2")
						.toString().trim();
				String holdingbuy = object.get("CJ2").toString().trim();
				String rangeholdingbuy = object.get("CJ2_CHG").toString()
						.trim();

				String nameholdingsell = object.get("PARTICIPANTABBR3")
						.toString().trim();
				String holdingsell = object.get("CJ3").toString().trim();
				String rangeholdingsell = object.get("CJ3_CHG").toString()
						.trim();

				DataInterestBean dib = new DataInterestBean(exchange, date,
						rank, contract, namevolume, volume, rangevolume,
						nameholdingbuy, holdingbuy, rangeholdingbuy,
						nameholdingsell, holdingsell, rangeholdingsell);

				shfeDataBeanList.add(dib);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shfeDataBeanList;
	}

	/**
	 * 拼接path
	 * 
	 * @param path
	 * @param param
	 * @return
	 */
	private String jointPath(String path, String date) {
		// http://www.shfe.com.cn/data/dailydata/kx/pm,20151119,.dat
		StringBuilder sBuilder = new StringBuilder();
		String[] split = path.split(",");
		sBuilder.append(split[0]);
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
	private void ShfeExchange(String shfepath, String date) {
		// System.out.println(czcepath);
		// String[] splitsStrings = czcepath.split("+");
		// 郑州对象
		//ShfeOpenInterest cdl = new ShfeOpenInterest();

		// 郑州路径
		String path = this.jointPath(shfepath, date);//cdl.jointPath(shfepath, date);

		// System.out.println(path);

		// 从网上下载数据
		// String webcontent = cdl.ConnectExchange_Get(path);
		String webcontent = Utils.getRequest(path, "utf-8");

		// 去掉特殊字符
		webcontent = webcontent.replace("&nbsp;", "");
		// System.out.println(webcontent);
		// 解析数据
		ArrayList<DataInterestBean> shfeDataBean = this.handleWebContent(webcontent, date);//cdl.handleWebContent(webcontent, date);

		this.HandleJdbc(shfeDataBean,"shfeinterestdate", shfeinterestdate);

	}

	/**
	 * 操作数据库
	 * 
	 * @param czceDataBean
	 */
	private void HandleJdbc(ArrayList<DataInterestBean> shfeDataBean,String exchange, String date) {
		if (shfeDataBean.size() == 0) {
			//System.out.println("ShfeOpenInterest没数据");
			return;
		}
		System.out.println("ShfeOpenInterest" + ":"
				+ shfeDataBean.get(0).date);
		// for (DataInterestBean dbean : shfeDataBean) {
		// System.out.println(dbean.toString());
		// }

		if (!firstFlag) {
			shfejdbc = new JdbcConnect();
			// 连接数据库
			shfejdbc.connetionDB();
			// 先删
			shfejdbc.deleteDB2(shfeinterestdate, "SHFE");
			// 关闭数据库
			shfejdbc.closeDB();
			firstFlag = true;
		}
		if (shfejdbc == null) {
			shfejdbc = new JdbcConnect();
		}
		// 连接数据库
		shfejdbc.connetionDB();
		// 插入数据库
//		shfejdbc.insertDB2(shfeDataBean);
		shfejdbc.insertDB_Batch(shfeDataBean);
		// 查询数据库
		// jdbc.queryDB();
		// 关闭数据库
		shfejdbc.closeDB();
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
				shfeinterestdate = Main.properties
						.getProperty("shfeinterestdate");
				// 地址
				shfeinterestpath = Main.properties
						.getProperty("shfeinterestpath");
				// 是否是交易日
				if (Utils.isTradingDay("shfeinterestdate", shfeinterestdate)) { // &&"20151111".compareTo(czcedate)>0

					ShfeExchange(shfeinterestpath, shfeinterestdate);
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
