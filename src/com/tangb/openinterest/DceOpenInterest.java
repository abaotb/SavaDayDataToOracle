package com.tangb.openinterest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
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
public class DceOpenInterest implements Runnable {

	// 第一次加载
	private boolean firstFlag;
	// 日期
	private String dceinterestdate;
	// 交易所地址
	private String dceinterestpath;
	// 数据库对象
	private JdbcConnect dcejdbc;
	// 时间间隔
	private int INTERVAL = 5000;

	public DceOpenInterest() {
		super();
	}

	/**
	 * 是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isCanParseInt(String str) {
		if (str == null) {// 非空验证
			return false;
		}
		return str.matches("\\d+");// 正则表达式判断该字串值是否为数字
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
		ArrayList<DataInterestBean> dceDataBeanList = new ArrayList<>();
		// 当天数据还没出来
		if ("".equals(webcontent)) {
			return dceDataBeanList;
		}
		String[] split_webcontent = webcontent.split("_");
		for (int i = 0; i < split_webcontent.length; i++) {
			// 合约名
			String contract = split_webcontent[i].split("==")[0];

			Document doc = (Document) Jsoup.parse(split_webcontent[i]);

			Elements trList = ((Element) doc).getElementsByTag("tr");
			if (trList == null || trList.size() < 10) {
				continue;
			} else {
				for (int j = 1; j < trList.size(); j++) {
					Elements tdList = trList.get(j).getElementsByTag("td");

					if (isCanParseInt(tdList.get(0).text().trim())) { // 第一列如果是数字
						String exchange = "DCE";
						// String date=date;
						String rank = tdList.get(0).text().trim(); // 名次
						// String contract;
						String namevolume = tdList.get(1).text().trim();
						String volume = tdList.get(2).text().trim();
						String rangevolume = tdList.get(3).text().trim();

						String nameholdingbuy = tdList.get(5).text().trim();
						String holdingbuy = tdList.get(6).text().trim();
						String rangeholdingbuy = tdList.get(7).text().trim();

						String nameholdingsell = tdList.get(9).text().trim();
						String holdingsell = tdList.get(10).text().trim();
						String rangeholdingsell = tdList.get(11).text().trim();
						DataInterestBean dib = new DataInterestBean(exchange,
								date, rank, contract, namevolume, volume,
								rangevolume, nameholdingbuy, holdingbuy,
								rangeholdingbuy, nameholdingsell, holdingsell,
								rangeholdingsell);

						dceDataBeanList.add(dib);
					}
				}
			}
		}
		return dceDataBeanList;
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
		sBuilder.append(date);
		sBuilder.append(split[2]);

		return sBuilder.toString();
	}

	/**
	 * 获取数据
	 * 
	 * @param dcepath
	 * @param date
	 */
	private void DceExchange(String dcepath, String date) {
		// System.out.println(dcepath);
		// String[] splitsStrings = dcepath.split("+");
		// 郑州对象
		//DceOpenInterest doi = new DceOpenInterest();

		// 郑州路径
		String path = this.jointPath(dcepath, date);// doi.jointPath(dcepath, date);

		// System.out.println(path);

		// 从网上下载数据
		// String webcontent = cdl.ConnectExchange_Get(path);
		String webcontent = this.getRequest(path, "utf-8", date);// Utils.getRequest2(path,
																	// "utf-8",
																	// date);

		// 去掉特殊字符
		webcontent = webcontent.replace("&nbsp;", "");
		// String[] split_webcontent = webcontent.split("_");
		// for (int i = 50; i < 60; i++) {
		//
		// System.out.println(split_webcontent[i]);
		// }

		// 解析数据
		ArrayList<DataInterestBean> dceDataBean = this.handleWebContent(
				webcontent, date); //doi.handleWebContent(webcontent, date);

		this.HandleJdbc(dceDataBean,"dceinterestdate", dceinterestdate);

	}

