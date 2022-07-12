ALTER TABLE webinars DROP COLUMN image_url;
ALTER TABLE webinars DROP COLUMN description_image_url;

ALTER TABLE webinars
    ADD COLUMN file_image_id bigint(20) NOT NULL;
ALTER TABLE webinars
    ADD CONSTRAINT `fk_webinar_image_file` FOREIGN KEY (file_image_id) REFERENCES files (id);

ALTER TABLE webinars
    ADD COLUMN file_descr_image_id bigint(20) NOT NULL;
ALTER TABLE webinars
    ADD CONSTRAINT `fk_webinar_descr_image_file` FOREIGN KEY (file_descr_image_id) REFERENCES files (id);

