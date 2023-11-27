package skillbox.code;

import skillbox.code.dao.EmployeeDao;
import skillbox.code.dao.PositionDao;
import skillbox.code.dao.TaskDao;
import skillbox.code.dao.TimesheetDao;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Position;
import skillbox.code.entity.Task;
import skillbox.code.entity.Timesheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ImportData {
    private static final String DATA_PATH = "data";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

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
                newPositions.add(new Position(title, hourSalary));
            }
        }
        positionDao.savePositions(newPositions);
        scanner.close();

        System.out.println("Imported " + (positionsNum - incorrectPositionsNum) + " positions");
        if(incorrectPositionsNum > 0) {
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
                        emp.setPositionId(pos.getId());
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

        importTasks(filename);

        EmployeeDao employeeDao = new EmployeeDao();
        List<Employee> employees = employeeDao.getEmployees();

        TaskDao taskDao = new TaskDao();
        List<Task> tasks = taskDao.getTasks();

        TimesheetDao timesheetDao = new TimesheetDao();

        Scanner scanner = new Scanner(new File(DATA_PATH + File.separatorChar + filename));
        int linesNum = 0;
        while (scanner.hasNextLine() && (linesNum++ < 10)) {
            String line = scanner.nextLine();
            Scanner s = new Scanner(line).useDelimiter(",");
            String currTask = s.next();
            String currEmployee = s.next();
            LocalDateTime startTask = LocalDateTime.parse(s.next(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTask = LocalDateTime.parse(s.next(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Timesheet timesheet = new Timesheet();
            for (var employee : employees) {
                if(employee.getName().equals(currEmployee)) {
                    timesheet.setEmployeeId(employee.getId());
                    break;
                }
            }
            for (var task : tasks) {
                if(task.getTitle().equals(currTask)) {
                    timesheet.setTaskId(task.getId());
                    break;
                }
            }
            timesheet.setStartTime(startTask);
            timesheet.setEndTime(endTask);
            timesheetDao.saveTimesheet(timesheet);
        }
        scanner.close();
    }

    private static void importTasks(String filename) throws FileNotFoundException, ParseException {

        TaskDao taskDao = new TaskDao();
        List<Task> tasks = taskDao.getTasks();
        Set<String> titles = new HashSet<>();

        Scanner scanner = new Scanner(new File(DATA_PATH + File.separatorChar + filename));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner s = new Scanner(line).useDelimiter(",");
            titles.add(s.next());
        }
        scanner.close();

        titles.forEach((title) -> {
            boolean isExist = false;
            for (var task : tasks) {
                if (task.getTitle().equals(title)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                Task newTask = new Task();
                newTask.setTitle(title);
                taskDao.saveTask(newTask);
            }
        });
    }
}