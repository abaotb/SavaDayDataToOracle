package com.tangb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.tangb.domain.DataInterestBean;
import com.tangb.domain.DataJavaBean;
import com.tangb.main.Main;

/**
 * 数据库操作
 * 
 * @author tangbao 2015-11-16下午2:40:15
 */
public class JdbcConnect {

	// 连接对象
	public Connection conn;
	// 发送命令对象
	public Statement stmt;
	// 拿到查询的结构集对象
	public ResultSet rs;
	public PreparedStatement preStmt;
	
	//驱动，地址，用户，密码,表名
	public String driverClass;
	public String url;
	public String user;
	public String password;
	public String table;   //20160117  加

	public JdbcConnect() {
		super();
		 try {
			driverClass = Main.driverClass;
			url = Main.url;
			user =  Main.user;
			password =  Main.password;
			table = Main.table;
			Class.forName(driverClass) ;      //驱动只加载一次就行，不然会内存泄露
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

	/**
	 * 连接数据库
	 */
	public void connetionDB() {
		try {
			// 1. 注册驱动
			// Class.forName(driverClass) ;
			//Class.forName("com.mysql.jdbc.Driver");
			// 2. 创建一个连接对象
			 conn = DriverManager.getConnection(url,user,password) ; 
			//conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exchangedata", "root", "root");
			// 3. 创建一个sql语句的发送命令对象
		    stmt = conn.createStatement();
			  

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		// 6. 关闭连接,命令对象，结果集
		try {
			if (rs != null) {

				rs.close();
				rs = null;
			}
			if (preStmt != null) {

				preStmt.close();
				preStmt = null;
			}
			if (stmt != null) {

				stmt.close();
				stmt = null;
			}
			if (conn != null) {

				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加日行情数据
	 * 
	 * @param data
	 *            JavaBean
	 * @return 插入条数
	 */
	public   int insertDB(ArrayList<DataJavaBean> dataList) {
		int result = 0;
		
		if (dataList.size()==0) {
			return result;
		}
		 try {
			 for (DataJavaBean data : dataList) {
				
				 String sql = "INSERT INTO "+table+"(" +
						 "jys," +
						 "contract," +
						 "rq," +
						 "prev_Close," +
						 "prev_Settle," +
						 "open_Pri," +
						 "high_Pri," +
						 "low_Pri," +
						 "close_Pri," +
						 "settle_Pri," +
						 "close_Range," +
						 "settle_Range," +
						 "volume," +
						 "turnover," +
						 "oi)" +
						 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				 preStmt = conn.prepareStatement(sql);
//				 System.out.println(preStmt.toString());
				 preStmt.setString(1, data.exchange);
				 preStmt.setString(2, data.contract);
				 preStmt.setString(3, data.date);
				 preStmt.setString(4, data.prev_Close);
				 preStmt.setString(5, data.prev_Settle);
				 preStmt.setString(6,data.open_Pri);
				 preStmt.setString(7, data.high_Pri);
				 preStmt.setString(8, data.low_Pri);
				 preStmt.setString(9, data.close_Pri);
				 preStmt.setString(10, data.settle_Pri);
				 preStmt.setString(11, data.close_Range);
				 preStmt.setString(12, data.settle_Range);
				 preStmt.setString(13, data.volume);
				 preStmt.setString(14,data.turnover);
				 preStmt.setString(15, data.oI);
				 int  i = preStmt.executeUpdate(); 
				 result++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 if (dataList!=null) {
			dataList =null;
		}
		return result;
	}
	/**
	 * 增加日持仓数据
	 * 
	 * @param data
	 *            JavaBean
	 * @return 插入条数
	 */
	public   int insertDB2(ArrayList<DataInterestBean> dataList) {
		int result = 0;
		
		if (dataList.size()==0) {
			return result;
		}
		 try {
			 for (DataInterestBean data : dataList) {
				
				 String sql = "INSERT INTO openinterest_rank(" +
						 "exchange," +
						 "date," +
						 "rank," +
						 "contract," +
						 "namevolume," +
						 "volume," +
						 "rangevolume," +
						 
						 "nameholdingbuy," +
						 "holdingbuy," +
						 "rangeholdingbuy," +
						 
						 "nameholdingsell," +
						 "holdingsell," +
						 "rangeholdingsell)" +
						 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				 preStmt = conn.prepareStatement(sql);
				 preStmt.setString(1, data.exchange);
				 preStmt.setString(2, data.date);
				 preStmt.setString(3, data.rank);
				 preStmt.setString(4, data.contract);
				 
				 preStmt.setString(5, data.namevolume);
				 preStmt.setString(6, data.volume);
				 preStmt.setString(7, data.rangevolume);
				 
				 preStmt.setString(8, data.nameholdingbuy);
				 preStmt.setString(9, data.holdingbuy);
				 preStmt.setString(10, data.rangeholdingbuy);
				 
				 preStmt.setString(11, data.nameholdingsell);
				 preStmt.setString(12, data.holdingsell);
				 preStmt.setString(13, data.rangeholdingsell);
				 int  i = preStmt.executeUpdate(); 
				 result++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 批量插入
	 * @param dataList
	 * @return
	 */
	public  int insertDB_Batch(ArrayList<DataInterestBean> dataList) {
		int result = 0;
		
		if (dataList.size()==0) {
			return result;
		}
		try {
			//不自动 Commit (瓜子不是一个一个吃,全部剥开放桌子上,然后一口舔了)
			conn.setAutoCommit(false);
			String sql = "INSERT INTO openinterest_rank(" +
			"exchange," +
			"date," +
			"rank," +
			"contract," +
			"namevolume," +
			"volume," +
			"rangevolume," +
			
			"nameholdingbuy," +
			"holdingbuy," +
			"rangeholdingbuy," +
			 
			"nameholdingsell," +
			"holdingsell," +
			"rangeholdingsell)" +
			"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			preStmt = conn.prepareStatement(sql);
			for (DataInterestBean data : dataList) {
				//来一个剥一个,然后放桌子上
				preStmt.setString(1, data.exchange);
				preStmt.setString(2, data.date);
				preStmt.setString(3, data.rank);
				preStmt.setString(4, data.contract);
				
				preStmt.setString(5, data.namevolume);
				preStmt.setString(6, data.volume);
				preStmt.setString(7, data.rangevolume);
				
				preStmt.setString(8, data.nameholdingbuy);
				preStmt.setString(9, data.holdingbuy);
				preStmt.setString(10, data.rangeholdingbuy);
				
				preStmt.setString(11, data.nameholdingsell);
				preStmt.setString(12, data.holdingsell);
				preStmt.setString(13, data.rangeholdingsell);
//				int  i = preStmt.executeUpdate(); 
				//来一个剥一个,然后放桌子上
				preStmt.addBatch();
				result++;
			}
			//批量执行上面3条语句
			preStmt.executeBatch();
			//Commit it 
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 查询数据
	 * 
	 * @return
	 */
	public boolean queryDB() {
		boolean result = false;
		try {
			// 4. 执行SQL,拿到查询的结构集对象
			String sqlString = "select * from "+table+""; //openinterest_rank

			rs = stmt.executeQuery(sqlString);
			// 5. 输出结果集的数据
			while (rs.next()) {
				System.out.println(rs.getString("jys") + ":"
						+ rs.getString("contract") + ":"
						+ rs.getString("rq")+":"
						+ rs.getString("open_Pri"));
				// System.out.println(rs.getInt("id") + ":" +
				// rs.getString("name") +
				// ":" + rs.getString("address"));
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	/**
	 * 查询数据
	 * 
	 * @return
	 */
	public boolean queryDB2() {
		boolean result = false;
		try {
			// 4. 执行SQL,拿到查询的结构集对象
			String sqlString = "select * from openinterest_rank"; //openinterest_rank
			
			rs = stmt.executeQuery(sqlString);
			// 5. 输出结果集的数据
			while (rs.next()) {
				System.out.println(rs.getString("exchange") + ":"
						+ rs.getString("contract") + ":"
						+ rs.getString("data.rank") + ":"
						+ rs.getString("date")+":"
						+ rs.getString("rankstyle"));
				// System.out.println(rs.getInt("id") + ":" +
				// rs.getString("name") +
				// ":" + rs.getString("address"));
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 删除日行情数据
	 * @param date
	 * @return
	 */
	public int deleteDB(String date,String exchange){
		int  result = 0;
		String sql = "delete from "+table+" where rq>='"+date +"' and jys='"+exchange+"'";  
	    try  
	    {  
	    	result = stmt.executeUpdate(sql);  
	    }  
	    catch (SQLException e)  
	    {  
	        e.printStackTrace();  
	    }  
		return result;
	}
	/**
	 * 删除日持仓数据
	 * @param date
	 * @return
	 */
	public int deleteDB2(String date,String exchange){
		int  result = 0;
		String sql = "delete from openinterest_rank where date>='"+date +"' and exchange='"+exchange+"'";  
		try  
		{  
			result = stmt.executeUpdate(sql);  
		}  
		catch (SQLException e)  
		{  
			e.printStackTrace();  
		}  
		return result;
	}
}
