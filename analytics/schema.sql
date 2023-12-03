DROP DATABASE IF EXISTS `analytics`;
CREATE DATABASE `analytics`;

CREATE TABLE `analytics`.`positions` (
    `position_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    `hour_salary` INT UNSIGNED NOT NULL,
    PRIMARY KEY (`position_id`),
    UNIQUE KEY `title` (`title`),
    CONSTRAINT `rate_limit` CHECK (`hour_salary` <= 100)
);

CREATE TABLE `analytics`.`tasks` (
    `task_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`task_id`),
    UNIQUE KEY `title` (`title`)
);

CREATE TABLE `analytics`.`employees` (
    `employee_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `position_id` INT UNSIGNED NOT NULL,
    PRIMARY KEY (`employee_id`),
    UNIQUE KEY `name` (`name`),
    CONSTRAINT `fk_pos` FOREIGN KEY (`position_id`) REFERENCES `positions` (`position_id`) ON DELETE RESTRICT
);

CREATE TABLE `analytics`.`timesheet` (
    `timesheet_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `employee_id` INT UNSIGNED NOT NULL,
    `task_id` INT UNSIGNED NOT NULL,
    `start_time` TIMESTAMP,
    `end_time` TIMESTAMP,
    PRIMARY KEY (`timesheet_id`),
    CONSTRAINT `fk_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_task` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`task_id`) ON DELETE RESTRICT,
    CONSTRAINT `time_check` CHECK ((`start_time` < `end_time`))
);

CREATE TABLE `analytics`.`timesheet_history` (
    `timesheet_id` INT UNSIGNED NOT NULL,
    `employee_id` INT UNSIGNED NOT NULL,
    `task_title` VARCHAR(100) NOT NULL,
    `start_time` TIMESTAMP,
    `end_time` TIMESTAMP,
    PRIMARY KEY (`timesheet_id`)
);

CREATE TRIGGER timesheet_log BEFORE DELETE ON timesheet FOR EACH ROW
    INSERT INTO timesheet_history SET
    timesheet_id = OLD.timesheet_id,
    employee_id = OLD.employee_id,
    task_title = (SELECT t.title FROM tasks t WHERE t.task_id = OLD.task_id),
    start_time = OLD.start_time,
    end_time = OLD.end_time;
