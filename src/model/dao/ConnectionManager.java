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
	private static final String URL = getRequiredConfig("DB_URL");

	/** suedbデータベースを使うユーザー。*/
	private static final String USER = getRequiredConfig("DB_USER");

	/** suedbデータベースを使うパスワード。 */
	private static final String PASS = getRequiredConfig("DB_PASSWORD");
	
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

	/**
	 * 指定されたキーの環境変数／システムプロパティを取得する。
	 * @param key 取得したい設定値のキー
	 * @return 環境変数またはシステムプロパティに設定された値
	 * @throws IllegalStateException 必要な設定が見つからなかった場合
	 */
	private static String getRequiredConfig(String key) {
		String value = System.getenv(key);
		if(value == null || value.isEmpty()){
			value = System.getProperty(key);
		}
		if(value == null || value.isEmpty()){
			throw new IllegalStateException("Required configuration '" + key + "' is not set.");
		}
		return value;
	}
}
