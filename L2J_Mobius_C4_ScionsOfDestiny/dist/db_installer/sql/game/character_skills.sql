-- ---------------------------
-- Table structure for character_skills
-- ---------------------------
CREATE TABLE IF NOT EXISTS character_skills (
  char_obj_id INT NOT NULL default 0,
  skill_id INT NOT NULL default 0,
  skill_level varchar(5) ,
  skill_name varchar(40),
  `class_index` INT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY  (char_obj_id,skill_id,`class_index`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

# RESTORE_SKILLS_FOR_CHAR, DELETE_CHAR_SKILLS
CREATE INDEX idx_charId_classIndex ON character_skills (char_obj_id, class_index);

# UPDATE_CHARACTER_SKILL_LEVEL, ADD_NEW_SKILLS, DELETE_SKILL_FROM_CHAR
CREATE INDEX idx_skillId_charId_classIndex ON character_skills (skill_id, char_obj_id, class_index);
