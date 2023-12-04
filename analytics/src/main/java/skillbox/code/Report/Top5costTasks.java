package skillbox.code.Report;

import org.hibernate.Session;
import skillbox.code.utils.HibernateUtil;
import skillbox.code.utils.ReportUtil;

import java.util.List;

public class Top5costTasks {
    private static String COST_COLUMN_NAME = "total_cost";
    private static final String TITLE_COLUMN_NAME = "title";
    public static void report() {
        final int[] columnsWidth = { 11, 20 };
        // SELECT SUM(HOUR(TIMEDIFF(end_time, start_time)) * p.hour_salary) as total_cost, t.title FROM timesheet tsh
        // JOIN tasks t ON tsh.task_id = t.task_id
        // JOIN employees e ON tsh.employee_id = e.employee_id
        // JOIN positions p ON e.position_id = p.position_id
        // GROUP BY t.title
        // ORDER BY total_cost DESC, title
        // LIMIT 5;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            String hql = "select sum(hour(timediff(endTime, startTime)) * employee.position.hourSalary) as total_cost," +
                    " task.title from Timesheet group by task.title ORDER BY total_cost desc, task.title asc";
            List<Object[]> report = session.createQuery(hql, Object[].class).setMaxResults(5).list();

            if (report.isEmpty()) {
                System.out.println("No data");
                return;
            }

            String headerSeparator = ReportUtil.getSeparatorTemplate(columnsWidth, columnsWidth.length);
            String tableTemplate = ReportUtil.getTableTemplate(columnsWidth, columnsWidth.length);

            System.out.print(headerSeparator);
            System.out.printf(tableTemplate, COST_COLUMN_NAME, TITLE_COLUMN_NAME);
            System.out.print(headerSeparator);
            report.forEach((objects -> {
                System.out.printf(tableTemplate, (Long) objects[0], (String) objects[1]);
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