	/**
	 * GET 请求交易所
	 * 
	 * @param path
	 *            交易所地址
	 * @param date
	 * @return
	 */
	private String getRequest(String path, String encoding, String date) {

		String webContent = "";
		HttpURLConnection conn = null;
		try {
			URL url = new URL(path);
			// 获取连接对象
			 conn = (HttpURLConnection) url.openConnection();
			// 设置连接属性
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(60000);// 5000
			conn.setReadTimeout(60000);
			// 建立连接，获取响应吗
			if (conn.getResponseCode() == 200) {
				// 1.拿到服务器返回的输入流
				InputStream is = conn.getInputStream();
				// 字节流转换为字符流
				// webContent = Utils.getTextFromStream(is, encoding);
				String name = readZipFile(is, date, "gbk");
				webContent = getAllContnt(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return webContent;
		}finally{
			if (conn!=null) {
				conn.disconnect();
				conn = null;
			}
		}
		// System.out.println(webContent);
		return webContent;
	}

	/**
	 * 解压文件 只取文件名
	 * 
	 * @param ins
	 * @param date
	 * @param encoding
	 * @return 合约名
	 */
	private String readZipFile(InputStream ins, String date, String encoding) {

		StringBuffer buffer = new StringBuffer();
		// 生成文件
		File file = new File(date);
		ZipFile zipFile = null;
		try {
			// 往文件写内容
			inputstreamToFile(ins, file);

		    zipFile = new ZipFile(file, "gbk");// , "gbk"

			Enumeration<ZipEntry> entryEnum = zipFile.getEntries();
			ZipEntry entry = null;
			// InputStream is = null;
			while (entryEnum.hasMoreElements()) {
				entry = entryEnum.nextElement();

				String name = entry.getName();
				// System.out.println("neme:"+name);

				// 获取文件流
				// is = zipFile.getInputStream(entry);

				// String text = getTextFromStream(is, encoding);
				String[] split = name.split("_");
				buffer.append(split[1] + "_");
			}
			// if (is != null) {
			// is.close();
			// is = null;
			// }
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (zipFile != null) {
				try {
					zipFile.close();
					zipFile = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 删除文件
		file.delete();

		return buffer.toString();
	}

	/**
	 * // InputStream字节流 --> File文件
	 * 
	 * @param ins
	 * @param file
	 */
	private void inputstreamToFile(InputStream ins, File file) {
		 OutputStream os=null;
		try {
			 os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (os!=null) {
					os.close();
					os=null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据品种每次请求内容
	 * 
	 * @param name
	 * @return 所有合约的排名源代码
	 */
	private String getAllContnt(String name) {
		StringBuffer sb = new StringBuffer();
		String path = Main.properties.getProperty("dceinterestdate2");
		String[] splitPath = path.split(",");
		// 合约名数组
		String[] splitName = name.split("_");

		String urlPath = "";
		for (String p : splitName) {
			urlPath = splitPath[0] + p + splitPath[2];
			sb.append(p + "==" + Utils.getRequest(urlPath, "gbk") + "_");
		}
		return sb.toString();
	}

	/**
	 * 操作数据库
	 * 
	 * @param dceDataBean
	 */
	private void HandleJdbc(ArrayList<DataInterestBean> dceDataBean,String exchange, String date) {
		if (dceDataBean.size() == 0) {
			//System.out.println("DceOpenInterest没数据");
			return;
		}
		System.out.println("DceOpenInterest" + ":"
				+ dceDataBean.get(0).date);
		// for (DataInterestBean dbean : dceDataBean) {
		// System.out.println(dbean.toString());
		// }

		if (!firstFlag) {
			dcejdbc = new JdbcConnect();
			// 连接数据库
			dcejdbc.connetionDB();
			// 先删
			dcejdbc.deleteDB2(dceinterestdate, "DCE");
			// 关闭数据库
			dcejdbc.closeDB();
			firstFlag = true;
		}
		if (dcejdbc == null) {
			dcejdbc = new JdbcConnect();
		}
		// 连接数据库
		dcejdbc.connetionDB();
		// 插入数据库   批量插入
		//dcejdbc.insertDB2(dceDataBean);
		dcejdbc.insertDB_Batch(dceDataBean);
		// 查询数据库
		// jdbc.queryDB();
		// 关闭数据库
		dcejdbc.closeDB();
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
				dceinterestdate = Main.properties
						.getProperty("dceinterestdate");
				// 地址
				dceinterestpath = Main.properties
						.getProperty("dceinterestpath");
				// 是否是交易日
				if (Utils.isTradingDay("dceinterestdate", dceinterestdate)) { // &&"20151111".compareTo(dcedate)>0

					DceExchange(dceinterestpath, dceinterestdate);
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
