-- 创建数据库qiyu_live_user，使用utf8mb3字符集和utf8_bin排序规则
create database qiyu_live_user character set utf8mb3 collate utf8_bin;

use qiyu_live_user;
-- 更改存储过程的结束符为$$
DELIMITER $$

-- 创建一个名为create_t_user_100的存储过程
CREATE PROCEDURE qiyu_live_user.create_t_user_100()
BEGIN

    -- 声明变量
    DECLARE i INT;
    DECLARE table_name VARCHAR(30);
    DECLARE table_pre VARCHAR(30);
    DECLARE sql_text VARCHAR(3000);
    DECLARE table_body VARCHAR(2000);

    -- 初始化变量
    SET i = 0;
    SET table_name = '';

    -- 初始化SQL语句片段
    SET sql_text = '';
    -- 定义表的主体结构
    SET table_body = '(
  user_id bigint NOT NULL DEFAULT -1 COMMENT ''用户id'',
  nick_name varchar(35)  DEFAULT NULL COMMENT ''昵称'',
  avatar varchar(255)  DEFAULT NULL COMMENT ''头像'',
  true_name varchar(20)  DEFAULT NULL COMMENT ''真实姓名'',
  sex tinyint(1) DEFAULT NULL COMMENT ''性别 0男，1女'',
  born_date datetime DEFAULT NULL COMMENT ''出生时间'',
  work_city int(9) DEFAULT NULL COMMENT ''工作地'',
  born_city int(9) DEFAULT NULL COMMENT ''出生地'',
  create_time datetime DEFAULT CURRENT_TIMESTAMP,
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;';

    -- 循环创建100张用户表
    WHILE i < 100
        DO
            -- 根据循环次数生成表名
            IF i < 10 THEN
                SET table_name = CONCAT('t_user_0', i);
            ELSE
                SET table_name = CONCAT('t_user_', i);
            END IF;

            -- 拼接创建表的SQL语句
            SET sql_text = CONCAT('CREATE TABLE ', table_name, table_body);
            -- 输出SQL语句
            SELECT sql_text;
            -- 将SQL语句赋值给变量@sql_text
            SET @sql_text = sql_text;
            -- 准备执行SQL语句
            PREPARE stmt FROM @sql_text;
            -- 执行SQL语句
            EXECUTE stmt;
            -- 释放准备的SQL语句
            DEALLOCATE PREPARE stmt;
            -- 自增循环变量
            SET i = i + 1;
        END WHILE;


END$$

-- 恢复默认的结束符
DELIMITER ;

call qiyu_live_user.create_t_user_100();



# select * from t_user where user_id = 1001;
# ShardingJdbc -> 路由
# 直接路由
#
# select * from t_user_01 where user_id = 1001;


