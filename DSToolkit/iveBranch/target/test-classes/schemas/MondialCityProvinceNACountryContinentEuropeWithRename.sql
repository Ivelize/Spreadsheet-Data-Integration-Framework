SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialCityProvinceNACountryContinentEuropeWithRename ;

CREATE DATABASE MondialCityProvinceNACountryContinentEuropeWithRename;

USE MondialCityProvinceNACountryContinentEuropeWithRename;

CREATE TABLE CountryE
(NameE VARCHAR(35) NOT NULL UNIQUE,
 CodeE VARCHAR(4),
 CapitalE VARCHAR(35),
 ProvinceE VARCHAR(35),
 AreaE FLOAT,
 PopulationE INT,
 CONSTRAINT CountryEKey PRIMARY KEY(CodeE),
 CONSTRAINT CountryAreaE CHECK (AreaE >= 0),
 CONSTRAINT CountryPopE CHECK (PopulationE >= 0))
 ENGINE=InnoDB;

CREATE TABLE bordersE
(Country1E VARCHAR(4),
 Country2E VARCHAR(4),
 LengthE FLOAT, 
 CONSTRAINT CHECK (LengthE > 0),
 CONSTRAINT BorderEKey PRIMARY KEY (Country1E,Country2E))
ENGINE=InnoDB;

CREATE TABLE CityE
(NameE VARCHAR(35),
 CountryE VARCHAR(4),
 ProvinceE VARCHAR(35),
 PopulationE INT,
 LongitudeE FLOAT,
 LatitudeE FLOAT,
 CONSTRAINT CityEKey PRIMARY KEY (NameE, CountryE),
 CONSTRAINT CityPopE CHECK (PopulationE >= 0),
 CONSTRAINT CityLonE CHECK ((LongitudeE >= -180) AND (LongitudeE <= 180)),
 CONSTRAINT CityLatE CHECK ((LatitudeE >= -90) AND (LatitudeE <= 90)))
ENGINE=InnoDB;

CREATE TABLE ContinentE
(NameE VARCHAR(20),
 AreaE FLOAT(10),
 CONSTRAINT ContinentEKey PRIMARY KEY(NameE))
ENGINE=InnoDB;

CREATE TABLE encompassesE
(CountryE VARCHAR(4) NOT NULL,
 ContinentE VARCHAR(20) NOT NULL,
 PercentageE FLOAT,
 CONSTRAINT CHECK ((PercentageE > 0) AND (PercentageE <= 100)),
 CONSTRAINT EncompassesEKey PRIMARY KEY (CountryE,ContinentE))
ENGINE=InnoDB;

ALTER TABLE EncompassesE
ADD CONSTRAINT EncompassesCountryEFK
FOREIGN KEY EncompassesCountryEFK (CountryE)
REFERENCES CountryE(CodeE);

ALTER TABLE EncompassesE
ADD CONSTRAINT EncompassesContinentEFK
FOREIGN KEY EncompassesContinentEFK (ContinentE)
REFERENCES ContinentE(NameE);

ALTER TABLE BordersE
ADD CONSTRAINT BordersCountry1EFK
FOREIGN KEY BordersCountry1EFK (Country1E)
REFERENCES CountryE(CodeE);

ALTER TABLE BordersE
ADD CONSTRAINT BordersCountry2EFK
FOREIGN KEY BordersCountry2EFK (Country2E)
REFERENCES CountryE(CodeE);
