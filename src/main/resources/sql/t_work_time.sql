-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2022 年 11 月 09 日 14:40
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
-- テーブルの構造 `t_work_time`
--

CREATE TABLE `t_work_time` (
  `employee_code` varchar(50) DEFAULT NULL,
  `work_date` varchar(50) DEFAULT NULL,
  `start_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `finish_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `break_start_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `break_finish_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- テーブルのデータのダンプ `t_work_time`
--

INSERT INTO `t_work_time` (`employee_code`, `work_date`, `start_time`, `finish_time`, `break_start_time`, `break_finish_time`) VALUES
('1234', '2022-02-16', '22:12:53', '22:23:53', '22:14:32', '22:14:45'),
('E0236', '2022-02-19', '04:42:01', '04:42:13', '04:42:06', '04:42:09');

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `t_work_time`
--
ALTER TABLE `t_work_time`
  ADD UNIQUE KEY `employee_code` (`employee_code`,`work_date`,`start_time`,`finish_time`,`break_start_time`,`break_finish_time`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
