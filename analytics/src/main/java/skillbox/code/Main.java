package skillbox.code;

import skillbox.code.Report.Top5costTasks;
import skillbox.code.Report.Top5employees;
import skillbox.code.Report.Top5longTasks;

import java.io.FileNotFoundException;
import java.text.ParseException;

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
3. В схеме описания данных (schema.sql) укажите ограничения для рейта должностей с помощью CHECK.
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
                // TODO Add list functionality
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

    public static void printTimesheet(String employeeName) {

    }

    public static void removeTimesheet(Integer timesheetId) {

    }
}