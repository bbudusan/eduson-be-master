UPDATE `courses` SET `duration` = CONCAT('00:', `duration`) WHERE LENGTH(`duration`) < 7;
ALTER TABLE `courses`
    MODIFY COLUMN `duration` TIME;