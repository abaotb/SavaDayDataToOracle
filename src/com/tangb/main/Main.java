package com.tangb.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import com.tangb.download.CffexDownLoad;
import com.tangb.download.CzceDownLoad;
import com.tangb.download.DceDownLoad;
import com.tangb.download.ShfeDownLoad;
import com.tangb.openinterest.CffexOpenInterest;
import com.tangb.openinterest.CzceOpenInterest;
import com.tangb.openinterest.DceOpenInterest;
import com.tangb.openinterest.ShfeOpenInterest;
public class Main {

	// 配置文件
	public static Properties properties;
	// 数据库信息:驱动，地址，用户，密码，表名
	public static String driverClass;
	public static String url;
	public static String user;
	public static String password;
	public static String table;   //20160117  加
	public static int interval;   //20160120加,定时器间隔 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (properties == null) {
			System.out.println("启动程序");
			try {
				properties = new Properties();
				// InputStream in = Main.class.getClassLoader()
				// .getResourceAsStream("dbcfg.properties");
				String filePath = System.getProperty("user.dir")
						+ "/src/dbcfg.properties";
				InputStream in = new BufferedInputStream(new FileInputStream(
						filePath));
				properties.load(in);
				driverClass = properties.getProperty("driverClass");
				url = properties.getProperty("url");
				user = properties.getProperty("user");
				password = properties.getProperty("password");
				table = properties.getProperty("table");
				interval = Integer.parseInt(properties.getProperty("interval"));
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		CzceDownLoad cdl = new CzceDownLoad();
		DceDownLoad ddl = new DceDownLoad();
		CffexDownLoad cfdl = new CffexDownLoad();
		ShfeDownLoad shfl=new ShfeDownLoad();
		// 启动行情线程
		cdl.run();
		ddl.run();
		cfdl.run();
		shfl.run();
		
//		CzceOpenInterest coi = new CzceOpenInterest();
//		DceOpenInterest doi = new DceOpenInterest();
//		ShfeOpenInterest soi = new ShfeOpenInterest();
//		CffexOpenInterest cfoi = new CffexOpenInterest();
//		// 启日持仓线程
//		coi.run();
//		doi.run();
//		soi.run();
//		cfoi.run();

	}

}
