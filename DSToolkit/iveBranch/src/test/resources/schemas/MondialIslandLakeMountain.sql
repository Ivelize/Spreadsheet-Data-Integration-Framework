SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS MondialIslandLakeMountain ;

CREATE DATABASE MondialIslandLakeMountain;

USE MondialIslandLakeMountain;


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

ALTER TABLE MountainOnIsland
ADD CONSTRAINT MountainOnIslandMountainFK
FOREIGN KEY MountainOnIslandMountainFK (Mountain)
REFERENCES Mountain(name);

ALTER TABLE MountainOnIsland
ADD CONSTRAINT MountainOnIslandIslandFK
FOREIGN KEY MountainOnIslandIslandFK (Island)
REFERENCES Island(name);

ALTER TABLE Geo_Desert
ADD CONSTRAINT Geo_DesertDesertFK
FOREIGN KEY Geo_DesertDesertFK (Desert)
REFERENCES Desert(Name);

ALTER TABLE Geo_Estuary
ADD CONSTRAINT Geo_EstuaryRiverFK
FOREIGN KEY Geo_EstuaryRiverFK (River)
REFERENCES River(Name);

ALTER TABLE Geo_Island
ADD CONSTRAINT Geo_IslandIslandFK
FOREIGN KEY Geo_IslandFK (Island)
REFERENCES Island(Name);

ALTER TABLE Geo_Lake
ADD CONSTRAINT Geo_LakeLakeFK
FOREIGN KEY Geo_LakeLakeFK (Lake)
REFERENCES Lake(Name);

ALTER TABLE Geo_Mountain
ADD CONSTRAINT Geo_MountainMountainFK
FOREIGN KEY Geo_MountainMountainFK (Mountain)
REFERENCES Mountain(Name);

ALTER TABLE Geo_River
ADD CONSTRAINT Geo_RiverRiverFK
FOREIGN KEY Geo_RiverRiverFK (River)
REFERENCES River(Name);

ALTER TABLE Geo_Sea
ADD CONSTRAINT Geo_SeaSeaFK
FOREIGN KEY Geo_SeaSeaFK (Sea)
REFERENCES Sea(Name);

ALTER TABLE Geo_Source
ADD CONSTRAINT Geo_SourceRiverFK
FOREIGN KEY Geo_SourceRiverFK (River)
REFERENCES River(Name);

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

