SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS Mondial ;

CREATE DATABASE Mondial;

USE Mondial;

CREATE TABLE Country 
(Name VARCHAR(35) NOT NULL UNIQUE,
 Code VARCHAR(4),
 Capital VARCHAR(35),
 Province VARCHAR(35),
 Area FLOAT,
 Population INT,
 CONSTRAINT CountryKey PRIMARY KEY(Code),
 CONSTRAINT CountryArea CHECK (Area >= 0),
 CONSTRAINT CountryPop CHECK (Population >= 0))
 ENGINE=InnoDB;

CREATE TABLE City
(Name VARCHAR(35),
 Country VARCHAR(4),
 Province VARCHAR(35),
 Population INT,
 Longitude FLOAT,
 Latitude FLOAT,
 CONSTRAINT CityKey PRIMARY KEY (Name, Country, Province),
 CONSTRAINT CityPop CHECK (Population >= 0),
 CONSTRAINT CityLon CHECK ((Longitude >= -180) AND (Longitude <= 180)),
 CONSTRAINT CityLat CHECK ((Latitude >= -90) AND (Latitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE Province
(Name VARCHAR(35) NOT NULL,
 Country VARCHAR(4) NOT NULL ,
 Population INT,
 Area FLOAT,
 Capital VARCHAR(35),
 CapProv VARCHAR(35),
 CONSTRAINT PrKey PRIMARY KEY (Name, Country),
 CONSTRAINT PrPop CHECK (Population >= 0),
 CONSTRAINT PrAr CHECK (Area >= 0))
ENGINE=InnoDB;

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

CREATE TABLE Continent
(Name VARCHAR(20),
 Area FLOAT(10),
 CONSTRAINT ContinentKey PRIMARY KEY(Name))
ENGINE=InnoDB;

CREATE TABLE borders
(Country1 VARCHAR(4),
 Country2 VARCHAR(4),
 Length FLOAT, 
 CONSTRAINT CHECK (Length > 0),
 CONSTRAINT BorderKey PRIMARY KEY (Country1,Country2))
ENGINE=InnoDB;

CREATE TABLE encompasses
(Country VARCHAR(4) NOT NULL,
 Continent VARCHAR(20) NOT NULL,
 Percentage FLOAT,
 CONSTRAINT CHECK ((Percentage > 0) AND (Percentage <= 100)),
 CONSTRAINT EncompassesKey PRIMARY KEY (Country,Continent))
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


CREATE TABLE Mountain
(Name VARCHAR(35), 
 Mountains VARCHAR(35),
 Height FLOAT,
 Type VARCHAR(10),
 Longitude FLOAT,
 Latitude FLOAT,
 CONSTRAINT MountainKey PRIMARY KEY(Name),
 CONSTRAINT CHECK ((Longitude >= -180) AND (Longitude <= 180) 
              AND  (Latitude >= -90) AND (Latitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE Desert
(Name VARCHAR(35),
 Area FLOAT,
 Longitude FLOAT,
 Latitude FLOAT,
 CONSTRAINT DesertKey PRIMARY KEY(Name),
 CONSTRAINT DesCoord 
   CHECK ((Longitude >= -180) AND (Longitude <= 180) 
     AND  (Latitude >= -90) AND (Latitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE Island
(Name VARCHAR(35),
 Islands VARCHAR(35),
 Area FLOAT,
 Height FLOAT,
 Type VARCHAR(10),
 CONSTRAINT IslandKey PRIMARY KEY(Name),
 CONSTRAINT IslandAr check (Area >= 0),
 Longitude FLOAT,
 Latitude FLOAT,
 CONSTRAINT IslandCoord
   CHECK ((Longitude >= -180) AND (Longitude <= 180) 
     AND  (Latitude >= -90) AND (Latitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE Lake
(Name VARCHAR(35),
 Area FLOAT,
 Depth FLOAT,
 Altitude FLOAT,
 Type VARCHAR(10),
 River VARCHAR(35),
 Longitude FLOAT,
 Latitude FLOAT,
 CONSTRAINT LakeKey PRIMARY KEY(Name),
 CONSTRAINT LakeAr CHECK (Area >= 0),
 CONSTRAINT LakeDpth CHECK (Depth >= 0),
 CONSTRAINT LakeCoord
   CHECK ((Longitude >= -180) AND (Longitude <= 180) 
     AND  (Latitude >= -90) AND (Latitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE Sea
(Name VARCHAR(35),
 Depth FLOAT,
 CONSTRAINT SeaKey PRIMARY KEY(Name),
 CONSTRAINT SeaDepth CHECK (Depth >= 0))
ENGINE=InnoDB;

CREATE TABLE River
(Name VARCHAR(35),
 River VARCHAR(35),
 Lake VARCHAR(35),
 Sea VARCHAR(35),
 Length FLOAT,
 SourceLongitude FLOAT,
 SourceLatitude FLOAT,
 Mountains VARCHAR(35),
 SourceAltitude FLOAT,
 EstuaryLongitude FLOAT,
 EstuaryLatitude FLOAT,
 CONSTRAINT RiverKey PRIMARY KEY(Name),
 CONSTRAINT RiverLength CHECK (Length >= 0),
 CONSTRAINT SourceCoord
     CHECK ((SourceLongitude >= -180) AND 
            (SourceLongitude <= 180) AND
            (SourceLatitude >= -90) AND
            (SourceLatitude <= 90)),
 CONSTRAINT EstCoord
     CHECK ((EstuaryLongitude >= -180) AND 
            (EstuaryLongitude <= 180) AND
            (EstuaryLatitude >= -90) AND
            (EstuaryLatitude <= 90)))
ENGINE=InnoDB;

CREATE TABLE geo_Mountain
(Mountain VARCHAR(35) ,
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GMountainKey PRIMARY KEY (Province,Country,Mountain) )
ENGINE=InnoDB;

CREATE TABLE geo_Desert
(Desert VARCHAR(35) ,
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GDesertKey PRIMARY KEY (Province, Country, Desert) )
ENGINE=InnoDB;

CREATE TABLE geo_Island
(Island VARCHAR(35) , 
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GIslandKey PRIMARY KEY (Province, Country, Island) )
ENGINE=InnoDB;

CREATE TABLE geo_River
(River VARCHAR(35) , 
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GRiverKey PRIMARY KEY (Province ,Country, River) )
ENGINE=InnoDB;

CREATE TABLE geo_Sea
(Sea VARCHAR(35) ,
 Country VARCHAR(4)  ,
 Province VARCHAR(35) ,
 CONSTRAINT GSeaKey PRIMARY KEY (Province, Country, Sea) )
ENGINE=InnoDB;

CREATE TABLE geo_Lake
(Lake VARCHAR(35) ,
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GLakeKey PRIMARY KEY (Province, Country, Lake) )
ENGINE=InnoDB;

CREATE TABLE geo_Source
(River VARCHAR(35) ,
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GSourceKey PRIMARY KEY (Province, Country, River) )
ENGINE=InnoDB;

CREATE TABLE geo_Estuary
(River VARCHAR(35) ,
 Country VARCHAR(4) ,
 Province VARCHAR(35) ,
 CONSTRAINT GEstuaryKey PRIMARY KEY (Province, Country, River) )
ENGINE=InnoDB;

CREATE TABLE mergesWith
(Sea1 VARCHAR(35) ,
 Sea2 VARCHAR(35) ,
 CONSTRAINT MergesWithKey PRIMARY KEY (Sea1, Sea2) )
ENGINE=InnoDB;

CREATE TABLE located
(City VARCHAR(35) ,
 Province VARCHAR(35) ,
 Country VARCHAR(4) ,
 River VARCHAR(35),
 Lake VARCHAR(35),
 Sea VARCHAR(35) )
ENGINE=InnoDB;

CREATE TABLE locatedOn
(City VARCHAR(35) ,
 Province VARCHAR(35) ,
 Country VARCHAR(4) ,
 Island VARCHAR(35) ,
 CONSTRAINT locatedOnKey PRIMARY KEY (City, Province, Country, Island) )
ENGINE=InnoDB;

CREATE TABLE islandIn
(Island VARCHAR(35) ,
 Sea VARCHAR(35) ,
 Lake VARCHAR(35) ,
 River VARCHAR(35) )
ENGINE=InnoDB;

CREATE TABLE MountainOnIsland
(Mountain VARCHAR(35),
 Island  VARCHAR(35),
 CONSTRAINT MntIslKey PRIMARY KEY (Mountain, Island) )
ENGINE=InnoDB;

ALTER TABLE City
ADD CONSTRAINT CityProvinceFK
FOREIGN KEY CityProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Economy
ADD CONSTRAINT EconomyCountryFK
FOREIGN KEY EconomyCountryFK (country)
REFERENCES Country(Code);

ALTER TABLE EthnicGroup
ADD CONSTRAINT EthicGroupCountryFK
FOREIGN KEY EthnicGroupCountryFK (country)
REFERENCES Country(Code);

ALTER TABLE Lake
ADD CONSTRAINT LakeRiverFK
FOREIGN KEY LakeRiverFK (river)
REFERENCES River(name);

ALTER TABLE Language
ADD CONSTRAINT LanguageCountryFK
FOREIGN KEY LanguageCountryFK (country)
REFERENCES Country(Code);

ALTER TABLE MountainOnIsland
ADD CONSTRAINT MountainOnIslandMountainFK
FOREIGN KEY MountainOnIslandMountainFK (Mountain)
REFERENCES Mountain(name);

ALTER TABLE MountainOnIsland
ADD CONSTRAINT MountainOnIslandIslandFK
FOREIGN KEY MountainOnIslandIslandFK (Island)
REFERENCES Island(name);

ALTER TABLE Politics
ADD CONSTRAINT PoliticsCountryFK
FOREIGN KEY PoliticsCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE Politics
ADD CONSTRAINT PoliticsDependentCountryFK
FOREIGN KEY PoliticsDependentCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE Population
ADD CONSTRAINT PopulationCountryFK
FOREIGN KEY PopulationCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE Province
ADD CONSTRAINT ProvinceCountryFK
FOREIGN KEY ProvinceCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE Religion
ADD CONSTRAINT ReligionCountryFK
FOREIGN KEY ReligionCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE River
ADD CONSTRAINT RiverRiverFK
FOREIGN KEY RiverRiverFK (River)
REFERENCES River(Name);

ALTER TABLE River
ADD CONSTRAINT RiverLakeFK
FOREIGN KEY RiverLakeFK (Lake)
REFERENCES Lake(Name);

ALTER TABLE River
ADD CONSTRAINT RiverSeaFK
FOREIGN KEY RiverSeaFK (Sea)
REFERENCES Sea(Name);

ALTER TABLE Borders
ADD CONSTRAINT BordersCountry1FK
FOREIGN KEY BordersCountry1FK (Country1)
REFERENCES Country(Code);

ALTER TABLE Borders
ADD CONSTRAINT BordersCountry2FK
FOREIGN KEY BordersCountry2FK (Country2)
REFERENCES Country(Code);

ALTER TABLE Encompasses
ADD CONSTRAINT EncompassesCountryFK
FOREIGN KEY EncompassesCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE Encompasses
ADD CONSTRAINT EncompassesContinentFK
FOREIGN KEY EncompassesContinentFK (Continent)
REFERENCES Continent(Name);

ALTER TABLE Geo_Desert
ADD CONSTRAINT Geo_DesertDesertFK
FOREIGN KEY Geo_DesertDesertFK (Desert)
REFERENCES Desert(Name);

ALTER TABLE Geo_Desert
ADD CONSTRAINT Geo_DesertProvinceFK
FOREIGN KEY Geo_DesertProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_Estuary
ADD CONSTRAINT Geo_EstuaryRiverFK
FOREIGN KEY Geo_EstuaryRiverFK (River)
REFERENCES River(Name);

ALTER TABLE Geo_Estuary
ADD CONSTRAINT Geo_EstuaryProvinceFK
FOREIGN KEY Geo_EstuaryProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_Island
ADD CONSTRAINT Geo_IslandIslandFK
FOREIGN KEY Geo_IslandFK (Island)
REFERENCES Island(Name);

ALTER TABLE Geo_Island
ADD CONSTRAINT Geo_IslandProvinceFK
FOREIGN KEY Geo_IslandProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_Lake
ADD CONSTRAINT Geo_LakeLakeFK
FOREIGN KEY Geo_LakeLakeFK (Lake)
REFERENCES Lake(Name);

ALTER TABLE Geo_Lake
ADD CONSTRAINT Geo_LakeProvinceFK
FOREIGN KEY Geo_LakeProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_Mountain
ADD CONSTRAINT Geo_MountainMountainFK
FOREIGN KEY Geo_MountainMountainFK (Mountain)
REFERENCES Mountain(Name);

ALTER TABLE Geo_Mountain
ADD CONSTRAINT Geo_MountainProvinceFK
FOREIGN KEY Geo_MountainProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_River
ADD CONSTRAINT Geo_RiverRiverFK
FOREIGN KEY Geo_RiverRiverFK (River)
REFERENCES River(Name);

ALTER TABLE Geo_River
ADD CONSTRAINT Geo_RiverProvinceFK
FOREIGN KEY Geo_RiverProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_Sea
ADD CONSTRAINT Geo_SeaSeaFK
FOREIGN KEY Geo_SeaSeaFK (Sea)
REFERENCES Sea(Name);

ALTER TABLE Geo_Sea
ADD CONSTRAINT Geo_SeaProvinceFK
FOREIGN KEY Geo_SeaProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE Geo_Source
ADD CONSTRAINT Geo_SourceRiverFK
FOREIGN KEY Geo_SourceRiverFK (River)
REFERENCES River(Name);

ALTER TABLE Geo_Source
ADD CONSTRAINT Geo_SourceProvinceFK
FOREIGN KEY Geo_SourceProvinceFK (Province, Country)
REFERENCES Province(Name, Country);

ALTER TABLE IsMember
ADD CONSTRAINT IsMemberOrganizationFK
FOREIGN KEY IsMemberOrganizationFK (Organization)
REFERENCES Organization(Abbreviation);

ALTER TABLE IsMember
ADD CONSTRAINT IsMemberCountryFK
FOREIGN KEY IsMemberCountryFK (Country)
REFERENCES Country(Code);

ALTER TABLE IslandIn
ADD CONSTRAINT IslandInIslandFK
FOREIGN KEY IslandInIslandFK (Island)
REFERENCES Island(Name);

ALTER TABLE IslandIn
ADD CONSTRAINT IslandInSeaFK
FOREIGN KEY IslandInSeaFK (Sea)
REFERENCES Sea(Name);

ALTER TABLE LocatedOn
ADD CONSTRAINT LocatedOnIslandFK
FOREIGN KEY LocatedOnIslandFK (Island)
REFERENCES Island(Name);

ALTER TABLE MergesWith
ADD CONSTRAINT MergesWithSea1FK
FOREIGN KEY MergesWithSea1FK (Sea1)
REFERENCES Sea(Name);

ALTER TABLE MergesWith
ADD CONSTRAINT MergesWithSea2FK
FOREIGN KEY MergesWithSea2FK (Sea2)
REFERENCES Sea(Name);
