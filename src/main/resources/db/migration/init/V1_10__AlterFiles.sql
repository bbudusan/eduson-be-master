ALTER TABLE files DROP COLUMN name;
ALTER TABLE files DROP COLUMN content_type;
ALTER TABLE files DROP FOREIGN KEY `fk_user_file`;
ALTER TABLE files DROP COLUMN user_id ;

ALTER TABLE files ADD COLUMN filename VARCHAR(200) NULL;
ALTER TABLE files ADD COLUMN file_path VARCHAR(200) NULL;
ALTER TABLE files ADD COLUMN upload_date datetime NULL;
