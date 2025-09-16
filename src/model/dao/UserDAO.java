package model.dao;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Shuncub
 * 管理者データベースを繋ぐDAOクラス。
 */
public class UserDAO {
	/** 唯一のインスタンスを生成する。*/
	private static UserDAO instance = new UserDAO();

	/** 特定のデータベースとの接続(セッション)。 */
	private Connection conn;
	
	/** privateのため新規のインスタンスをつくらせない。*/
	private UserDAO() {}

	/**
	 * @return UserDAOの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static UserDAO getInstance() {
		return instance;
	}

	/**
	 * @throws SQLException データベース処理に問題があった場合。
	 * 特定のデータベースとの接続(セッション)を生成する。
	 */
	public void dbConnect() throws SQLException {
		ConnectionManager cm = ConnectionManager.getInstance();
		conn = cm.connect();
	}

	/**
	 * @throws SQLException データベース処理に問題があった場合。
	 * 静的SQL文を実行し、作成された結果を返すために使用されるオブジェクトを生成する。
	 */
	public void createSt() throws SQLException {
		// PreparedStatement を各メソッド内で生成するため、ここで行う処理はありません。
	}

	/** 特定のデータベースとの接続(セッション)を切断する。 */
	public void dbDiscon() {
		try {
            if (conn != null)
                    conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param userId - ユーザーID。
	 * @param password - パスワード。
	 * @return データベースと一致していたらtrue、一致していなかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * @throws NoSuchAlgorithmException。ある暗号アルゴリズムが要求されたにもかかわらず、現在の環境では使用可能でない場合。
	 * 指定されたemployeeCodeとpasswordから管理者ユーザーがログインできるかどうかチェックする。
	 */
	public boolean loginUser(String userId, String password) throws SQLException, NoSuchAlgorithmException {

		boolean loginUserChkFlag = false;

		//パスワードをハッシュ化
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] passwordDigest = digest.digest(password.getBytes());
		String sha1 = String.format("%040x", new BigInteger(1, passwordDigest));

		// user_idとpasswordがマッチしたユーザレコードを取得する
        String sql = "SELECT user_id FROM m_user WHERE user_id = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId);
                ps.setString(2, sha1.substring(8));
                try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                                loginUserChkFlag = true;
                        }
                }
        }
        return loginUserChkFlag;
	}

	/**
	 * @param userId - ユーザーID。
	 * @param password - パスワード。
	 * @return データベースに管理者を挿入出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * @throws NoSuchAlgorithmException。ある暗号アルゴリズムが要求されたにもかかわらず、現在の環境では使用可能でない場合。
	 * 管理者ユーザーの情報を新規追加する。
	 */
	public boolean insertUser(String userId, String password) throws SQLException, NoSuchAlgorithmException {

		// オートコミットを無効にする
		conn.setAutoCommit(false);

		boolean insertUserChkFlag = false;

		// user_idがマッチしたユーザレコードを取得する
        String selectSql = "SELECT 1 FROM m_user WHERE user_id = ?";
        String insertSql = "INSERT INTO m_user VALUES(?, ?, null)";

        //パスワードをハッシュ化
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] passwordDigest = digest.digest(password.getBytes());
        String sha1 = String.format("%040x", new BigInteger(1, passwordDigest));

        try {
                try (PreparedStatement checkPs = conn.prepareStatement(selectSql)) {
                        checkPs.setString(1, userId);
                        try (ResultSet rs = checkPs.executeQuery()) {
                                if (rs.next()) {
                                        rollbackQuietly();
                                        return false;
                                }
                        }
                }

                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setString(1, userId);
                        insertPs.setString(2, sha1.substring(8));
                        int result = insertPs.executeUpdate();
                        if (result > 0) {
                                insertUserChkFlag = true;
                                conn.commit();
                        } else {
                                rollbackQuietly();
                        }
                }
        } catch (SQLException e) {
                rollbackQuietly();
                throw e;
        } finally {
                resetAutoCommit();
        }

        return insertUserChkFlag;
        }

        private void rollbackQuietly() {
                if (conn != null) {
                        try {
                                conn.rollback();
                        } catch (SQLException e) {
                                e.printStackTrace();
                        }
                }
        }

        private void resetAutoCommit() {
                if (conn != null) {
                        try {
                                conn.setAutoCommit(true);
                        } catch (SQLException e) {
                                e.printStackTrace();
                        }
                }
        }
}