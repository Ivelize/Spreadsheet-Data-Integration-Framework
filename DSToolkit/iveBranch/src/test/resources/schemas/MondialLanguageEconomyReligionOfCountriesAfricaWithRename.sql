SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialLanguageEconomyReligionOfCountriesAfricaWithRename ;

CREATE DATABASE MondialLanguageEconomyReligionOfCountriesAfricaWithRename;

USE MondialLanguageEconomyReligionOfCountriesAfricaWithRename;


CREATE TABLE EconomyA
(CountryA VARCHAR(4),
 GDPA FLOAT,
 AgricultureA FLOAT,
 ServiceA FLOAT,
 IndustryA FLOAT,
 InflationA FLOAT,
 CONSTRAINT EconomyAKey PRIMARY KEY(CountryA),
 CONSTRAINT EconomyGDPA CHECK (GDPA >= 0))
ENGINE=InnoDB;

CREATE TABLE PopulationA
(CountryA VARCHAR(4),
 Population_GrowthA FLOAT,
 Infant_MortalityA FLOAT,
 CONSTRAINT PopAKey PRIMARY KEY(CountryA))
ENGINE=InnoDB;

CREATE TABLE PoliticsA
(CountryA VARCHAR(4),
 IndependenceA DATE,
 DependentA  VARCHAR(4),
 GovernmentA VARCHAR(120),
 CONSTRAINT PoliticsAKey PRIMARY KEY(CountryA))
ENGINE=InnoDB;

CREATE TABLE LanguageA
(CountryA VARCHAR(4),
 NameA VARCHAR(50),
 PercentageA FLOAT,
 CONSTRAINT LanguageAKey PRIMARY KEY (NameA, CountryA),
 CONSTRAINT LanguagePercentA 
   CHECK ((PercentageA > 0) AND (PercentageA <= 100)))
ENGINE=InnoDB;

CREATE TABLE ReligionA
(CountryA VARCHAR(4),
 NameA VARCHAR(50),
 PercentageA FLOAT,
 CONSTRAINT ReligionAKey PRIMARY KEY (NameA, CountryA),
 CONSTRAINT ReligionPercentA
   CHECK ((PercentageA > 0) AND (PercentageA <= 100)))
ENGINE=InnoDB;

CREATE TABLE EthnicGroupA
(CountryA VARCHAR(4),
 NameA VARCHAR(50),
 PercentageA FLOAT,
 CONSTRAINT EthnicAKey PRIMARY KEY (NameA, CountryA),
 CONSTRAINT EthnicPercentA
   CHECK ((PercentageA > 0) AND (PercentageA <= 100)))
ENGINE=InnoDB;

CREATE TABLE OrganizationA
(AbbreviationA VARCHAR(12) PRIMARY KEY,
 NameA VARCHAR(80) NOT NULL,
 CityA VARCHAR(35) ,
 CountryA VARCHAR(4) , 
 ProvinceA VARCHAR(35) ,
 EstablishedA DATE,
 CONSTRAINT OrgNameUniqueA UNIQUE (NameA))
ENGINE=InnoDB;

CREATE TABLE isMemberA
(CountryA VARCHAR(4),
 OrganizationA VARCHAR(12),
 TypeA VARCHAR(35) DEFAULT 'member',
 CONSTRAINT MemberAKey PRIMARY KEY (CountryA,OrganizationA) )
ENGINE=InnoDB;

ALTER TABLE IsMemberA
ADD CONSTRAINT IsMemberOrganizationAFK
FOREIGN KEY IsMemberOrganizationAFK (OrganizationA)
REFERENCES OrganizationA(AbbreviationA);