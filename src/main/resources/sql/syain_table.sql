-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2022 年 11 月 09 日 14:39
-- サーバのバージョン： 8.0.30
-- PHP のバージョン: 7.3.24-(to be removed in future macOS)

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- データベース: `test_schema`
--

-- --------------------------------------------------------

--
-- テーブルの構造 `syain_table`
--

CREATE TABLE `syain_table` (
  `syain_no` int NOT NULL,
  `syain_name` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- テーブルのデータのダンプ `syain_table`
--

INSERT INTO `syain_table` (`syain_no`, `syain_name`) VALUES
(1, '福岡 辰徳'),
(2, '薬院 達也'),
(3, '大濠 千代子'),
(4, '高宮 早苗'),
(5, '大橋 連'),
(6, '井尻 鉄平'),
(7, '平尾 徹子'),
(8, '朝倉 亮'),
(9, '黒崎 郁恵'),
(10, '清川 聖也');

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `syain_table`
--
ALTER TABLE `syain_table`
  ADD PRIMARY KEY (`syain_no`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
