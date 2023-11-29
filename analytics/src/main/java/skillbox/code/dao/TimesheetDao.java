package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Timesheet;
import skillbox.code.entity.Task;
import skillbox.code.utils.HibernateUtil;

import javax.persistence.Query;
import java.util.List;

public class TimesheetDao {

    // 1. Проверяем время начало и конца, если не релевантно - выходим
    // 2. Получаем все записи по текущему пользователю. Если есть - проверяем пересечение
    // 3. Если все ок - сохраняем в БД
    public void saveTimesheet(Timesheet timesheet) {

        if (timesheet.getStartTime().isAfter(timesheet.getEndTime())) {
            System.out.println("Save timesheet for task " + timesheet.getTaskId() + " FAILED");
            System.out.println("Incorrect time zones: " + timesheet.getStartTime() + " / " + timesheet.getEndTime());
            return;
        }

        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            // Проверка пересечения данной задачи с другими у текущего работника
            List<Timesheet> existingTimesheets = getTimesheet(timesheet.getEmployeeId());
            for (var t : existingTimesheets) {
                if ((timesheet.getStartTime().isAfter(t.getStartTime()) &&
                        timesheet.getStartTime().isBefore(t.getEndTime())) ||
                    (timesheet.getEndTime().isAfter(t.getStartTime()) &&
                        timesheet.getEndTime().isBefore(t.getEndTime()))) {
                    System.out.println("Save timesheet for task " + timesheet.getTaskId() + " FAILED");
                    System.out.println("Intersection with existing task " + t.getTaskId());

                    return;
                }
            }

            transaction = session.beginTransaction();

            // TODO В рамках транзакции необходимо именно здесь сохранять task, и в случае успеха
            // добавлять timesheet в БД, предварительно получив id

            session.save(timesheet);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // TODO Можно ли удалить задачу средствами самой БД? Пока у меня не получилось
    public Timesheet removeTimesheet(Integer id) {

        Transaction transaction = null;

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();

            // Проверяем, что timesheet существует
            List<Timesheet> list = session.createQuery("from Timesheet where timesheet_id = :id")
                    .setParameter("id", id).list();
            if (list.isEmpty()) {
                System.out.println("Timesheet " + id + " isn't found");
                transaction.commit();
                return null;
            }

            Timesheet timesheet = list.get(0);
            session.delete(timesheet);

            // TODO Удаляем задачу из БД, если больше нет связанных timesheets
            if (list.size() == 1) {
                List<Task> listTasks = session.createQuery("from Task where task_id = :id")
                        .setParameter("id", timesheet.getTaskId()).list();
                if (listTasks.isEmpty()) {
                    System.out.println("Task " + timesheet.getTaskId() + " isn't found");
                    transaction.commit();
                    return null;
                }

                Task task = listTasks.get(0);
                session.delete(task);
            }

            // TODO Здесь необходимо добавить таймшит в лог

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return null;
    }

    public List<Timesheet> getTimesheet(int employeeId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Timesheet where employee_id = :emp_id")
                    .setParameter("emp_id", employeeId).list();
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
