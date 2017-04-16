-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: studentsrdfsource
-- ------------------------------------------------------
-- Server version	5.5.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `nodes`
--

DROP TABLE IF EXISTS `nodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nodes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hash` bigint(20) NOT NULL DEFAULT '0',
  `lex` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `lang` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `datatype` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Hash` (`hash`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nodes`
--

LOCK TABLES `nodes` WRITE;
/*!40000 ALTER TABLE `nodes` DISABLE KEYS */;
INSERT INTO `nodes` VALUES (1,8875736020274405042,'http://localhost:2020/modules/2','','',2),(2,-5596434070047198370,'http://localhost:2020/vocab/resource/modules_Credits','','',2),(3,-4603872457861794977,'30','','http://www.w3.org/2001/XMLSchema#int',50),(4,-7418445028574973109,'http://localhost:2020/vocab/resource/modules_ModuleID','','',2),(5,932222845101669323,'2','','http://www.w3.org/2001/XMLSchema#int',50),(6,6454844767405606854,'http://www.w3.org/2000/01/rdf-schema#label','','',2),(7,-6454819501686944935,'modules #2','','',3),(8,5447108141082235195,'http://localhost:2020/vocab/resource/modules_Description','','',2),(9,4986439272468100940,'RDF','','',3),(10,-3892017155795612136,'http://localhost:2020/vocab/resource/modules_ModuleName','','',2),(11,-8212691018667719890,'Semantic Web','','',3),(12,-6430697865200335348,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','',2),(13,-1363942363350255704,'http://localhost:2020/vocab/resource/modules','','',2),(14,-8600874766312189208,'http://localhost:2020/vocab/resource/enrol','','',2),(15,-3229882937365982287,'http://www.w3.org/1999/02/22-rdf-syntax-ns#Property','','',2),(16,-285671646489684579,'http://localhost:2020/vocab/resource/phonenumber_phonetype','','',2),(17,-3524404235730828485,'phonenumber phonetype','','',3),(18,8718148246437760209,'modules ModuleID','','',3),(19,-7628043507419343041,'http://localhost:2020/vocab/resource/students_Homepage','','',2),(20,-3523831644706185533,'students Homepage','','',3),(21,-18271461843544702,'http://localhost:2020/vocab/resource/students_Address','','',2),(22,4483306903113031770,'students Address','','',3),(23,-6742476896855914691,'modules Description','','',3),(24,-5482381146830461686,'http://localhost:2020/vocab/resource/universities_Type','','',2),(25,8228417305495264020,'universities Type','','',3),(26,-796823422657994095,'http://localhost:2020/vocab/resource/universities','','',2),(27,3344506816075049889,'universities','','',3),(28,-1253177975019506333,'http://www.w3.org/2000/01/rdf-schema#Class','','',2),(29,1649171956126690944,'http://localhost:2020/students/2','','',2),(30,-3322052283363708155,'http://localhost:2020/modules/3','','',2),(31,274116909724666884,'http://localhost:2020/vocab/resource/students_RegisterTo','','',2),(32,-9040413629627409759,'http://localhost:2020/universities/2','','',2),(33,791611039813108567,'students #2','','',3),(34,-2004966132683620859,'http://localhost:2020/vocab/resource/students_Email','','',2),(35,8518792827956584115,'mailto:varunr@isi.edu','','',3),(36,186098358900095886,'http://localhost:2020/vocab/resource/students_StdnID','','',2),(37,-987031880215081024,'http://localhost:2020/vocab/resource/students_FirstName','','',2),(38,-7815619278418514806,'Varun','','',3),(39,6618448892830473549,'http://localhost:2020/vocab/resource/students_LastName','','',2),(40,-6282341409124359333,'Ratnakar','','',3),(41,4249955594542231905,'http://www.isi.edu/~varunr','','',3),(42,-1512270185557296192,'http://localhost:2020/vocab/resource/students_Type','','',2),(43,3157969155762781128,'Undergraduate','','',3),(44,-1775544993257617587,'http://localhost:2020/vocab/resource/students','','',2),(45,5662076315913095485,'http://localhost:2020/phonenumber/1/0161-382-311/home','','',2),(46,-8414286179728440956,'http://localhost:2020/vocab/resource/phonenumber_StdnID','','',2),(47,4005137583622999995,'http://localhost:2020/students/1','','',2),(48,-3227149800666513249,'home','','',3),(49,-579570203935974769,'http://localhost:2020/vocab/resource/phonenumber_number','','',2),(50,-7419687408658648658,'0161-382-311','','',3),(51,2302127452550211484,'phonenumber #1/0161-382-311/home','','',3),(52,-3488530000671081768,'http://localhost:2020/vocab/resource/phonenumber','','',2),(53,6931649296045260881,'students','','',3),(54,-5914097646471422671,'http://localhost:2020/vocab/resource/universities_URI','','',2),(55,-4986397997566810104,'http://annotation.semanticweb.org/iswc/iswc.daml#University_of_Karlsruhe','','',3),(56,9176909122022002278,'University','','',3),(57,1193682104245689041,'http://localhost:2020/vocab/resource/universities_Name','','',2),(58,4141854726163105342,'University of Karlsruhe','','',3),(59,-139248437691100547,'http://localhost:2020/vocab/resource/universities_UniID','','',2),(60,-4515436358940006383,'universities #2','','',3),(61,-4115314222912580806,'students LastName','','',3),(62,-5282513933336300095,'phonenumber number','','',3),(63,-2078842656688530277,'phonenumber','','',3),(64,-3480181842784604861,'http://localhost:2020/vocab/resource/universities_Location','','',2),(65,-4595378215553410041,'universities Location','','',3),(66,359782993536693166,'universities UniID','','',3),(67,985694016956904503,'http://localhost:2020/modules/1','','',2),(68,-5569243937520889879,'15','','http://www.w3.org/2001/XMLSchema#int',50),(69,-5077976256804839953,'1','','http://www.w3.org/2001/XMLSchema#int',50),(70,3175292698398593403,'modules #1','','',3),(71,-624356023041797831,'Module on databases','','',3),(72,-6381732673740515214,'Databases','','',3),(73,-3018158878546347071,'students FirstName','','',3),(74,-2396463905046985032,'students StdnID','','',3),(75,-5177786985860276072,'students Type','','',3),(76,873113571595113808,'http://localhost:2020/phonenumber/1/310-448-8794/mobile','','',2),(77,-1964385087329367642,'mobile','','',3),(78,-3029671871329096046,'310-448-8794','','',3),(79,4458978457382680804,'phonenumber #1/310-448-8794/mobile','','',3),(80,5342301289433076621,'modules','','',3),(81,-4813405148622764452,'http://localhost:2020/vocab/resource/universities_Country','','',2),(82,3644029032105028214,'universities Country','','',3),(83,-3943791915681902477,'students Email','','',3),(84,-2317237285936319628,'universities Name','','',3),(85,-7632891325301853355,'modules ModuleName','','',3),(86,3233270481311514157,'http://localhost:2020/universities/1','','',2),(87,-4699809287500719289,'http://trellis.semanticweb.org/expect/web/semanticweb/iswc02_trellis.pdf#ISI','','',3),(88,3302660848654790160,'Organization','','',3),(89,1854600928111015685,'United States','','',3),(90,8213470276166596666,'California','','',3),(91,-7200687249861693507,'http://localhost:2020/vocab/resource/universities_Address','','',2),(92,1994981515288877357,'4676 Admirality Way, Marina Del Rey','','',3),(93,-2001856376644310305,'USC Information Sciences Institute','','',3),(94,-4769937600961980847,'universities #1','','',3),(95,-5197489147295607060,'universities URI','','',3),(96,-1331513398225098530,'3','','http://www.w3.org/2001/XMLSchema#int',50),(97,1780942976621804182,'modules #3','','',3),(98,-1961043571762836867,'OS','','',3),(99,330307692964565070,'Operating Sys','','',3),(100,-842173249159277777,'students #1','','',3),(101,1329865297372603395,'mailto:gil@isi.edu','','',3),(102,-3004963672858502406,'Yolanda','','',3),(103,-5503504698844759347,'Gil','','',3),(104,9208723693715292134,'http://www.isi.edu/~gil','','',3),(105,5152045631693983773,'Postgraduate','','',3),(106,-6335339008123487078,'universities Address','','',3),(107,1325547038913938815,'modules Credits','','',3);
/*!40000 ALTER TABLE `nodes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-10 12:58:08
