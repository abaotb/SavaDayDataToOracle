package com.tangb.test;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import javax.management.remote.TargetedNotification;


import org.jsoup.nodes.Document;

import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tangb.db.JdbcConnect;
import com.tangb.domain.DataJavaBean;
import com.tangb.download.DownLoadFromWeb;
import com.tangb.openinterest.CzceOpenInterest;
import com.tangb.utils.Utils;



public class test {

	public static StringBuffer sb = new StringBuffer();

	/**
	 * @param args
	 * @throws IOException
	 * @throws Exception
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("hello world");
//		unZip("E:/MyEclipseWorkSpace/DownLoadDayData/page.zip",
//				"E:/MyEclipseWorkSpace/DownLoadDayData/123/");
		// unZip("D:/123/123.zip", "D:/123/");

		System.out.println(sb.toString());
	}

	public void tewst(){
		//ZipInputStream is = zipFile.getInputStream(fileHeader);
       // OutputStream os = new FileOutputStream(outFile);
	}
//	public static ArrayList<InputStream> readZipFile(String srcZipFile){
//		ArrayList<InputStream> inputStreams = new ArrayList<InputStream>();
//		try {
//			ZipFile zipFile = new ZipFile(srcZipFile, "gbk");
//			Enumeration<ZipEntry> entryEnum = zipFile.getEntries();
//			ZipEntry entry = null;
//			InputStream eis = null;
//			while (entryEnum.hasMoreElements()) {
//				eis = zipFile.getInputStream(entry);
//				inputStreams.add(eis);
//			}
//			if (eis !=null) {
//				eis.close();
//			}
//		} catch  (Exception e) {
//			e.printStackTrace();
//		}
//		return inputStreams;
//	}
//
//	public static final int DEFAULT_BUFSIZE = 1024 * 16;
//
//	/**
//	 * 解压Zip文件
//	 * 
//	 * @param srcZipFile
//	 * @param destDir
//	 * @throws IOException
//	 */
//	public static void unZip(File srcZipFile, String destDir)
//			throws IOException {
//		ZipFile zipFile = new ZipFile(srcZipFile);
//		Enumeration<ZipEntry> entryEnum = zipFile.getEntries();
//		ZipEntry entry = null;
//		while (entryEnum.hasMoreElements()) {
//
//			entry = entryEnum.nextElement();
//			File destFile = new File(destDir + entry.getName());
//			if (entry.isDirectory()) {
//				destFile.mkdirs();
//			} else {
//				destFile.getParentFile().mkdirs();
//				InputStream eis = zipFile.getInputStream(entry);
//
//				BufferedInputStream bufIs = null;
//				BufferedOutputStream bufOs = null;
//
//				try {
//					bufIs = new BufferedInputStream(eis);
//					bufOs = new BufferedOutputStream(new FileOutputStream(
//							destFile));
//					byte[] buf = new byte[DEFAULT_BUFSIZE];
//					int len = 0;
//					while ((len = bufIs.read(buf, 0, buf.length)) > 0) {
//						bufOs.write(buf, 0, len);
//
//						String text = new String(buf, "gbk"); // "gb2312"
//						System.out.println(text);
//					}
//				} catch (IOException ex) {
//					throw ex;
//				} finally {
//					close(bufOs, bufIs);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 解压Zip文件
//	 * 
//	 * @param srcZipFile
//	 * @param destDir
//	 * @throws IOException
//	 */
//	public static void unZip(String srcZipFile, String destDir)
//			throws IOException {
//		ZipFile zipFile = new ZipFile(srcZipFile, "gbk");
//		unZip(zipFile, destDir);
//	}
//
//	/**
//	 * 解压Zip文件
//	 * 
//	 * @param zipFile
//	 * @param destDir
//	 * @throws IOException
//	 */
//	public static void unZip(ZipFile zipFile, String destDir)
//			throws IOException {
//		Enumeration<ZipEntry> entryEnum = zipFile.getEntries();
//		// Enumeration<? extends ZipEntry> entryEnum = zipFile.getEntries();
//		ZipEntry entry = null;
//		while (entryEnum.hasMoreElements()) {
//
//			entry = entryEnum.nextElement();
//			File destFile = new File(destDir + entry.getName());
//			if (entry.isDirectory()) {
//				destFile.mkdirs();
//			} else {
//				destFile.getParentFile().mkdirs();
//				InputStream eis = zipFile.getInputStream(entry);
//				// System.out.println(eis.read());
//				write(eis, destFile);
//			}
//		}
//	}
//
//	/**
//	 * 将输入流中的数据写到指定文件
//	 * 
//	 * @param inputStream
//	 * @param destFile
//	 */
//	public static void write(InputStream inputStream, File destFile)
//			throws IOException {
//		BufferedInputStream bufIs = null;
//		BufferedOutputStream bufOs = null;
//
//		try {
//			bufIs = new BufferedInputStream(inputStream);
//			bufOs = new BufferedOutputStream(new FileOutputStream(destFile));
//			byte[] buf = new byte[DEFAULT_BUFSIZE];
//			int len = 0;
//			while ((len = bufIs.read(buf, 0, buf.length)) > 0) {
//				bufOs.write(buf, 0, len);
//				bufOs.flush();
//				String text = new String(buf, "gbk"); // "gb2312"
//				System.out.println(text);
//				System.out
//						.println("-------------------------------------------");
//				sb.append(text);
//			}
//		} catch (IOException ex) {
//			throw ex;
//		} finally {
//			close(bufOs, bufIs);
//		}
//	}
//
//	/**
//	 * 安全关闭多个流
//	 * 
//	 * @param streams
//	 */
//	public static void close(Closeable... streams) {
//		try {
//			for (Closeable s : streams) {
//				if (s != null)
//					s.close();
//			}
//		} catch (IOException ioe) {
//			ioe.printStackTrace(System.err);
//		}
//	}

}
