package com.tangb.download;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.tangb.utils.Utils;


/**
 * 从网上下载数据
 * @author tangbao
 * 2015-11-13下午4:10:23
 */
public class DownLoadFromWeb {
	
	
	//交易所地址
	public String path;
	//参数
	public ArrayList<String>param;
	
	public DownLoadFromWeb() {
		super();
	}
	public DownLoadFromWeb(String path, ArrayList<String> param) {
		super();
		this.path = path;
		this.param = param;
	}
	/**
	 * 连接数据库
	 * @param path 交易所地址
	 * @param param 参数
	 * @return 网页内容
	 */
	public static String ConnectExchange(String path,ArrayList<String>param){

		String webContent="";

		try {
			URL url = new URL(path);
			//获取连接对象
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//设置连接属性
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			//建立连接，获取响应吗
			if(conn.getResponseCode() == 200){
				//1.拿到服务器返回的输入流
				InputStream is = conn.getInputStream();
				
				//字节流转换为字符流
				webContent = Utils.getTextFromStream(is,"gb2312");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return webContent;
		}
		return webContent;
	}
	/**
	 * 处理网络内容
	 * @param webcontent网络内容
	 * @return javaBean
	 */
	public static ArrayList<String> handleWebContent(String webcontent){
		 ArrayList<String> arrayList = new ArrayList<>();
		 //javabean
		 
		 return arrayList;
	}
	/**
	 * 拼接path
	 * @param path
	 * @param param
	 * @return
	 */
	public static String jointPath(String path,ArrayList<String>param){
		String result="";
		
		
		
		return result;
	}
}
