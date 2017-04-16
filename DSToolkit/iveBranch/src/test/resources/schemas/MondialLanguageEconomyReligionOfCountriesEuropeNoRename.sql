SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialLanguageEconomyReligionOfCountriesEuropeNoRename ;

CREATE DATABASE MondialLanguageEconomyReligionOfCountriesEuropeNoRename;

USE MondialLanguageEconomyReligionOfCountriesEuropeNoRename;


CREATE TABLE Economy
(Country VARCHAR(4),
 GDP FLOAT,
 Agriculture FLOAT,
 Service FLOAT,
 Industry FLOAT,
 Inflation FLOAT,
 CONSTRAINT EconomyKey PRIMARY KEY(Country),
 CONSTRAINT EconomyGDP CHECK (GDP >= 0))
ENGINE=InnoDB;

CREATE TABLE Population
(Country VARCHAR(4),
 Population_Growth FLOAT,
 Infant_Mortality FLOAT,
 CONSTRAINT PopKey PRIMARY KEY(Country))
ENGINE=InnoDB;

CREATE TABLE Politics
(Country VARCHAR(4),
 Independence DATE,
 Dependent  VARCHAR(4),
 Government VARCHAR(120),
 CONSTRAINT PoliticsKey PRIMARY KEY(Country))
ENGINE=InnoDB;

CREATE TABLE Language
(Country VARCHAR(4),
 Name VARCHAR(50),
 Percentage FLOAT,
 CONSTRAINT LanguageKey PRIMARY KEY (Name, Country),
 CONSTRAINT LanguagePercent
   CHECK ((Percentage > 0) AND (Percentage <= 100)))
ENGINE=InnoDB;

CREATE TABLE Religion
(Country VARCHAR(4),
 Name VARCHAR(50),
 Percentage FLOAT,
 CONSTRAINT ReligionKey PRIMARY KEY (Name, Country),
 CONSTRAINT ReligionPercent 
   CHECK ((Percentage > 0) AND (Percentage <= 100)))
ENGINE=InnoDB;

CREATE TABLE EthnicGroup
(Country VARCHAR(4),
 Name VARCHAR(50),
 Percentage FLOAT,
 CONSTRAINT EthnicKey PRIMARY KEY (Name, Country),
 CONSTRAINT EthnicPercent 
   CHECK ((Percentage > 0) AND (Percentage <= 100)))
ENGINE=InnoDB;

CREATE TABLE Organization
(Abbreviation VARCHAR(12) PRIMARY KEY,
 Name VARCHAR(80) NOT NULL,
 City VARCHAR(35) ,
 Country VARCHAR(4) , 
 Province VARCHAR(35) ,
 Established DATE,
 CONSTRAINT OrgNameUnique UNIQUE (Name))
ENGINE=InnoDB;

CREATE TABLE isMember
(Country VARCHAR(4),
 Organization VARCHAR(12),
 Type VARCHAR(35) DEFAULT 'member',
 CONSTRAINT MemberKey PRIMARY KEY (Country,Organization) )
ENGINE=InnoDB;

ALTER TABLE IsMember
ADD CONSTRAINT IsMemberOrganizationFK
FOREIGN KEY IsMemberOrganizationFK (Organization)
REFERENCES Organization(Abbreviation);