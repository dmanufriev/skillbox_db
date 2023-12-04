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
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, ParseException {

        org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger("org.hibernate");
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);

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