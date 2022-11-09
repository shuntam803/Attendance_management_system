
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- データベース: `test_schema`
--

-- --------------------------------------------------------

--
-- テーブルの構造 `m_section`
--

CREATE TABLE `m_section` (
  `section_code` int NOT NULL,
  `section_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- テーブルのデータのダンプ `m_section`
--

INSERT INTO `m_section` (`section_code`, `section_name`) VALUES
(1001, '総務部'),
(1002, '人事部'),
(1003, '経理部');

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `m_section`
--
ALTER TABLE `m_section`
  ADD PRIMARY KEY (`section_code`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
