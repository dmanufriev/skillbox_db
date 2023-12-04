package skillbox.code;

import skillbox.code.Report.Top5costTasks;
import skillbox.code.Report.Top5employees;
import skillbox.code.Report.Top5longTasks;
import skillbox.code.dao.*;
import skillbox.code.entity.*;
import skillbox.code.utils.ReportUtil;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;

/*
TODO list:
1. Add checkstyle
2. Реализовать вызов из консоли java -jar program.jar import positions.csv
    - program import positions.csv
    - program import employees.csv
    - program import timesheet.csv
    - program list employee
    - program get [employeeName]
    - program remove [employeeName]
    - program report top5longTasks
    - program report top5costTasks
    - program report top5employees
    Возможные варианты запуска из консоли:
    make run ARG0=import ARG1=positions.csv
    ./build/install/analytics/bin/analytics import positions.csv
3. Можно ли создавать базу данных вручную через консоль. Или обязательно нужно проверять программно
    при запуске? И, при необходимости, создавать.
4. Использовать commit в случае успешного импорта один раз для обеспечения целостности данных
5. Проверьте, что все данные в БД импортированы корректно. Сравните количество записей в CSV-файлах и в БД
6. Заблокировать вывод в консоль отладочной информации от Hibernate
7. При импорте данных необходимо использовать стратегию Multi Insert с игнорированием тех данных, которые
   уже есть в БД. Вставлять можно пачками, например по 10 строк, в рамках одного коммита
   INSERT IGNORE INTO subscribe(email, is_active) VALUES ('user4@example.net', 1), ('user2@example.net', 0);
*/

/*
Требования:
 1. Целостность БД. Время старта должно быть меньше времени окончания. Реализовать в БД?
   В случае обнаружения некорректной записи программа должна игнорировать её и выводить предупреждение в терминал
2. Целостность БД. Один сотрудник может работать в один период времени только над одной задачей.
   В случае обнаружения некорректной записи программа должна игнорировать её и выводить предупреждение в терминал
   Т.е. проверяем пересечение дат текущего таймшита со всеми таймшитами конкретного сотрудника.
   Для обеспечения целостности временных рядов таймшитов напишите триггер в
    schema.sql BEFORE INSERT/UPDATE, который будет проверять, не пересекаются
    ли таймшиты сотрудника (сотрудник может работать только над одной задачей в
    каждый момент времени). Также задачу можно решить средствами транзакций
    Hibernate. Для этого внутри транзакции сохранения объекта таймшита проверьте,
    не входят ли существующие задачи для этого сотрудника в тот же диапазон.
3. В схеме описания данных (schema.sql) укажите ограничения для рейта должностей с помощью CHECK. Какие это ограничения?
 */

// Проверка
// SELECT title, tsh.task_id, tsh.employee_id, p.title, HOUR(TIMEDIFF(tsh.end_time, tsh.start_time)) as spent_hours, p.hour_salary FROM timesheet tsh LEFT JOIN tasks t ON tsh.task_id = t.task_id LEFT JOIN employees e ON tsh.employee_id = e.employee_id LEFT JOIN positions p ON e.position_id = p.position_id WHERE t.title = 'BILLING-970';
// Сводная таблица в человеческом виде
//   select tsh.timesheet_id, t.title, e.name, p.title, p.hour_salary, tsh.start_time, tsh.end_time FROM timesheet tsh JOIN tasks t ON tsh.task_id = t.task_id JOIN employees e ON tsh.employee_id = e.employee_id JOIN positions p ON e.position_id = p.position_id ORDER BY tsh.timesheet_id LIMIT 10;


/* Полезные ссылки:
Ссылка на настройку аннотаций: https://javarush.com/quests/lectures/questhibernate.level09.lecture01
Как работать с ID: https://habr.com/ru/companies/haulmont/articles/653843/ В статье выше есть видео обзор
Параметры LIMIT, OFFSET и сортировка задаются при помощи отдельных параметров https://javarush.com/quests/lectures/questhibernate.level10.lecture04
 */

public class Main {
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        if (args.length < 2) {
            System.err.println("Provide parameters: [action] [object] " + args.length);
            System.exit(1);
        }
        switch (args[0]) {
            case "import":
                System.out.println("Importing file " + args[1]);
                switch (args[1]) {
                    case "positions.csv":
                        ImportData.importPositions(args[1]);
                        break;
                    case "employees.csv":
                        ImportData.importEmployees(args[1]);
                        break;
                    case "timesheet.csv":
                        ImportData.importTimesheet(args[1]);
                        break;
                }
                break;
            case "list":
                System.out.println("Employees list:");
                printEmployees();
                break;
            case "get":
                System.out.println("Timesheet for employee " + args[1]);
                printTimesheet(args[1]);
                break;
            case "remove":
                System.out.println("Removing timesheet with id " + args[1]);
                removeTimesheet(Integer.valueOf(args[1]));
                break;
            case "report":
                System.out.println("Report " + args[1]);
                switch (args[1]) {
                    case "top5longTasks":
                        Top5longTasks.report();
                        break;
                    case "top5costTasks":
                        Top5costTasks.report();
                        break;
                    case "top5employees":
                        Top5employees.report();
                        break;
                }
                break;
        }
        System.exit(0);
    }

    public static void printEmployees() {
        final int[] columnsWidth = { 15, 30 };

        EmployeeDao employeeDao = new EmployeeDao();
        List<Employee> employees = employeeDao.getEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found");
            return;
        }

        String headerSeparator = ReportUtil.getSeparatorTemplate(columnsWidth, columnsWidth.length);
        String tableTemplate = ReportUtil.getTableTemplate(columnsWidth, columnsWidth.length);

        System.out.print(headerSeparator);
        System.out.printf(tableTemplate, "name", "position");
        System.out.print(headerSeparator);
        for (var e : employees) {
            System.out.printf(tableTemplate, e.getName(), e.getPosition().getTitle());
        }
    }

    public static void printTimesheet(String employeeName) {
        final int[] columnsWidth = { 5, 12, 12, 25, 25 };

        EmployeeDao employeeDao = new EmployeeDao();
        Employee employee = employeeDao.getEmployee(employeeName);
        if (employee == null) {
            System.out.println("Employee " + employeeName + " isn't found");
            return;
        }

        TimesheetDao timesheetDao = new TimesheetDao();
        List<Timesheet> timesheets = timesheetDao.getTimesheet(employee.getId());
        if (timesheets.isEmpty()) {
            System.out.println("Timesheets for " + employeeName + " aren't found");
            return;
        }

        String headerSeparator = ReportUtil.getSeparatorTemplate(columnsWidth, columnsWidth.length);
        String tableTemplate = ReportUtil.getTableTemplate(columnsWidth, columnsWidth.length);

        System.out.print(headerSeparator);
        System.out.printf(tableTemplate, "id", "employee_id", "task_id", "start_time", "end_time");
        System.out.print(headerSeparator);
        for (var t : timesheets) {
            System.out.printf(tableTemplate, t.getId(), t.getEmployee().getId(), t.getTask().getId(),
                                t.getStartTime(), t.getEndTime());
        }
    }

    public static void removeTimesheet(Integer timesheetId) {
        TimesheetDao timesheetDao = new TimesheetDao();
        Timesheet timesheet = timesheetDao.removeTimesheet(timesheetId);
        if (timesheet != null) {
            System.out.println("Timesheet id " + timesheet.getId() + " removed");
        }
    }
}