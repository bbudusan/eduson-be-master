ALTER TABLE courses DROP COLUMN course_url;

ALTER TABLE courses ADD COLUMN file_id bigint(20) NOT NULL;
ALTER TABLE courses ADD CONSTRAINT `fk_course_file` FOREIGN KEY (file_id) REFERENCES files(id);




