package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.entity.ViewListDisplay;

/**
 * @author Shuncub
 * 従業員一覧画面表示のためにデータベースを接続するDAOクラス。
 */
public class ViewListDAO {
	/** 唯一のインスタンスを生成する */
	private static ViewListDAO instance = new ViewListDAO(); //唯一のインスタンスとする

	/** 特定のデータベースとの接続(セッション)。 */
	private Connection conn;
	
	/** 従業員一覧表示用モデルクラスのリストを生成する。 */
	private List<ViewListDisplay> list = new LinkedList<ViewListDisplay>();

	/** privateのため新規のインスタンスをつくらせない。 */
	private ViewListDAO() {}

	/**
	 * @return ViewListDAOの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static ViewListDAO getInstance() {
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
	 * @return List<ViewListDisplay>  従業員モデルクラスのリスト。
	 * @throws SQLException データベース処理に問題があった場合。
	 * 従業員情報一覧全件を取得して、List<ViewListDisplay>型で返します。
	 */
	public List<ViewListDisplay> showAllList() throws SQLException {
		list.clear();
		String sql = "SELECT e.employee_code,"
				+ "concat(e.last_name, e.first_name),"
				+ "concat(e.last_kana_name, e.first_kana_name),"
				+ "e.gender, e.birth_day,"
				+ "s.section_name, hire_date "
				+ "FROM m_employee e LEFT OUTER JOIN m_section s "
				+ "ON e.section_code = s.section_code";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                                ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                                //レコードの値を取得
                                ViewListDisplay vld = new ViewListDisplay();

                                vld.setEmployeeCode(rs.getString(1));
                                vld.setEmployeeName(rs.getString(2));
                                vld.setEmployeeKanaName(rs.getString(3));
                                vld.setGender(rs.getInt(4));
                                vld.setBirthDay(rs.getDate(5));
                                vld.setSectionName(rs.getString(6));
                                vld.setHireDate(rs.getDate(7));

                                list.add(vld);
                        }
                }
                return list;
        }
}