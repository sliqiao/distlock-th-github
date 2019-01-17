CREATE TABLE `t_distlock_info` (
  `lock_key` varchar(255) NOT NULL COMMENT ' 锁名称',
  `lock_value` varchar(255) DEFAULT NULL COMMENT '锁值',
  `expire` int(255) unsigned DEFAULT NULL COMMENT '过期时间',
  `acquire_timeout` bigint(20) unsigned DEFAULT NULL COMMENT '获取锁超时时间',
  `acquire_count` smallint(5) unsigned DEFAULT NULL COMMENT '尝试获取锁次数',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`lock_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;