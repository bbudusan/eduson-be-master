CREATE TABLE `general`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `confidential` BIT NOT NULL,
    `gkey` varchar(500) NOT NULL,
    `version` bigint(20) NOT NULL,
    `lang_code` varchar(10),
    `content` longtext,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_general_gkey` (`gkey`, `version`, `lang_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `general` VALUES (1,_binary '','stripeTestKeySecret',1,'','(((stripe test key secret în text mode cu confidențial bifat...)))'),(2,_binary '\0','stripeKeyPublic',1,'','(((stripe key public în text mode cu confidențial nebifat...)))'),(3,_binary '','stripeKeySecret',1,'','(((stripe key secret în text mode cu confidențial bifat...)))'),(4,_binary '\0','stripeTestKeyPublic',1,'','(((stripe test key public în text mode cu confidențial nebifat...)))'),(5,_binary '\0','stripeTestMode',1,'','(((yes sau no în text mode, cu confidențial nebifat...)))');

