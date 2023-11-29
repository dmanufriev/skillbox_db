package skillbox.code.Report;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import skillbox.code.entity.Timesheet;
import skillbox.code.utils.HibernateUtil;

import java.util.List;
import java.util.Set;

// TODO Параметры LIMIT, OFFSET и сортировка задаются при помощи отдельных параметров
// https://javarush.com/quests/lectures/questhibernate.level10.lecture04
// TODO Заголовок колонок необходимо получать из запроса при помощи query.getParameterMetadata()
// TODO Автоматизировать расчет ширины столбцов при выводе на печать при помощи анализа самого длинного слова в столбце

public class Top5longTasks {
    public static void report() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            Query<Object[]> query = session.createNativeQuery(
                    "SELECT SUM(HOUR(TIMEDIFF(end_time, start_time)) * p.hour_salary) as total_cost, " +
                            "t.title FROM timesheet tsh " +
                            "LEFT JOIN tasks t ON tsh.task_id = t.task_id " +
                            "LEFT JOIN employees e ON tsh.employee_id = e.employee_id " +
                            "LEFT JOIN positions p ON e.position_id = p.position_id " +
                            "GROUP BY t.title ORDER BY total_cost DESC LIMIT 5");
            ((NativeQuery<Object[]>) query).addScalar("total_cost", StandardBasicTypes.LONG);
            ((NativeQuery<Object[]>) query).addScalar("title", StandardBasicTypes.STRING);
            List<Object[]> tasks = query.list();

            if (!tasks.isEmpty()) {
                System.out.println("+-------------+-------------+");
                System.out.println("| spent_hours |  title      |");
                System.out.println("+-------------+-------------+");
                tasks.forEach((objects -> {
                    System.out.println("| " + (Long) objects[0] + " | " + (String) objects[1] + " | ");
                }));
            } else {
                System.out.println("No data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public static void reportHql() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            String hql = "select sum(hour(timediff(end_time, start_time)) * p.hour";

            Query<Object[]> query = session.createNativeQuery(
                    "SELECT SUM(HOUR(TIMEDIFF(end_time, start_time)) * p.hour_salary) as total_cost, " +
                            "t.title FROM timesheet tsh " +
                            "LEFT JOIN tasks t ON tsh.task_id = t.task_id " +
                            "LEFT JOIN employees e ON tsh.employee_id = e.employee_id " +
                            "LEFT JOIN positions p ON e.position_id = p.position_id " +
                            "GROUP BY t.title ORDER BY total_cost DESC LIMIT 5");
            ((NativeQuery<Object[]>) query).addScalar("total_cost", StandardBasicTypes.LONG);
            ((NativeQuery<Object[]>) query).addScalar("title", StandardBasicTypes.STRING);
            List<Object[]> tasks = query.list();

            if (!tasks.isEmpty()) {
                System.out.println("+-------------+-------------+");
                System.out.println("| spent_hours |  title      |");
                System.out.println("+-------------+-------------+");
                tasks.forEach((objects -> {
                    System.out.println("| " + (Long) objects[0] + " | " + (String) objects[1] + " | ");
                }));
            } else {
                System.out.println("No data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
// SELECT SUM(HOUR(TIMEDIFF(end_time, start_time)) * p.hour_salary) as total_cost, t.title FROM timesheet tsh LEFT JOIN tasks t ON tsh.task_id = t.task_id LEFT JOIN employees e ON tsh.employee_id = e.employee_id LEFT JOIN positions p ON e.position_id = p.position_id GROUP BY t.title ORDER BY total_cost DESC LIMIT 5;

// Проверка
// SELECT title, tsh.task_id, tsh.employee_id, p.title, HOUR(TIMEDIFF(tsh.end_time, tsh.start_time)) as spent_hours, p.hour_salary FROM timesheet tsh LEFT JOIN tasks t ON tsh.task_id = t.task_id LEFT JOIN employees e ON tsh.employee_id = e.employee_id LEFT JOIN positions p ON e.position_id = p.position_id WHERE t.title = 'BILLING-970';
