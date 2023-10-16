DROP TABLE IF EXISTS `character_surveillances`;
CREATE TABLE IF NOT EXISTS `character_surveillances` (
  `charId` INT UNSIGNED NOT NULL,
  `targetId` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`charId`,`targetId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;