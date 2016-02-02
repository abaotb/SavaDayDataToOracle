package com.tangb.download;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tangb.db.JdbcConnect;
import com.tangb.domain.DataJavaBean;
import com.tangb.main.Main;
import com.tangb.utils.Utils;

/**
 * 从网上下载数据
 * 
 * @author tangbao 2015-11-13下午4:10:23
 */
public class ShfeDownLoad implements Runnable {

	// 第一次加载
	private boolean firstFlag;
	// 日期
	private String shfedate;
	// 交易所 地址
	private String shfepath;
	// 数据库对象
	private JdbcConnect shfejdbc;
	// 时间间隔
	private int INTERVAL =  Main.interval;

	public ShfeDownLoad() {
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
		ArrayList<DataJavaBean> shfeDataBeanList = null;
		try {

			if ("".equals(webcontent)) {
				return shfeDataBeanList;
			}
			shfeDataBeanList = new ArrayList<DataJavaBean>();
			// 拿到Json对象
			JSONObject jsonObject = new JSONObject(webcontent);

			// 拿到数组
			JSONArray jsonList = jsonObject.getJSONArray("o_curinstrument");

			// System.err.println(jsonList.toString());
			for (int i = 0; i < jsonList.length() - 1; i++) {
				JSONObject object = (JSONObject) jsonList.get(i);

				if ("".equals((object.get("SETTLEMENTPRICE")))) { // 计算价不可能为0
																	// 也可防止今天的数据还没出
					continue;
				}
				// 有些位置为空 Convert.ToDouble("")不能为0;
				Iterator iterator = object.keys();
				String key = null;
				String value = null;

				// 去除空的值，置为0
				while (iterator.hasNext()) {

					key = (String) iterator.next();
					value = object.getString(key);
					if ("".equals(value)) {
						object.put(key, "0");
					}

				}

				String exchange = "SHFE";

				// 切割contract
				int temp1 = object.get("PRODUCTID").toString().trim()
						.indexOf("_");
				String temp2 = object.get("PRODUCTID").toString().trim()
						.substring(0, temp1);
				String contract = temp2
						+ object.get("DELIVERYMONTH").toString().trim();

				// String date = "0";
				String prev_Close = "0";// 前收盘
				String open_Pri = object.get("OPENPRICE").toString().trim();// 开盘价
				String high_Pri = object.get("HIGHESTPRICE").toString().trim();// 最高价
				String low_Pri = object.get("LOWESTPRICE").toString().trim();// 最低价
				String close_Pri = object.get("CLOSEPRICE").toString().trim();// 收盘价

				String prev_Settle = object.get("PRESETTLEMENTPRICE")
						.toString().trim();// 前结算
				String settle_Pri = object.get("SETTLEMENTPRICE").toString()
						.trim();// 结算价

				String close_Range = object.get("ZD1_CHG").toString().trim(); // 涨跌一
				String settle_Range = object.get("ZD2_CHG").toString().trim(); // 涨跌二

				String volume = object.get("VOLUME").toString().trim();// 交易量
				// 没有成交额
				String turnover = "0";// object.get("TURNOVER").toString();//
										// 成交额
				String oI = object.get("OPENINTEREST").toString().trim();// 持仓量

				DataJavaBean cbd = new DataJavaBean(exchange, contract, date,
						prev_Close, prev_Settle, open_Pri, high_Pri, low_Pri,
						close_Pri, settle_Pri, close_Range, settle_Range,
						volume, turnover, oI);
				shfeDataBeanList.add(cbd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shfeDataBeanList;
	}

	/**
	 * 将Json对象转换成Map
	 * 
	 * @param jsonObject
	 *            json对象
	 * @return Map对象
	 * @throws JSONException
	 */
	public Map toMap(String jsonString) throws JSONException {

		JSONObject jsonObject = new JSONObject(jsonString);

		Map result = new HashMap();
		Iterator iterator = jsonObject.keys();
		String key = null;
		String value = null;

		while (iterator.hasNext()) {

			key = (String) iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);

		}
		return result;

	}

	/**
	 * 拼接path
	 * 
	 * @param path
	 * @param date
	 * @return
	 */
	private String jointPath(String path, String date) {
		// "http://www.shfe.com.cn/data/dailydata/,20151012,.dat"
		StringBuilder sBuilder = new StringBuilder();
		String[] split = path.split(",");
		sBuilder.append(split[0]);
		sBuilder.append(date);
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
	private void ShfeExchange(String cffexpath, String date) {
		// System.out.println(czcepath);
		// String[] splitsStrings = czcepath.split("+");

		// 郑州路径
		String path = this.jointPath(cffexpath, date);

		// System.out.println(path);

		// 从网上下载数据
		// String webcontent =
		// DceDownLoad.ConnectExchange_Post("http://www.dce.com.cn/PublicWeb/MainServlet",
		// "action=Pu00011_result&Pu00011_Input.trade_date=20151112&Pu00011_Input.variety=all&Pu00011_Input.trade_type=0");
		// String webcontent = sdl.ConnectExchange_Get(path);
		String webcontent = Utils.getRequest(path, "UTF-8");

		// 去掉特殊字符
		webcontent = webcontent.replace("&nbsp;", "");

		// System.out.println(webcontent);

		// 解析数据
		ArrayList<DataJavaBean> shfeDataBean = this.handleWebContent(webcontent,
				date);

		this.HandleJdbc(shfeDataBean, "shfedate", shfedate);
	}

	/**
	 * 操作数据库
	 * @param shfeDataBean
	 */
	private void HandleJdbc(ArrayList<DataJavaBean> shfeDataBean,String exchange,String date) {
		if (shfeDataBean == null) {
			//System.out.println("ShfeDownLoad没数据");
			return;
		}
		// for (DataJavaBean dbean : databean) {
		System.out.println("上期所:"
				+ shfeDataBean.get(0).date);
		// System.out.println(databean.toString());
		// }
		if (!firstFlag) {
			shfejdbc = new JdbcConnect();
			// 连接数据库
			shfejdbc.connetionDB();
			// 先删
			shfejdbc.deleteDB(shfedate, "SHFE");
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
		shfejdbc.insertDB(shfeDataBean);
		// 查询数据库
		// jdbc.queryDB();
		// 关闭数据库
		shfejdbc.closeDB();
		//修改日期
		String dateString = Utils.addDate(date);
		Utils.writePropertiesFile(exchange, dateString);
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				// 日期
				shfedate = Main.properties.getProperty("shfedate");
				// 地址
				shfepath = Main.properties.getProperty("shfepath");
				// 是否是交易日
				if (Utils.isTradingDay("shfedate", shfedate)) {

					ShfeExchange(shfepath, shfedate);
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
