package skillbox.code.Report;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import skillbox.code.utils.HibernateUtil;
import java.util.List;

public class Top5employees {
    private static String HOURS_COLUMN_NAME = "total_hours";
    private static final int HOURS_COLUMN_WIDTH = 11;
    private static final String EMPLOYEE_COLUMN_NAME = "name";
    private static final int EMPLOYEE_COLUMN_WIDTH = 20;
    public static void report() {
        // SELECT SUM(HOUR(TIMEDIFF(tsh.end_time, tsh.start_time))) as total_hours, e.name FROM timesheet tsh
        // JOIN employees e ON tsh.employee_id = e.employee_id
        // GROUP BY e.employee_id
        // ORDER BY total_hours DESC, name
        // LIMIT 10;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            String hql = "select sum(hour(timediff(endTime, startTime))) as total_hours," +
                    " employee.name from Timesheet group by employee.id ORDER BY total_hours desc, name asc";
            List<Object[]> report = session.createQuery(hql, Object[].class).setMaxResults(5).list();

            if (report.isEmpty()) {
                System.out.println("No data");
                return;
            }

            StringBuilder builder = new StringBuilder("+");
            builder.append(StringUtils.repeat("-", HOURS_COLUMN_WIDTH + 2));
            builder.append("+");
            builder.append(StringUtils.repeat("-", EMPLOYEE_COLUMN_WIDTH + 2));
            builder.append("+");
            String headerSeparator = builder.toString();

            System.out.println(headerSeparator);
            System.out.printf("| %" + HOURS_COLUMN_WIDTH + "s | %-" + EMPLOYEE_COLUMN_WIDTH + "s |\n",
                    HOURS_COLUMN_NAME, EMPLOYEE_COLUMN_NAME);
            System.out.println(headerSeparator);
            report.forEach((objects -> {
                System.out.printf("| %" + HOURS_COLUMN_WIDTH + "s | %-" + EMPLOYEE_COLUMN_WIDTH + "s |\n",
                        (Long) objects[0], (String) objects[1]);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
