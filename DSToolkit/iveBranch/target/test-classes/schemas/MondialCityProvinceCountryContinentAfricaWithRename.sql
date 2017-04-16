SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialCityProvinceCountryContinentAfricaWithRename ;

CREATE DATABASE MondialCityProvinceCountryContinentAfricaWithRename;

USE MondialCityProvinceCountryContinentAfricaWithRename;

CREATE TABLE CountryA
(NameA VARCHAR(35) NOT NULL UNIQUE,
 CodeA VARCHAR(4),
 CapitalA VARCHAR(35),
 ProvinceA VARCHAR(35),
 AreaA FLOAT,
 PopulationA INT,
 CONSTRAINT CountryAKey PRIMARY KEY(CodeA),
 CONSTRAINT CountryAreaA CHECK (AreaA >= 0),
 CONSTRAINT CountryPopA CHECK (PopulationA >= 0))
 ENGINE=InnoDB;

CREATE TABLE bordersA
(Country1A VARCHAR(4),
 Country2A VARCHAR(4),
 LengthA FLOAT, 
 CONSTRAINT CHECK (LengthA > 0),
 CONSTRAINT BorderAKey PRIMARY KEY (Country1A,Country2A))
ENGINE=InnoDB;

CREATE TABLE CityA
(NameA VARCHAR(35),
 CountryA VARCHAR(4),
 ProvinceA VARCHAR(35),
 PopulationA INT,
 LongitudeA FLOAT,
 LatitudeA FLOAT,
 CONSTRAINT CityAKey PRIMARY KEY (NameA, CountryA, ProvinceA),
 CONSTRAINT CityPopA CHECK (PopulationA >= 0),
 CONSTRAINT CityLonA CHECK ((LongitudeA >= -180) AND (LongitudeA <= 180)),
 CONSTRAINT CityLatA CHECK ((LatitudeA >= -90) AND (LatitudeA <= 90)))
ENGINE=InnoDB;

CREATE TABLE ProvinceA
(NameA VARCHAR(35) NOT NULL,
 CountryA VARCHAR(4) NOT NULL ,
 PopulationA INT,
 AreaA FLOAT,
 CapitalA VARCHAR(35),
 CapProvA VARCHAR(35),
 CONSTRAINT PrAKey PRIMARY KEY (NameA, CountryA),
 CONSTRAINT PrPopA CHECK (PopulationA >= 0),
 CONSTRAINT PrArA CHECK (AreaA >= 0))
ENGINE=InnoDB;

CREATE TABLE ContinentA
(NameA VARCHAR(20),
 AreaA FLOAT(10),
 CONSTRAINT ContinentAKey PRIMARY KEY(NameA))
ENGINE=InnoDB;

CREATE TABLE encompassesA
(CountryA VARCHAR(4) NOT NULL,
 ContinentA VARCHAR(20) NOT NULL,
 PercentageA FLOAT,
 CONSTRAINT CHECK ((PercentageA > 0) AND (PercentageA <= 100)),
 CONSTRAINT EncompassesAKey PRIMARY KEY (CountryA,ContinentA))
ENGINE=InnoDB;

ALTER TABLE CityA
ADD CONSTRAINT CityProvinceAFK
FOREIGN KEY CityProvinceAFK (ProvinceA, CountryA)
REFERENCES ProvinceA(NameA, CountryA);

ALTER TABLE ProvinceA
ADD CONSTRAINT ProvinceCountryAFK
FOREIGN KEY ProvinceCountryAFK (CountryA)
REFERENCES CountryA(CodeA);

ALTER TABLE EncompassesA
ADD CONSTRAINT EncompassesCountryAFK
FOREIGN KEY EncompassesCountryAFK (CountryA)
REFERENCES CountryA(CodeA);

ALTER TABLE EncompassesA
ADD CONSTRAINT EncompassesContinentAFK
FOREIGN KEY EncompassesContinentAFK (ContinentA)
REFERENCES ContinentA(NameA);

ALTER TABLE BordersA
ADD CONSTRAINT BordersCountry1AFK
FOREIGN KEY BordersCountry1AFK (Country1A)
REFERENCES CountryA(CodeA);

ALTER TABLE BordersA
ADD CONSTRAINT BordersCountry2AFK
FOREIGN KEY BordersCountry2AFK (Country2A)
REFERENCES CountryA(CodeA);