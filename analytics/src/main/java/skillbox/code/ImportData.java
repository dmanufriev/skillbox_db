package skillbox.code;

import skillbox.code.dao.EmployeeDao;
import skillbox.code.dao.PositionDao;
import skillbox.code.dao.TimesheetDao;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Position;
import skillbox.code.entity.Task;
import skillbox.code.entity.Timesheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ImportData {
    private static final String DATA_PATH = "data";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static void importPositions(String filename) throws FileNotFoundException {

        PositionDao positionDao = new PositionDao();
        List<Position> positions = positionDao.getPositions();
        List<Position> newPositions = new ArrayList<>();

        Scanner scanner = new Scanner(new File(DATA_PATH + File.separatorChar + filename));
        int positionsNum = 0, incorrectPositionsNum = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner s = new Scanner(line).useDelimiter(",");
            String title = s.next();
            int hourSalary = s.nextInt();
            positionsNum++;
            boolean isExist = false;
            for (var pos : positions) {
                if (pos.getTitle().equals(title)) {
                    isExist = true;
                    incorrectPositionsNum++;
                    break;
                }
            }
            if (!isExist) {
                Position position = new Position();
                position.setTitle(title);
                position.setHourSalary(hourSalary);
                newPositions.add(position);
            }
        }
        scanner.close();
        positionDao.savePositions(newPositions);

        System.out.println("Imported " + (positionsNum - incorrectPositionsNum) + " positions");
        if (incorrectPositionsNum > 0) {
            System.out.println("Incorrect: " + incorrectPositionsNum);
        }
    }

    public static void importEmployees(String filename) throws FileNotFoundException {

        EmployeeDao employeeDao = new EmployeeDao();
        PositionDao positionDao = new PositionDao();
        List<Employee> employees = employeeDao.getEmployees();
        List<Position> positions = positionDao.getPositions();

        Scanner scanner = new Scanner(new File(DATA_PATH + File.separatorChar + filename));
        int employeesNum = 0, incorrectEmployeesNum = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner s = new Scanner(line).useDelimiter(",");
            String name = s.next();
            String position = s.next();
            employeesNum++;
            boolean isExist = false;
            for (var emp : employees) {
                if (emp.getName().equals(name)) {
                    isExist = true;
                    incorrectEmployeesNum++;
                    break;
                }
            }
            if (!isExist) {
                Employee emp = new Employee();
                emp.setName(name);
                for (var pos : positions) {
                    if (pos.getTitle().equals(position)) {
                        emp.setPosition(pos);
                        employeeDao.saveEmployee(emp);
                        break;
                    }
                }
            }
        }
        scanner.close();

        System.out.println("Imported " + (employeesNum - incorrectEmployeesNum) + " employees");
        if(incorrectEmployeesNum > 0) {
            System.out.println("Incorrect: " + incorrectEmployeesNum);
        }
    }

    public static void importTimesheet(String filename) throws FileNotFoundException, ParseException {

        EmployeeDao employeeDao = new EmployeeDao();
        List<Employee> employees = employeeDao.getEmployees();
        TimesheetDao timesheetDao = new TimesheetDao();

        Scanner scanner = new Scanner(new File(DATA_PATH + File.separatorChar + filename));
        int timesheetsNum = 0, incorrectTimesheetsNum = 0;

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            Scanner s = new Scanner(line).useDelimiter(",");
            String currTaskTitle = s.next();
            String currEmployeeName = s.next();
            LocalDateTime startTask = LocalDateTime.parse(s.next(), formatter);
            LocalDateTime endTask = LocalDateTime.parse(s.next(), formatter);
            timesheetsNum++;

            Timesheet timesheet = new Timesheet();
            timesheet.setStartTime(startTask);
            timesheet.setEndTime(endTask);

            for (var employee : employees) {
                if(employee.getName().equals(currEmployeeName)) {
                    timesheet.setEmployee(employee);
                    break;
                }
            }

            Task task = new Task();
            task.setTitle(currTaskTitle);
            timesheet.setTask(task);

            if (timesheetDao.saveTimesheet(timesheet) < 0) {
                incorrectTimesheetsNum++;
            }
        }
        scanner.close();

        System.out.println("Imported " + (timesheetsNum - incorrectTimesheetsNum) + " timesheets");
        if(incorrectTimesheetsNum > 0) {
            System.out.println("Incorrect: " + incorrectTimesheetsNum);
        }
    }
}