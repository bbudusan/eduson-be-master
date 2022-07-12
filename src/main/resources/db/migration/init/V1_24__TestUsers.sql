--  password is : Test1234

ALTER TABLE users MODIFY COLUMN username varchar(100) null;

INSERT INTO `users` VALUES (1,'admin','admin','admin','admin@admin.com','$2a$10$c66cg3d5q6KhOBHIyCTuGuaFEO2EjZ8fDwt9sfK6mnP6zB10JsgnO','ACTIVE');
INSERT INTO `users` VALUES (2,'user','user','user','user@user.com','$2a$10$c66cg3d5q6KhOBHIyCTuGuaFEO2EjZ8fDwt9sfK6mnP6zB10JsgnO','ACTIVE');


INSERT INTO `user_roles` VALUES (1,5);
INSERT INTO `user_roles` VALUES (2,4);