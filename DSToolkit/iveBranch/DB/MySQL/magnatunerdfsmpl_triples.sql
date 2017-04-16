-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: magnatunerdfsmpl
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
-- Table structure for table `triples`
--

DROP TABLE IF EXISTS `triples`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `triples` (
  `s` int(11) NOT NULL,
  `p` int(11) NOT NULL,
  `o` int(11) NOT NULL,
  PRIMARY KEY (`s`,`p`,`o`),
  KEY `ObjSubj` (`o`,`s`),
  KEY `PredObj` (`p`,`o`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `triples`
--

LOCK TABLES `triples` WRITE;
/*!40000 ALTER TABLE `triples` DISABLE KEYS */;
INSERT INTO `triples` VALUES (279,98,1),(1,2,3),(1,4,5),(66,4,5),(157,4,5),(183,4,5),(281,4,5),(282,4,5),(283,4,5),(284,4,5),(408,98,6),(6,7,8),(62,7,8),(71,7,8),(122,7,8),(143,7,8),(153,7,8),(168,7,8),(179,7,8),(196,7,8),(208,7,8),(230,7,8),(243,7,8),(291,7,8),(311,7,8),(337,7,8),(395,7,8),(408,7,8),(409,7,8),(410,7,8),(411,7,8),(413,7,8),(414,7,8),(415,7,8),(416,7,8),(417,7,8),(418,7,8),(419,7,8),(6,9,10),(6,11,10),(6,12,13),(62,12,13),(71,12,13),(122,12,13),(143,12,13),(153,12,13),(168,12,13),(179,12,13),(196,12,13),(208,12,13),(230,12,13),(243,12,13),(291,12,13),(311,12,13),(337,12,13),(395,12,13),(403,12,13),(409,12,13),(410,12,13),(411,12,13),(413,12,13),(414,12,13),(415,12,13),(416,12,13),(417,12,13),(418,12,13),(419,12,13),(6,14,15),(294,14,15),(6,16,17),(45,16,17),(62,16,17),(66,16,17),(71,16,17),(75,16,17),(82,16,17),(88,16,17),(99,16,17),(100,16,17),(102,16,17),(104,16,17),(105,16,17),(106,16,17),(109,16,17),(122,16,17),(133,16,17),(143,16,17),(147,16,17),(153,16,17),(157,16,17),(168,16,17),(172,16,17),(179,16,17),(183,16,17),(196,16,17),(204,16,17),(208,16,17),(211,16,17),(225,16,17),(230,16,17),(237,16,17),(243,16,17),(256,16,17),(260,16,17),(265,16,17),(269,16,17),(276,16,17),(281,16,17),(282,16,17),(283,16,17),(284,16,17),(291,16,17),(294,16,17),(308,16,17),(311,16,17),(317,16,17),(323,16,17),(331,16,17),(337,16,17),(340,16,17),(353,16,17),(366,16,17),(373,16,17),(383,16,17),(386,16,17),(392,16,17),(395,16,17),(403,16,17),(409,16,17),(410,16,17),(411,16,17),(413,16,17),(414,16,17),(415,16,17),(416,16,17),(417,16,17),(418,16,17),(419,16,17),(425,16,17),(442,16,17),(445,16,17),(449,16,17),(470,16,17),(473,16,17),(487,16,17),(518,16,17),(523,16,17),(528,16,17),(537,16,17),(556,16,17),(557,16,17),(558,16,17),(575,16,17),(583,16,17),(613,16,17),(6,2,18),(6,4,19),(62,4,19),(71,4,19),(122,4,19),(143,4,19),(153,4,19),(168,4,19),(179,4,19),(196,4,19),(208,4,19),(230,4,19),(243,4,19),(291,4,19),(311,4,19),(337,4,19),(395,4,19),(409,4,19),(410,4,19),(411,4,19),(413,4,19),(414,4,19),(415,4,19),(416,4,19),(417,4,19),(418,4,19),(419,4,19),(211,7,20),(398,39,20),(8,21,22),(20,21,22),(46,21,22),(67,21,22),(76,21,22),(83,21,22),(97,110,22),(134,21,22),(238,21,22),(247,21,22),(279,110,22),(318,21,22),(332,21,22),(398,37,22),(408,110,22),(460,21,22),(554,110,22),(616,110,22),(20,9,23),(20,24,23),(20,25,26),(20,27,28),(8,16,29),(20,16,29),(40,16,29),(46,16,29),(55,16,29),(67,16,29),(76,16,29),(83,16,29),(126,16,29),(134,16,29),(218,16,29),(238,16,29),(247,16,29),(270,16,29),(318,16,29),(324,16,29),(332,16,29),(376,16,29),(387,16,29),(452,16,29),(460,16,29),(481,16,29),(636,16,29),(20,30,31),(20,32,33),(34,35,36),(34,37,38),(40,21,38),(34,39,40),(225,7,40),(34,9,41),(34,42,43),(398,42,43),(438,42,43),(521,42,43),(606,42,43),(34,16,44),(398,16,44),(438,16,44),(521,16,44),(606,16,44),(554,98,45),(45,7,46),(172,7,46),(265,7,46),(308,7,46),(366,7,46),(373,7,46),(425,7,46),(442,7,46),(554,7,46),(556,7,46),(557,7,46),(45,9,47),(45,11,47),(45,12,48),(172,12,48),(265,12,48),(308,12,48),(366,12,48),(373,12,48),(425,12,48),(442,12,48),(556,12,48),(557,12,48),(45,14,49),(66,14,49),(99,14,49),(413,14,49),(487,14,49),(45,2,50),(45,4,51),(172,4,51),(265,4,51),(308,4,51),(366,4,51),(373,4,51),(425,4,51),(442,4,51),(515,4,51),(556,4,51),(557,4,51),(580,112,52),(52,9,53),(52,16,54),(113,16,54),(118,16,54),(140,16,54),(151,16,54),(161,16,54),(201,16,54),(216,16,54),(286,16,54),(289,16,54),(297,16,54),(303,16,54),(306,16,54),(370,16,54),(435,16,54),(458,16,54),(526,16,54),(564,16,54),(620,16,54),(646,16,54),(340,7,55),(438,39,55),(55,21,56),(387,21,56),(438,37,56),(452,21,56),(606,37,56),(55,9,57),(55,24,57),(55,25,58),(55,27,59),(55,30,60),(55,32,61),(408,98,62),(62,9,63),(62,11,63),(62,14,64),(523,14,64),(62,2,65),(279,98,66),(66,7,67),(157,7,67),(183,7,67),(279,7,67),(281,7,67),(282,7,67),(283,7,67),(284,7,67),(66,9,68),(66,11,68),(66,12,69),(82,12,69),(88,12,69),(99,12,69),(100,12,69),(102,12,69),(104,12,69),(105,12,69),(106,12,69),(109,12,69),(157,12,69),(183,12,69),(281,12,69),(282,12,69),(283,12,69),(284,12,69),(66,2,70),(408,98,71),(71,9,72),(71,11,72),(71,14,73),(100,14,73),(133,14,73),(282,14,73),(557,14,73),(575,14,73),(71,2,74),(616,98,75),(75,7,76),(147,7,76),(204,7,76),(256,7,76),(276,7,76),(294,7,76),(353,7,76),(383,7,76),(392,7,76),(445,7,76),(449,7,76),(470,7,76),(473,7,76),(518,7,76),(523,7,76),(528,7,76),(537,7,76),(558,7,76),(575,7,76),(583,7,76),(613,7,76),(616,7,76),(75,9,77),(75,11,77),(75,12,78),(147,12,78),(204,12,78),(256,12,78),(276,12,78),(294,12,78),(353,12,78),(383,12,78),(392,12,78),(445,12,78),(449,12,78),(470,12,78),(473,12,78),(518,12,78),(523,12,78),(528,12,78),(537,12,78),(558,12,78),(575,12,78),(583,12,78),(613,12,78),(75,14,79),(410,14,79),(75,2,80),(75,4,81),(120,4,81),(147,4,81),(204,4,81),(256,4,81),(276,4,81),(294,4,81),(353,4,81),(383,4,81),(392,4,81),(445,4,81),(449,4,81),(470,4,81),(473,4,81),(497,4,81),(518,4,81),(523,4,81),(528,4,81),(537,4,81),(558,4,81),(575,4,81),(583,4,81),(590,4,81),(613,4,81),(618,4,81),(619,4,81),(97,98,82),(82,7,83),(88,7,83),(97,7,83),(99,7,83),(100,7,83),(102,7,83),(104,7,83),(105,7,83),(106,7,83),(109,7,83),(82,9,84),(82,11,84),(82,14,85),(208,14,85),(225,14,85),(449,14,85),(82,2,86),(82,4,87),(88,4,87),(99,4,87),(100,4,87),(102,4,87),(103,4,87),(104,4,87),(105,4,87),(106,4,87),(107,4,87),(109,4,87),(97,98,88),(88,9,89),(88,11,89),(88,14,90),(183,14,90),(211,14,90),(260,14,90),(308,14,90),(418,14,90),(88,2,91),(83,9,92),(83,24,92),(83,25,93),(83,27,94),(83,30,95),(83,32,96),(97,98,99),(97,98,100),(97,9,101),(97,11,101),(97,98,102),(97,98,103),(97,98,104),(97,98,105),(97,98,106),(97,98,107),(97,16,108),(279,16,108),(408,16,108),(554,16,108),(616,16,108),(97,98,109),(111,112,113),(111,114,115),(111,9,116),(111,16,117),(139,16,117),(188,16,117),(200,16,117),(254,16,117),(285,16,117),(300,16,117),(302,16,117),(314,16,117),(357,16,117),(369,16,117),(402,16,117),(406,16,117),(503,16,117),(580,16,117),(314,112,118),(118,9,119),(616,98,120),(120,2,121),(408,98,122),(122,9,123),(122,11,123),(122,14,124),(122,2,125),(260,7,126),(126,21,127),(126,9,128),(126,24,128),(126,25,129),(126,27,130),(126,30,131),(126,32,132),(603,189,133),(133,7,134),(133,9,135),(133,11,135),(133,12,136),(133,2,137),(133,4,138),(399,187,139),(139,112,140),(139,114,141),(139,9,142),(408,98,143),(143,9,144),(143,11,144),(143,14,145),(237,14,145),(392,14,145),(143,2,146),(616,98,147),(147,9,148),(147,11,148),(147,14,149),(414,14,149),(147,2,150),(406,112,151),(151,9,152),(408,98,153),(153,9,154),(153,11,154),(153,14,155),(470,14,155),(153,2,156),(279,98,157),(157,9,158),(157,11,158),(105,14,159),(157,14,159),(415,14,159),(425,14,159),(518,14,159),(157,2,160),(161,9,162),(8,9,163),(8,24,163),(8,25,164),(8,27,165),(8,30,166),(8,32,167),(408,98,168),(168,9,169),(168,11,169),(168,14,170),(445,14,170),(168,2,171),(554,98,172),(172,9,173),(172,11,173),(109,14,174),(172,14,174),(281,14,174),(317,14,174),(409,14,174),(528,14,174),(172,2,175),(102,9,176),(102,11,176),(102,14,177),(265,14,177),(411,14,177),(102,2,178),(408,98,179),(179,9,180),(179,11,180),(106,14,181),(179,14,181),(283,14,181),(323,14,181),(473,14,181),(556,14,181),(179,2,182),(279,98,183),(183,9,184),(183,11,184),(183,2,185),(186,187,188),(186,189,190),(186,9,191),(36,16,192),(186,16,192),(253,16,192),(299,16,192),(399,16,192),(401,16,192),(405,16,192),(439,16,192),(578,16,192),(603,16,192),(104,9,193),(104,11,193),(104,14,194),(284,14,194),(337,14,194),(386,14,194),(442,14,194),(613,14,194),(104,2,195),(408,98,196),(196,9,197),(196,11,197),(196,14,198),(340,14,198),(383,14,198),(403,14,198),(196,2,199),(36,187,200),(200,112,201),(200,114,202),(200,9,203),(616,98,204),(204,9,205),(204,11,205),(204,14,206),(366,14,206),(395,14,206),(204,2,207),(408,98,208),(208,9,209),(208,11,209),(208,2,210),(399,189,211),(211,9,212),(211,11,212),(211,12,213),(211,2,214),(211,4,215),(188,112,216),(216,9,217),(218,21,219),(218,9,220),(218,24,220),(218,25,221),(218,27,222),(218,30,223),(218,32,224),(36,189,225),(225,9,226),(225,11,226),(225,12,227),(225,2,228),(225,4,229),(408,98,230),(230,9,231),(230,11,231),(230,14,232),(230,2,233),(439,189,234),(234,2,235),(234,4,236),(253,189,237),(237,7,238),(237,9,239),(237,11,239),(237,12,240),(237,2,241),(237,4,242),(408,98,243),(243,9,244),(243,11,244),(243,14,245),(537,14,245),(243,2,246),(247,9,248),(247,24,248),(247,25,249),(247,27,250),(247,30,251),(247,32,252),(253,187,254),(253,9,255),(616,98,256),(256,9,257),(256,11,257),(256,14,258),(311,14,258),(256,2,259),(299,189,260),(260,9,261),(260,11,261),(260,12,262),(260,2,263),(260,4,264),(554,98,265),(265,9,266),(265,11,266),(265,2,267),(103,2,268),(269,7,270),(269,9,271),(269,11,271),(269,12,272),(269,14,273),(276,14,273),(291,14,273),(331,14,273),(373,14,273),(269,2,274),(269,4,275),(616,98,276),(276,9,277),(276,11,277),(276,2,278),(67,9,280),(67,24,280),(279,9,280),(279,11,280),(279,98,281),(279,98,282),(279,98,283),(279,98,284),(285,112,286),(285,114,287),(285,9,288),(503,112,289),(289,9,290),(408,98,291),(291,9,292),(291,11,292),(291,2,293),(616,98,294),(294,9,295),(294,11,295),(294,2,296),(300,112,297),(297,9,298),(299,187,300),(299,9,301),(302,112,303),(302,114,304),(302,9,305),(357,112,306),(306,9,307),(554,98,308),(308,9,309),(308,11,309),(308,2,310),(408,98,311),(311,9,312),(311,11,312),(311,2,313),(603,187,314),(314,114,315),(314,9,316),(317,7,318),(317,9,319),(317,11,319),(317,12,320),(317,2,321),(317,4,322),(323,7,324),(323,9,325),(323,11,325),(323,12,326),(323,2,327),(323,4,328),(105,9,329),(105,11,329),(105,2,330),(331,7,332),(331,9,333),(331,11,333),(331,12,334),(331,2,335),(331,4,336),(408,98,337),(337,9,338),(337,11,338),(337,2,339),(340,9,341),(340,11,341),(340,12,342),(340,2,343),(340,4,344),(332,9,345),(332,24,345),(332,9,346),(332,24,346),(332,27,347),(332,9,348),(332,24,348),(332,9,349),(332,24,349),(332,25,350),(332,30,351),(332,32,352),(616,98,353),(353,9,354),(353,11,354),(353,14,355),(417,14,355),(353,2,356),(439,187,357),(357,114,358),(357,9,359),(134,9,360),(134,24,360),(134,25,361),(134,27,362),(134,30,363),(134,32,364),(107,2,365),(554,98,366),(366,9,367),(366,11,367),(366,2,368),(369,112,370),(369,114,371),(369,9,372),(554,98,373),(373,9,374),(373,11,374),(373,2,375),(487,7,376),(376,21,377),(636,21,377),(376,9,378),(376,24,378),(376,25,379),(376,27,380),(376,30,381),(376,32,382),(616,98,383),(383,9,384),(383,11,384),(383,2,385),(405,189,386),(386,7,387),(606,39,387),(386,9,388),(386,11,388),(386,12,389),(386,2,390),(386,4,391),(616,98,392),(392,9,393),(392,11,393),(392,2,394),(408,98,395),(395,9,396),(395,11,396),(395,2,397),(398,35,399),(398,9,400),(521,35,401),(401,187,402),(401,189,403),(401,9,404),(606,35,405),(405,187,406),(405,9,407),(408,98,409),(408,98,410),(408,98,411),(408,9,412),(408,11,412),(408,98,413),(408,98,414),(408,98,415),(408,98,416),(408,98,417),(408,98,418),(408,98,419),(40,9,420),(40,24,420),(40,25,421),(40,27,422),(40,30,423),(40,32,424),(554,98,425),(425,9,426),(425,11,426),(425,2,427),(286,9,428),(324,21,429),(324,9,430),(324,24,430),(324,25,431),(324,27,432),(324,30,433),(324,32,434),(435,9,436),(140,9,437),(438,35,439),(438,9,440),(113,9,441),(554,98,442),(442,9,443),(442,11,443),(442,2,444),(616,98,445),(445,9,446),(445,11,446),(445,2,447),(303,9,448),(616,98,449),(449,9,450),(449,11,450),(449,2,451),(452,9,453),(452,24,453),(452,25,454),(452,27,455),(452,30,456),(452,32,457),(254,112,458),(458,9,459),(460,9,461),(460,24,461),(460,25,462),(460,27,463),(460,30,464),(460,32,465),(406,114,466),(406,9,467),(254,114,468),(254,9,469),(616,98,470),(470,9,471),(470,11,471),(470,2,472),(616,98,473),(473,9,474),(473,11,474),(473,2,475),(238,9,476),(238,24,476),(238,25,477),(238,27,478),(238,30,479),(238,32,480),(403,7,481),(521,39,481),(403,9,482),(403,11,482),(403,2,483),(403,4,484),(411,9,485),(411,11,485),(411,2,486),(578,189,487),(487,9,488),(487,11,488),(487,12,489),(487,2,490),(487,4,491),(410,9,492),(410,11,492),(410,2,493),(201,9,494),(36,9,495),(439,9,496),(616,98,497),(497,2,498),(67,25,499),(67,27,500),(67,30,501),(67,32,502),(578,187,503),(503,114,504),(503,9,505),(282,9,506),(282,11,506),(282,2,507),(481,9,508),(481,24,508),(481,32,509),(481,9,510),(481,24,510),(481,25,511),(481,30,512),(481,27,513),(481,21,514),(521,37,514),(554,98,515),(515,2,516),(370,9,517),(616,98,518),(518,9,519),(518,11,519),(518,2,520),(521,9,522),(616,98,523),(523,9,524),(523,11,524),(523,2,525),(526,9,527),(616,98,528),(528,9,529),(528,11,529),(528,2,530),(76,30,531),(76,9,532),(76,24,532),(76,25,533),(76,32,534),(76,9,535),(76,24,535),(76,27,536),(616,98,537),(537,9,538),(537,11,538),(537,2,539),(418,9,540),(418,11,540),(418,2,541),(417,9,542),(417,11,542),(417,2,543),(387,9,544),(387,24,544),(387,25,545),(387,27,546),(387,30,547),(387,32,548),(318,9,549),(318,24,549),(318,25,550),(318,27,551),(318,30,552),(318,32,553),(554,9,555),(554,11,555),(557,9,555),(557,11,555),(554,98,556),(554,98,557),(616,98,558),(558,9,559),(558,11,559),(416,14,560),(558,14,560),(558,2,561),(284,9,562),(284,11,562),(284,2,563),(402,112,564),(402,114,565),(402,9,566),(109,9,567),(109,11,567),(109,2,568),(283,9,569),(283,11,569),(283,2,570),(556,9,571),(556,11,571),(556,2,572),(415,9,573),(415,11,573),(415,2,574),(616,98,575),(575,9,576),(575,11,576),(575,2,577),(578,9,579),(580,114,581),(580,9,582),(616,98,583),(583,9,584),(583,11,584),(419,14,585),(583,14,585),(583,2,586),(413,9,587),(413,11,587),(413,2,588),(399,9,589),(616,98,590),(590,2,591),(190,2,592),(190,4,593),(100,9,594),(100,11,594),(100,2,595),(419,9,596),(419,11,596),(419,2,597),(99,9,598),(99,11,598),(99,2,599),(557,2,600),(281,9,601),(281,11,601),(281,2,602),(603,9,604),(564,9,605),(606,9,607),(46,9,608),(46,24,608),(46,25,609),(46,27,610),(46,30,611),(46,32,612),(616,98,613),(613,9,614),(613,11,614),(613,2,615),(616,9,617),(616,11,617),(616,98,618),(616,98,619),(620,9,621),(618,2,622),(270,21,623),(270,9,624),(270,24,624),(270,25,625),(270,27,626),(270,30,627),(270,32,628),(409,9,629),(409,11,629),(409,2,630),(619,2,631),(188,114,632),(188,9,633),(416,9,634),(416,11,634),(416,2,635),(636,9,637),(636,24,637),(636,25,638),(636,27,639),(636,30,640),(636,32,641),(300,114,642),(300,9,643),(414,9,644),(414,11,644),(414,2,645),(646,9,647),(106,9,648),(106,11,648),(106,2,649);
/*!40000 ALTER TABLE `triples` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-10 12:58:07
