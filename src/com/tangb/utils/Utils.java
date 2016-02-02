package com.tangb.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import com.tangb.main.Main;

public class Utils {

	/**
	 * 字节流转换为字符流
	 * 
	 * @param is
	 *            字节流
	 * @return 字符串
	 */
	public static String getTextFromStream(InputStream is, String encoding) {

		byte[] b = new byte[1024];
		int len = 0;
		// 创建字节数组输出流，读取输入流的文本数据时，同步把数据写入数组输出流
		ByteArrayOutputStream bos = null;
		try {
			// String downloadURL = "page.zip";
			// FileOutputStream fs = new FileOutputStream(downloadURL);
			bos = new ByteArrayOutputStream();
			while ((len = is.read(b)) != -1) {
				bos.write(b, 0, len);
				// fs.write(b, 0, len);
			}
			// 把字节数组输出流里的数据转换成字节数组
			// String text = new String(bos.toByteArray());
			// 手动指定码表 乱码的出现是因为服务器和客户端码表不一致导致
			String text = new String(bos.toByteArray(), encoding); // "gb2312"
			return text;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
					bos = null;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 字节流转换为字符流
	 * 
	 * @param is
	 *            字节流
	 * @return 字符串
	 */
	public static String getTextFromStreamReader(InputStream is, String encoding) {
		String result = "";
		InputStreamReader inputStreamReader = null;
		try {
			// 定义BufferedReader输入流来读取URL的响应
			inputStreamReader = new InputStreamReader(is, encoding);// "GBK"
			BufferedReader in = null;
			in = new BufferedReader(inputStreamReader);
			String line = "";
			while ((line = in.readLine()) != null) {
				result += line;
				result += "\r\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
					inputStreamReader = null;
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * POST请求交易所
	 * 
	 * @param path
	 *            交易所地址
	 * @param param
	 *            参数
	 * @return 网页内容
	 */
	public static String postRequest(String url, String param, String encoding) {
		URLConnection conn = null;
		PrintWriter out = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("connection", "Keep-Alive");// 维持长连接
			// conn.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			String requestString = "客服端要以以流方式发送到服务端的数据...";

			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数 post的关键所在！
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			InputStream is = conn.getInputStream();
			// 字节流转换为字符流
			result = Utils.getTextFromStream(is, encoding);
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {

			try {
				if (out != null) {
					out.close();
				}
				if (conn != null) {
					conn = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;

	}

	/**
	 * GET 请求交易所
	 * 
	 * @param path
	 *            交易所地址
	 * @return
	 */
	public static String getRequest(String path, String encoding) {

		String webContent = "";
		HttpURLConnection conn = null;
		try {
			URL url = new URL(path);
			// 获取连接对象
			conn = (HttpURLConnection) url.openConnection();
			// 设置连接属性
			conn.setRequestMethod("GET");
			conn.setRequestProperty("connection", "Keep-Alive");// 维持长连接
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			// 建立连接，获取响应吗
			if (conn.getResponseCode() == 200) {
				// 1.拿到服务器返回的输入流
				InputStream is = conn.getInputStream();
				// 字节流转换为字符流
				webContent = Utils.getTextFromStream(is, encoding);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return webContent;
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return webContent;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");// ("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		format = null;
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");// ("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		format = null;
		return date;
	}

	/**
	 * 日期加一
	 * 
	 * @param date
	 * @return
	 */
	public static String addDate(String dateStr) {

		Date date = Utils.StrToDate(dateStr);

		Calendar calendar = new GregorianCalendar();
		// Calendar c = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		calendar = null;
		return Utils.DateToStr(date);
	}

	/**
	 * 写入Properties文件
	 */
	public static void writePropertiesFile(String key, String value) {

		Properties properties = Main.properties;
		InputStream fis = null;
		OutputStream fos = null;
		// URL url =
		// Utils.class.getClassLoader().getResource("dbcfg.properties");
		try {
			String url = System.getProperty("user.dir")
					+ "/src/dbcfg.properties";
			fis = new BufferedInputStream(new FileInputStream(url));
			properties.load(fis);
			File file = new File(url); // url.toURI()
			if (!file.exists()) {
				file.createNewFile();
			}
			fis = new FileInputStream(file);
			properties.load(fis);
			fis.close(); // 一定要在修改值之前关闭fis
			fos = new FileOutputStream(file);
			properties.setProperty(key, value);
			properties.store(fos, "");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				fis.close();
				fis = null;
				fos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 是否是交易日
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isTradingDay(String exchange, String date) {
		boolean result = false;

		// 比今天日期大返回false
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String today = format.format(new Date());
		// if (today.compareTo(date) <= 0) { // 等于，过了今天晚上才下载
		//
		// return result;
		// } else {
		// // 日期加一后修改配置的日期
		// String dateString = Utils.addDate(date);
		// Utils.writePropertiesFile(exchange, dateString);
		// }
		//20151208   //5点后才执行
		if (today.compareTo(date) < 0) { // 等于，过了今天晚上才下载
			format = null;
			return result;
		}else if (today.compareTo(date) == 0) {
		    int now = Integer.parseInt(new SimpleDateFormat("HHmmss").format(new Date()));
			if (now < 200000) {         //8点后才执行
				format = null;
				return result;
			}
		}
		// 周末.
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(format.parse(date));
			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}

			if (dayForWeek == 6 || dayForWeek == 7) {
				// 日期加一后修改配置的日期
				String dateString = Utils.addDate(date);
				Utils.writePropertiesFile(exchange, dateString);
				format = null;
				return result;
			}
			// System.out.println("dayForWeek:"+dayForWeek);
			// System.out.println("today："+Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
		} catch (Exception e) {

			e.printStackTrace();
		}

		// 节假日
		if (Main.properties.containsKey(date)) {
			// System.out.println("节假日");
			// 日期加一后修改配置的日期
			String dateString = Utils.addDate(date);
			Utils.writePropertiesFile(exchange, dateString);
			format = null;
			return result;
		}

		result = true;
		return result;
	}

	/**
	 * 是否比今天日期大
	 * 
	 * @return
	 */
	public static boolean afterToday(String date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String today = format.format(new Date());
		if (date.compareTo(today) >= 0) {
			format = null;
			return true;
		}
		format = null;
		return false;
	}
}
