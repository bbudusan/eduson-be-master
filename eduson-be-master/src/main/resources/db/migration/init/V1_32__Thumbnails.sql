ALTER TABLE webinars DROP FOREIGN KEY `fk_webinar_descr_image_file`;
ALTER TABLE webinars DROP COLUMN file_descr_image_id;

INSERT INTO files(id) VALUE(-1);
ALTER TABLE courses
    ADD COLUMN file_image_id bigint(20) NOT NULL DEFAULT -1;
ALTER TABLE courses
    ADD CONSTRAINT `fk_course_image_file` FOREIGN KEY (file_image_id) REFERENCES files (id);
