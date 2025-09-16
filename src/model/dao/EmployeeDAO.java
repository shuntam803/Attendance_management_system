
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.entity.Employee;
import model.entity.Section;

/**
 * @author Shuncub
 * 従業員データベースを繋ぐDAOクラス。
 */
public class EmployeeDAO {
	/** 唯一のインスタンスを生成する。*/
	private static EmployeeDAO instance = new EmployeeDAO();
	
	/** 特定のデータベースとの接続(セッション)。 */
	private Connection conn;
	
	/** privateのため新規のインスタンスをつくらせない。 */
	private EmployeeDAO() {}
	
	/**
	 * @return EmployeeDAOの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static EmployeeDAO getInstance() {
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
	 * @param lastName 従業員の苗字。
	 * @param firstName 従業員の氏名。
	 * @param lastKanaName 従業員の苗字のふりがな。
	 * @param firstKanaName 従業員の氏名のふりがな。
	 * @param gender 性別（0なら男性、1なら女性）。
	 * @param birthDay 生年月日（"yyyy-MM-dd"の文字列）。
	 * @param sectionCode 部署コード。
	 * @param hireDate 入社日。
	 * @param password パスワード。
	 * @return データベースに従業員情報を挿入出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 従業員情報を新規追加する。
	 */
	public boolean insertEmployee(String lastName, String firstName, String lastKanaName, String firstKanaName,
			int gender, String birthDay, String sectionCode, String hireDate, String password) throws SQLException {

		// オートコミットを無効にする
		conn.setAutoCommit(false);

		String selectMaxSql = "SELECT MAX(employee_code) FROM m_employee";
		String insertSql = "INSERT INTO m_employee VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, null, ?)";
		int code = 0;
		String employeeCode = "E0001";
		boolean flag = false;

                try {
                        try (PreparedStatement maxPs = conn.prepareStatement(selectMaxSql);
                                        ResultSet rs = maxPs.executeQuery()) {
                                if (rs.next() && rs.getString(1) != null) {
                                        code = Integer.parseInt(rs.getString(1).substring(1)) + 1;
                                        employeeCode = "E" + String.format("%04d", code);
                                }
                        }

                        try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                                insertPs.setString(1, employeeCode);
                                insertPs.setString(2, lastName);
                                insertPs.setString(3, firstName);
                                insertPs.setString(4, lastKanaName);
                                insertPs.setString(5, firstKanaName);
                                insertPs.setInt(6, gender);
                                insertPs.setString(7, birthDay);
                                insertPs.setString(8, sectionCode);
                                insertPs.setString(9, hireDate);
                                insertPs.setString(10, password);
                                int result = insertPs.executeUpdate();
                                if (result > 0) {
                                        conn.commit();
                                        flag = true;
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

                return flag;
	}

	/**
	 * @param employee - 従業員モデルクラス。
	 * @return データベースに従業員情報を更新出来たらtrue、出来なかったらfalse。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 従業員の情報をアップデートする。（従業員情報編集機能）
	 */
	public Employee updateEmployee(Employee employee) throws SQLException {
		conn.setAutoCommit(false);
		
		String sql = "UPDATE m_employee SET last_name = ?, first_name = ?, last_kana_name = ?, first_kana_name = ?, "
				+ "gender = ?, birth_day = ?, section_code = ?, hire_date = ? WHERE employee_code = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employee.getLastName());
			ps.setString(2, employee.getFirstName());
			ps.setString(3, employee.getLastKanaName());
			ps.setString(4, employee.getFirstKanaName());
			ps.setInt(5, employee.getGender());
			ps.setString(6, employee.getBirthDay());
			ps.setString(7, employee.getSectionCode());
			ps.setString(8, employee.getHireDate());
			ps.setString(9, employee.getEmployeeCode());
			int count = ps.executeUpdate();
			if (count > 0) {
				conn.commit();
			} else {
				rollbackQuietly();
			}
		} catch (SQLException e) {
			rollbackQuietly();
			throw e;
		} finally {
			resetAutoCommit();
		}

		return employee;
	}

	/**
	 * @param employeeCode - 従業員コード。
	 * @return 対応する従業員、存在しない場合null。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 指定されたemployeeCodeから従業員の情報を取得して、Employee型で返す。
	 */
	public Employee selectEmployee(String employeeCode) throws SQLException {
		String sql = "SELECT * FROM m_employee WHERE employee_code = ?";

		Employee employee = null;

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					employee = new Employee();
					employee.setEmployeeCode(rs.getString(1));
					employee.setLastName(rs.getString(2));
					employee.setFirstName(rs.getString(3));
					employee.setLastKanaName(rs.getString(4));
					employee.setFirstKanaName(rs.getString(5));
					employee.setGender(rs.getInt(6));
					employee.setBirthDay(rs.getString(7));
					employee.setSectionCode(rs.getString(8));
					employee.setHireDate(rs.getString(9));
				}
			}
		}

		return employee;
	}


	/**
	 * @param employeeCode - 従業員コード。
	 * @return データベースに従業員情報を更新出来たら1、出来なかったら0。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 指定されたemployeeCodeの従業員情報を削除する。
	 */
	public int deleteEmployee(String employeeCode) throws SQLException {
		conn.setAutoCommit(false);
		String sql = "DELETE FROM m_employee WHERE employee_code = ?";
		int count = 0;

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			count = ps.executeUpdate();
			if (count > 0) {
				conn.commit();
			} else {
				rollbackQuietly();
			}
		} catch (SQLException e) {
			rollbackQuietly();
			throw e;
		} finally {
			resetAutoCommit();
		}

		return count;

	}

	/**
	 * @return List<Section> - 部署一覧。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 表示のために部署一覧を取得して、List<Section>型で返す。
	 */
        public List<Section> getSection() throws SQLException {
                String sql = "SELECT * FROM m_section ORDER BY section_code";
                List<Section> sections = new LinkedList<Section>();
                try (PreparedStatement ps = conn.prepareStatement(sql);
                                ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                                //レコードの値を取得
                                Section se = new Section();
                                se.setSectionCode(rs.getString(1));
                                se.setSectionName(rs.getString(2));
                                sections.add(se);
                        }
                }
                return sections;
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