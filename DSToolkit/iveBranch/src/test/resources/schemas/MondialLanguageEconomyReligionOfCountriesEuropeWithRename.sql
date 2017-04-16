SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialLanguageEconomyReligionOfCountriesEuropeWithRename ;

CREATE DATABASE MondialLanguageEconomyReligionOfCountriesEuropeWithRename;

USE MondialLanguageEconomyReligionOfCountriesEuropeWithRename;


CREATE TABLE EconomyE
(CountryE VARCHAR(4),
 GDPE FLOAT,
 AgricultureE FLOAT,
 ServiceE FLOAT,
 IndustryE FLOAT,
 InflationE FLOAT,
 CONSTRAINT EconomyEKey PRIMARY KEY(CountryE),
 CONSTRAINT EconomyGDPE CHECK (GDPE >= 0))
ENGINE=InnoDB;

CREATE TABLE PopulationE
(CountryE VARCHAR(4),
 Population_GrowthE FLOAT,
 Infant_MortalityE FLOAT,
 CONSTRAINT PopEKey PRIMARY KEY(CountryE))
ENGINE=InnoDB;

CREATE TABLE PoliticsE
(CountryE VARCHAR(4),
 IndependenceE DATE,
 DependentE  VARCHAR(4),
 GovernmentE VARCHAR(120),
 CONSTRAINT PoliticsEKey PRIMARY KEY(CountryE))
ENGINE=InnoDB;

CREATE TABLE LanguageE
(CountryE VARCHAR(4),
 NameE VARCHAR(50),
 PercentageE FLOAT,
 CONSTRAINT LanguageEKey PRIMARY KEY (NameE, CountryE),
 CONSTRAINT LanguagePercentE 
   CHECK ((PercentageE > 0) AND (PercentageE <= 100)))
ENGINE=InnoDB;

CREATE TABLE ReligionE
(CountryE VARCHAR(4),
 NameE VARCHAR(50),
 PercentageE FLOAT,
 CONSTRAINT ReligionEKey PRIMARY KEY (NameE, CountryE),
 CONSTRAINT ReligionPercentE 
   CHECK ((PercentageE > 0) AND (PercentageE <= 100)))
ENGINE=InnoDB;

CREATE TABLE EthnicGroupE
(CountryE VARCHAR(4),
 NameE VARCHAR(50),
 PercentageE FLOAT,
 CONSTRAINT EthnicKeyE PRIMARY KEY (NameE, CountryE),
 CONSTRAINT EthnicPercentE 
   CHECK ((PercentageE > 0) AND (PercentageE <= 100)))
ENGINE=InnoDB;

CREATE TABLE OrganizationE
(AbbreviationE VARCHAR(12) PRIMARY KEY,
 NameE VARCHAR(80) NOT NULL,
 CityE VARCHAR(35) ,
 CountryE VARCHAR(4) , 
 ProvinceE VARCHAR(35) ,
 EstablishedE DATE,
 CONSTRAINT OrgNameUniqueE UNIQUE (NameE))
ENGINE=InnoDB;

CREATE TABLE isMemberE
(CountryE VARCHAR(4),
 OrganizationE VARCHAR(12),
 TypeE VARCHAR(35) DEFAULT 'member',
 CONSTRAINT MemberEKey PRIMARY KEY (CountryE,OrganizationE) )
ENGINE=InnoDB;

ALTER TABLE IsMemberE
ADD CONSTRAINT IsMemberOrganizationEFK
FOREIGN KEY IsMemberOrganizationEFK (OrganizationE)
REFERENCES OrganizationE(AbbreviationE);