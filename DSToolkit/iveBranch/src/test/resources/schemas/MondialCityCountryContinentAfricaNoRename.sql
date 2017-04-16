SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialCityCountryContinentAfricaNoRename ;

CREATE DATABASE MondialCityCountryContinentAfricaNoRename;

USE MondialCityCountryContinentAfricaNoRename;

CREATE TABLE Country
(Name VARCHAR(35) NOT NULL UNIQUE,
 Code VARCHAR(4),
 Capital VARCHAR(35),
 Area FLOAT,
 Population INT,
 CONSTRAINT CountryKey PRIMARY KEY(Code),
 CONSTRAINT CountryArea CHECK (Area >= 0),
 CONSTRAINT CountryPop CHECK (Population >= 0))
 ENGINE=InnoDB;

CREATE TABLE borders
(Country1 VARCHAR(4),
 Country2 VARCHAR(4),
 Length FLOAT, 
 CONSTRAINT CHECK (Length > 0),
 CONSTRAINT BorderKey PRIMARY KEY (Country1,Country2))
ENGINE=InnoDB;

CREATE TABLE City
(Name VARCHAR(35),
 Country VARCHAR(4),
 Population INT,
 Longitude FLOAT,
 Latitude FLOAT,
 CONSTRAINT CityKey PRIMARY KEY (Name, Country),
 CONSTRAINT CityPop CHECK (Population >= 0),
 CONSTRAINT CityLon CHECK ((Longitude >= -180) AND (Longitude <= 180)),
 CONSTRAINT CityLat CHECK ((Latitude >= -90) AND (Latitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE Continent
(Name VARCHAR(20),
 Area FLOAT(10),
 CONSTRAINT ContinentKey PRIMARY KEY(Name))
ENGINE=InnoDB;

CREATE TABLE encompasses
(Country VARCHAR(4) NOT NULL,
 Continent VARCHAR(20) NOT NULL,
 Percentage FLOAT,
 CONSTRAINT CHECK ((Percentage > 0) AND (Percentage <= 100)),
 CONSTRAINT EncompassesKey PRIMARY KEY (Country,Continent))
ENGINE=InnoDB;

ALTER TABLE Encompasses
ADD CONSTRAINT EncompassesCountryFK
FOREIGN KEY EncompassesCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE Encompasses
ADD CONSTRAINT EncompassesContinentFK
FOREIGN KEY EncompassesContinentFK (Continent)
REFERENCES Continent(Name);

ALTER TABLE Borders
ADD CONSTRAINT BordersCountry1FK
FOREIGN KEY BordersCountry1FK (Country1)
REFERENCES Country(Code);

ALTER TABLE Borders
ADD CONSTRAINT BordersCountry2FK
FOREIGN KEY BordersCountry2FK (Country2)
REFERENCES Country(Code);
