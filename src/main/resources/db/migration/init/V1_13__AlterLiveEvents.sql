ALTER TABLE live_events DROP COLUMN image_url;
ALTER TABLE live_events DROP COLUMN description_image_url;

ALTER TABLE live_events
    ADD COLUMN file_image_id bigint(20) NOT NULL;
ALTER TABLE live_events
    ADD CONSTRAINT `fk_events_image_file` FOREIGN KEY (file_image_id) REFERENCES files (id);

ALTER TABLE live_events
    ADD COLUMN file_descr_image_id bigint(20) NOT NULL;
ALTER TABLE live_events
    ADD CONSTRAINT `fk_events_descr_image_file` FOREIGN KEY (file_descr_image_id) REFERENCES files (id);

