package skillbox.code.Report;

public class Top5costTasks {
    public static void report() {
        //SELECT tsh.employee_id, HOUR(TIMEDIFF(end_time, start_time)) AS spent_hours, t.title AS title, e.name, p.title, p.hour_salary FROM timesheet tsh LEFT JOIN tasks t ON tsh.task_id = t.task_id LEFT JOIN employees
    };
}
