
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- データベース: `test_schema`
--

-- --------------------------------------------------------

--
-- テーブルの構造 `m_employee`
--

CREATE TABLE `m_employee` (
  `employee_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `last_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `first_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `last_kana_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `first_kana_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` int NOT NULL,
  `birth_day` date NOT NULL,
  `section_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `hire_date` date NOT NULL,
  `confirmation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- テーブルのデータのダンプ `m_employee`
--

INSERT INTO `m_employee` (`employee_code`, `last_name`, `first_name`, `last_kana_name`, `first_kana_name`, `gender`, `birth_day`, `section_code`, `hire_date`, `confirmation`, `password`) VALUES
('E0236', '山田', '太郎', 'やまだ', 'たろう', 0, '2022-02-09', '1003', '2022-02-16', 'E0236', 'aaaa1111'),
('E0237', '福岡', '太朗', 'ふくおか', 'たろう', 1, '2020-02-02', '1002', '2022-02-02', NULL, 'aaaa1111'),
('E0240', '安倍', '晋三', 'あべ', 'しんぞう', 0, '2022-02-09', '1001', '2022-02-16', NULL, 'aaaaaaaa'),
('E0241', '織田', '信長', 'おだ', 'のぶなが', 0, '2022-02-07', '1001', '2022-02-18', 'aaaaaaaa', 'aaaaaaaa');

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `m_employee`
--
ALTER TABLE `m_employee`
  ADD UNIQUE KEY `employee_code` (`employee_code`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
