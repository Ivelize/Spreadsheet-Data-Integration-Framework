SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS stanford;

CREATE DATABASE `stanford`;

USE stanford;

CREATE TABLE `result` (
  `EXPTID` varchar(100) DEFAULT NULL,
  `ORF_Name` varchar(500) DEFAULT NULL,
  `DESCRIPTION` varchar(500) DEFAULT NULL,
  `GENE_NAME` varchar(500) DEFAULT NULL,
  `BIOLOGICAL_PROCESS` varchar(500) DEFAULT NULL,
  `MOLECULAR_FUNCTION` varchar(500) DEFAULT NULL,
  `DS_GENE_1` varchar(500) DEFAULT NULL,
  `DS_GENE_2` varchar(500) DEFAULT NULL,
  `Name` varchar(500) DEFAULT NULL,
  `Sequence_Type` varchar(500) DEFAULT NULL
) ENGINE=InnoDB;

CREATE TABLE `experiment` (
  `Exptid` varchar(500) DEFAULT NULL,
  `Experiment_Name` varchar(500) DEFAULT NULL,
  `Organism` varchar(500) DEFAULT NULL,
  `Category` varchar(500) DEFAULT NULL,
  `Subcategory` varchar(500) DEFAULT NULL,
  `Description` varchar(500) DEFAULT NULL,
  `Experimenter` varchar(500) DEFAULT NULL
) ENGINE=InnoDB;