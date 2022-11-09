package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Shuncub
 * データベースに接続するためのクラス。
 */
public class ConnectionManager {
	/** Javaとsuedbデータベースの接続のためのAPIのURL。 */
	private static final String URL = "jdbc:mysql://127.0.0.1/test_schema?useSSL=false";
	
	/** suedbデータベースを使うユーザー。*/
	private static final String USER = "root";
	
	/** suedbデータベースを使うパスワード。 */
	private static final String PASS = "4gp351dG!";
	
	/** 唯一のインスタンスを生成する。 */
	private static ConnectionManager instance = new ConnectionManager();

	/** privateのため新規のインスタンスをつくらせない。 */
	private ConnectionManager() {}

	/**
	 * @return ConnectionManagerの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static ConnectionManager getInstance(){
		return instance;
	}

	/**
	 * @return 対応するConnection。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * データベースに接続する。
	 */
	public Connection connect() throws SQLException{
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return DriverManager.getConnection(URL, USER, PASS);
	}
}