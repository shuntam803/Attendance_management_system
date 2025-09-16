
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Shuncub
 * 出退勤時刻管理データベースを繋ぐDAOクラス。
 */
public class AttendanceEmployeeDAO {
	/** 唯一のインスタンスを生成する。*/
	private static AttendanceEmployeeDAO instance = new AttendanceEmployeeDAO();
	
	/** 特定のデータベースとの接続(セッション)。 */
	private Connection conn;
	
	/**
	 * 日付/時間オブジェクトの出力および解析のためのフォーマッタ。
	 * "HH:mm:ss"のフォーマットで表記。
	 */
	DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	/**
	 * 年/月/日オブジェクトの出力および解析のためのフォーマッタ。
	 * "yyyy-MM-dd"のフォーマットで表記。
	 */
	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/** privateのため新規のインスタンスをつくらせない */
	private AttendanceEmployeeDAO() {}
	/**
	 * @return AttendanceEmployeeDAOの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static AttendanceEmployeeDAO getInstance() {
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

	/** 特定のデータベースとの接続(セッション)を切断する。*/
	public void dbDiscon() {
		try {
                        if (conn != null)
                                conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @param password 対応するパスワード。
	 * @return データベースと一致していたら従業員コード、一致していなかったらnull。
	 * @throws SQLException データベース処理に問題があった場合。
	 * 指定されたemployeeCodeとpasswordから従業員がログインできるかどうかチェックする。
	 */
	public String loginEmployee(String employeeCode, String password) throws SQLException {
                String sql = "SELECT employee_code FROM m_employee WHERE employee_code = ? AND password = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, employeeCode);
                        ps.setString(2, password);
                        try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                        return employeeCode;
                                }
                        }
                }
                return null;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return データベースに出勤情報を挿入出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * タイムカード出勤時間をテーブルに記録する。
	 */
	public boolean setStartTime(String employeeCode) throws SQLException {
		conn.setAutoCommit(false);
		LocalDateTime now = LocalDateTime.now();
		//既にその日のデータが追加されていたらfalseを返す
                String checkSql = "SELECT 1 FROM t_work_time WHERE employee_code = ? AND work_date = ?";
                String insertSql = "INSERT INTO t_work_time (employee_code, work_date, start_time) VALUES (?, ?, ?)";
                boolean result = false;
                try {
                        boolean exists;
                        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                                checkPs.setString(1, employeeCode);
                                checkPs.setString(2, now.format(dateFormat));
                                try (ResultSet rs = checkPs.executeQuery()) {
                                        exists = rs.next();
                                }
                        }

                        if (!exists) {
                                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                                        insertPs.setString(1, employeeCode);
                                        insertPs.setString(2, now.format(dateFormat));
                                        insertPs.setString(3, now.format(timeFormat));
                                        if (insertPs.executeUpdate() > 0) {
                                                conn.commit();
                                                result = true;
                                        }
                                }
                        }

                        if (!result) {
                                rollbackQuietly();
                        }
                } catch (SQLException e) {
                        rollbackQuietly();
                        throw e;
                } finally {
                        resetAutoCommit();
                }
                return result;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return データベースに退勤情報を更新出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * タイムカード退勤時間をテーブルに記録する。
	 */
	public boolean setFinishTime(String employeeCode) throws SQLException {
		conn.setAutoCommit(false);
		LocalDateTime now = LocalDateTime.now();
		//出勤が押されていなかったらfalseを返す
                String selectSql = "SELECT 1 FROM t_work_time WHERE employee_code = ? AND work_date = ?";
                String updateSql = "UPDATE t_work_time SET finish_time = ? WHERE employee_code = ? AND work_date = ?";
                boolean updated = false;
                try {
                        boolean exists;
                        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                                selectPs.setString(1, employeeCode);
                                selectPs.setString(2, now.format(dateFormat));
                                try (ResultSet rs = selectPs.executeQuery()) {
                                        exists = rs.next();
                                }
                        }

                        if (exists) {
                                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                                        updatePs.setString(1, now.format(timeFormat));
                                        updatePs.setString(2, employeeCode);
                                        updatePs.setString(3, now.format(dateFormat));
                                        if (updatePs.executeUpdate() > 0) {
                                                conn.commit();
                                                updated = true;
                                        }
                                }
                        }

                        if (!updated) {
                                rollbackQuietly();
                        }
                } catch (SQLException e) {
                        rollbackQuietly();
                        throw e;
                } finally {
                        resetAutoCommit();
                }
                return updated;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return データベースに休憩開始情報を更新出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * タイムカード休憩開始時間をテーブルに記録する。
	 */
	public boolean setStartBreakTime(String employeeCode) throws SQLException {
		conn.setAutoCommit(false);
		LocalDateTime now = LocalDateTime.now();
		//出勤が押されていなかったらfalseを返す
                String selectSql = "SELECT 1 FROM t_work_time WHERE employee_code = ? AND work_date = ?";
                String updateSql = "UPDATE t_work_time SET break_start_time = ? WHERE employee_code = ? AND work_date = ?";
                boolean updated = false;
                try {
                        boolean exists;
                        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                                selectPs.setString(1, employeeCode);
                                selectPs.setString(2, now.format(dateFormat));
                                try (ResultSet rs = selectPs.executeQuery()) {
                                        exists = rs.next();
                                }
                        }

                        if (exists) {
                                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                                        updatePs.setString(1, now.format(timeFormat));
                                        updatePs.setString(2, employeeCode);
                                        updatePs.setString(3, now.format(dateFormat));
                                        if (updatePs.executeUpdate() > 0) {
                                                conn.commit();
                                                updated = true;
                                        }
                                }
                        }

                        if (!updated) {
                                rollbackQuietly();
                        }
                } catch (SQLException e) {
                        rollbackQuietly();
                        throw e;
                } finally {
                        resetAutoCommit();
                }
                return updated;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return データベースに休憩終了情報を更新出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * タイムカード休憩終了時間をテーブルに記録する。
	 */
	public boolean setFinishBreakTime(String employeeCode) throws SQLException {
		conn.setAutoCommit(false);
		LocalDateTime now = LocalDateTime.now();
		//出勤または休憩開始が押されていなかったらfalseを返す
                String selectSql = "SELECT break_start_time FROM t_work_time WHERE employee_code = ? AND work_date = ?";
                String updateSql = "UPDATE t_work_time SET break_finish_time = ? WHERE employee_code = ? AND work_date = ?";
                boolean updated = false;
                try {
                        boolean canUpdate = false;
                        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                                selectPs.setString(1, employeeCode);
                                selectPs.setString(2, now.format(dateFormat));
                                try (ResultSet rs = selectPs.executeQuery()) {
                                        if (rs.next() && rs.getString(1) != null) {
                                                canUpdate = true;
                                        }
                                }
                        }

                        if (canUpdate) {
                                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                                        updatePs.setString(1, now.format(timeFormat));
                                        updatePs.setString(2, employeeCode);
                                        updatePs.setString(3, now.format(dateFormat));
                                        if (updatePs.executeUpdate() > 0) {
                                                conn.commit();
                                                updated = true;
                                        }
                                }
                        }

                        if (!updated) {
                                rollbackQuietly();
                        }
                } catch (SQLException e) {
                        rollbackQuietly();
                        throw e;
                } finally {
                        resetAutoCommit();
                }
                return updated;
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