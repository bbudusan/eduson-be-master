ALTER TABLE users
  ADD COLUMN profile_image_id bigint(20);
ALTER TABLE lectors
  DROP COLUMN profile_image_id;
