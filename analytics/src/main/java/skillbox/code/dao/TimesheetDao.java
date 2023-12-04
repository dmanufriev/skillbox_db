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
    public int saveTimesheet(Timesheet timesheet) {

        // Проверка корректности временных интервалов
        if (!timesheet.getStartTime().isBefore(timesheet.getEndTime())) {
            System.out.println("Save timesheet for task " + timesheet.getTask().getTitle() + " FAILED");
            System.out.println(" Incorrect time zones: " + timesheet.getStartTime() + " / " + timesheet.getEndTime());
            return -1;
        }

        Transaction transaction = null;
        try {

            // Проверка пересечения данной задачи с другими у текущего работника
            List<Timesheet> existingTimesheets = getTimesheet(timesheet.getEmployee().getId());
            if (timesheet.hasIntervalsIntersection(existingTimesheets)) {
                System.out.println("Save timesheet for task " + timesheet.getTask().getTitle() + " FAILED");
                System.out.println(" Intersection with existing task");
                return -2;
            }

            // В рамках транзакции необходимо именно здесь сохранять task, и, в случае успеха, добавлять timesheet в БД
            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Task task = session.createQuery("from Task where title = :task_title", Task.class)
                    .setParameter("task_title", timesheet.getTask().getTitle())
                    .uniqueResult();
            if (task == null) {
                session.save(timesheet.getTask());
            } else {
                timesheet.setTask(task);
            }
            session.save(timesheet);
            transaction.commit();
            return 0;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return -10;
        }
    }

    public Timesheet removeTimesheet(Integer id) {

        Transaction transaction = null;
        Timesheet timesheet = null;

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            // Проверяем, что timesheet существует
            timesheet = (Timesheet) session.createQuery("from Timesheet where timesheet_id = :id")
                    .setParameter("id", id).uniqueResult();
            if (timesheet == null) {
                System.out.println("Timesheet " + id + " isn't found");
                return null;
            }

            transaction = session.beginTransaction();
            session.delete(timesheet);
            // Если текущая задача больше не связана с другими таймшитами, удаляем и задачу
            List<Timesheet> timesheetsWithTask= session.createQuery("from Timesheet where task_id = :id")
                    .setParameter("id", timesheet.getTask().getId()).list();
            if (timesheetsWithTask.isEmpty()) {
                session.delete(timesheet.getTask());
            }
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return timesheet;
    }

    public List<Timesheet> getTimesheet(Integer employeeId) {
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
