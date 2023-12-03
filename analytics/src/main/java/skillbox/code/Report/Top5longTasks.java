package skillbox.code.Report;

import org.hibernate.Session;
import skillbox.code.utils.HibernateUtil;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Top5longTasks {
    private static String HOURS_COLUMN_NAME = "spent_hours";
    private static final int HOURS_COLUMN_WIDTH = 11;
    private static final String TITLE_COLUMN_NAME = "title";
    private static final int TITLE_COLUMN_WIDTH = 20;

    public static void report() {
        // SELECT SUM(HOUR(TIMEDIFF(end_time, start_time))) as spent_hours, t.title FROM timesheet tsh
        // JOIN tasks t ON tsh.task_id = t.task_id
        // JOIN employees e ON tsh.employee_id = e.employee_id
        // JOIN positions p ON e.position_id = p.position_id
        // GROUP BY t.title
        // ORDER BY spent_hours DESC, title
        // LIMIT 5;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            String hql = "select sum(hour(timediff(endTime, startTime))) as spent_hours," +
                        " task.title from Timesheet group by task.title ORDER BY spent_hours desc, title asc";

            List<Object[]> report = session.createQuery(hql, Object[].class).setMaxResults(5).list();

            if (report.isEmpty()) {
                System.out.println("No data");
                return;
            }

            StringBuilder builder = new StringBuilder("+");
            builder.append(StringUtils.repeat("-", HOURS_COLUMN_WIDTH + 2));
            builder.append("+");
            builder.append(StringUtils.repeat("-", TITLE_COLUMN_WIDTH + 2));
            builder.append("+");
            String headerSeparator = builder.toString();

            System.out.println(headerSeparator);
            System.out.printf("| %" + HOURS_COLUMN_WIDTH + "s | %-" + TITLE_COLUMN_WIDTH + "s |\n",
                    HOURS_COLUMN_NAME, TITLE_COLUMN_NAME);
            System.out.println(headerSeparator);
            report.forEach((objects -> {
                System.out.printf("| %" + HOURS_COLUMN_WIDTH + "s | %-" + TITLE_COLUMN_WIDTH + "s |\n",
                        (Long) objects[0], (String) objects[1]);
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
