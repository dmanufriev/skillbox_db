package skillbox.code.Report;

import org.hibernate.Session;
import skillbox.code.utils.HibernateUtil;
import skillbox.code.utils.ReportUtil;

import java.util.List;

public class Top5employees {
    private static String HOURS_COLUMN_NAME = "total_hours";
    private static final String EMPLOYEE_COLUMN_NAME = "name";
    public static void report() {
        final int[] columnsWidth = { 11, 20 };
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

            String headerSeparator = ReportUtil.getSeparatorTemplate(columnsWidth, columnsWidth.length);
            String tableTemplate = ReportUtil.getTableTemplate(columnsWidth, columnsWidth.length);

            System.out.print(headerSeparator);
            System.out.printf(tableTemplate, HOURS_COLUMN_NAME, EMPLOYEE_COLUMN_NAME);
            System.out.print(headerSeparator);
            report.forEach((objects -> {
                System.out.printf(tableTemplate, (Long) objects[0], (String) objects[1]);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
