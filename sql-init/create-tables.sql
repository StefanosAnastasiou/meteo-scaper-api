-- --------------------------------*
-- Table structure for table `City`
-- --------------------------------*
DROP TABLE IF EXISTS `City`;
CREATE TABLE `City` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `city` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -------------------------------*
-- Table structure for table `Day`
-- -------------------------------*
DROP TABLE IF EXISTS `Day`;
CREATE TABLE `Day` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `day` date DEFAULT NULL,
      `city_id` int(11) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `city_id` (`city_id`),
      CONSTRAINT `Day_ibfk_1` FOREIGN KEY (`city_id`) REFERENCES `City` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2179 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ---------------------------------------*
-- Table structure for table `Measurement`
-- ---------------------------------------*
DROP TABLE IF EXISTS `Measurement`;
CREATE TABLE `Measurement` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `time` time DEFAULT NULL,
      `temperature` int(11) DEFAULT NULL,
      `humidity` int(11) DEFAULT NULL,
      `wind` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
      `phenomeno` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
      `day_id` int(11) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `day_id` (`day_id`),
      CONSTRAINT `Measurement_ibfk_1` FOREIGN KEY (`day_id`) REFERENCES `Day` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------
-- Structure for function 'MeasurementId'
-- ------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `MeasurementId`(city CHAR(50)) RETURNS int
    DETERMINISTIC
BEGIN
    DECLARE measurementId INT;
    DECLARE ct VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE utf8mb4_unicode_ci;
    SET ct = city;
    SET measurementId = (SELECT M.id FROM Measurement AS M
                           JOIN Day AS D on M.day_id = D.id
                           JOIN City AS C on D.city_id = C.id where C.city=ct
                           AND D.day >= current_date and M.time >
                                IF(CURRENT_TIME BETWEEN "23:00:00" AND "23:59:59",  "23:59:59", current_time) ORDER BY M.id ASC LIMIT 1);
    RETURN measurementId;
END ;;
DELIMITER ;