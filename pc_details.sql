-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 12, 2014 at 10:55 AM
-- Server version: 5.5.8
-- PHP Version: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `lab_pc`
--

-- --------------------------------------------------------

--
-- Table structure for table `pc_details`
--

CREATE TABLE IF NOT EXISTS `pc_details` (
  `pc_name` text NOT NULL,
  `ip` text NOT NULL,
  `mac` text NOT NULL,
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `pc_details`
--

INSERT INTO `pc_details` (`pc_name`, `ip`, `mac`, `id`) VALUES
('SUDIP-PC.mshome.net', '192.168.137.200', '   10-bf-48-d9-bf-9e', 1),
('RUET.mshome.net', '192.168.137.228', '   10-1f-74-5a-63-82', 2),
('SENSOR.mshome.net', '192.168.137.248', '   50-e5-49-26-49-de', 3);
