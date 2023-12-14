DROP TABLE IF EXISTS `conquest_data`;
CREATE TABLE `conquest_data` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `current_cycle` mediumint(8) unsigned NOT NULL DEFAULT 1,
  `conquest_season_end` bigint(13) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;