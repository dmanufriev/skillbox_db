package skillbox.code.Report;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import skillbox.code.utils.HibernateUtil;

import java.util.List;

public class Top5costTasks {
    private static String COST_COLUMN_NAME = "total_cost";
    private static final int COST_COLUMN_WIDTH = 11;
    private static final String TITLE_COLUMN_NAME = "title";
    private static final int TITLE_COLUMN_WIDTH = 20;
    public static void report() {
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

            StringBuilder builder = new StringBuilder("+");
            builder.append(StringUtils.repeat("-", COST_COLUMN_WIDTH + 2));
            builder.append("+");
            builder.append(StringUtils.repeat("-", TITLE_COLUMN_WIDTH + 2));
            builder.append("+");
            String headerSeparator = builder.toString();

            System.out.println(headerSeparator);
            System.out.printf("| %" + COST_COLUMN_WIDTH + "s | %-" + TITLE_COLUMN_WIDTH + "s |\n",
                    COST_COLUMN_NAME, TITLE_COLUMN_NAME);
            System.out.println(headerSeparator);
            report.forEach((objects -> {
                System.out.printf("| %" + COST_COLUMN_WIDTH + "s | %-" + TITLE_COLUMN_WIDTH + "s |\n",
                        (Long) objects[0], (String) objects[1]);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
