-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 11 Feb 2024 pada 06.23
-- Versi server: 10.4.28-MariaDB
-- Versi PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tasktracker_db`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tasks`
--

CREATE TABLE `tasks` (
  `kode_task` int(11) NOT NULL,
  `judul` varchar(50) NOT NULL,
  `deskripsi` text NOT NULL,
  `tanggal` varchar(10) NOT NULL,
  `prioritas` enum('low','medium','high') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tasks`
--

INSERT INTO `tasks` (`kode_task`, `judul`, `deskripsi`, `tanggal`, `prioritas`) VALUES
(10, 'coba', 'cobalah', '25-02-2024', 'low'),
(12, 'minum', 'minum air', '13-02-2024', 'low'),
(13, 'makan', 'makan nasi', '16-02-2024', 'low'),
(14, 'belajar', 'belajar kotlin', '17-02-2024', 'medium'),
(15, 'kelas kotlin', 'belajar membuat program kotlin', '21-02-2024', 'medium'),
(16, 'ngeFiks Bug', 'memperbaiki bug yang muncul', '23-02-2024', 'high'),
(17, 'TaskProjeck', 'Mengerjakan projeck sederhana', '24-02-2024', 'high'),
(18, 'mandi pakai sabun', 'biar wangi', '26-02-2024', 'low'),
(19, 'test prioritas', 'deskripsinya', '20-2-2024', 'medium'),
(20, 'tet', 'tetetes', '01-03-2024', 'medium');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `tasks`
--
ALTER TABLE `tasks`
  ADD PRIMARY KEY (`kode_task`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `tasks`
--
ALTER TABLE `tasks`
  MODIFY `kode_task` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
