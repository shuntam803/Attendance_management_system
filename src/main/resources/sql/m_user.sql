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
-- テーブルの構造 `m_user`
--

CREATE TABLE `m_user` (
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `confirmation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- テーブルのデータのダンプ `m_user`
--

INSERT INTO `m_user` (`user_id`, `password`, `confirmation`) VALUES
('1111', '1a5faf512691635f5290493695745bef', 'aaaa1111'),
('1112', '1a5faf512691635f5290493695745bef', NULL),
('1212', '1a5faf512691635f5290493695745bef', NULL),
('1234', 'aaaa1111', ''),
('1235', 'abcd1234', ''),
('1236', 'aaaa1111', NULL),
('1237', '1a5faf512691635f5290493695745bef', 'aaaa1111'),
('2222', 'd6b75947c02681f31c90c668c46bf6b8', NULL),
('aaaa1111', '1a5faf512691635f5290493695745bef', NULL),
('E1111', 'd6b75947c02681f31c90c668c46bf6b8', NULL),
('E2222', 'd6b75947c02681f31c90c668c46bf6b8', 'aaaaaaaa');

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `m_user`
--
ALTER TABLE `m_user`
  ADD PRIMARY KEY (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
