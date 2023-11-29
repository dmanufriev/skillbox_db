package skillbox.code.Report;

public class Top5employees {
    public static void report() {
        // SELECT SUM(HOUR(TIMEDIFF(tsh.end_time, tsh.start_time))) as total_hours, e.name FROM timesheet tsh LEFT JOIN employees e ON tsh.employee_id = e.employee_id GROUP BY e.employee_id ORDER BY total_hours DESC LIMIT 10;
    };
}
