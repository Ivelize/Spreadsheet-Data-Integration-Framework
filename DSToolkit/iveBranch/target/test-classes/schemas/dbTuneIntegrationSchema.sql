SET SESSION sql_mode='ANSI,ORACLE';

DROP DATABASE IF EXISTS DBTuneIntegrRDF ;

CREATE DATABASE DBTuneIntegrRDF;

USE DBTuneIntegrRDF;

CREATE TABLE Record
(title VARCHAR(35),
 maker VARCHAR(30),
 description VARCHAR(50),
 date_created VARCHAR(30),
 track_title VARCHAR(50),
 paid_download VARCHAR(35),
 CONSTRAINT RecordKey PRIMARY KEY (title, maker))
ENGINE=InnoDB;

CREATE TABLE MusicArtist
(name VARCHAR(35),
 img VARCHAR(30),
 biography VARCHAR(130),
 homepage VARCHAR(35),
 based_near VARCHAR(35),
 CONSTRAINT MusicArtistKey PRIMARY KEY (name, homepage))
ENGINE=InnoDB;

ALTER TABLE Record
ADD CONSTRAINT RecordMusicArtistFK
FOREIGN KEY RecordMusicArtistFK (maker)
REFERENCES MusicArtist(name);

