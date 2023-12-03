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

        // Проверка корректности временных интервалов
        if (timesheet.getStartTime().isAfter(timesheet.getEndTime())) {
            System.out.println("Save timesheet for task " + timesheet.getTask().getTitle() + " FAILED");
            System.out.println("Incorrect time zones: " + timesheet.getStartTime() + " / " + timesheet.getEndTime());
            return;
        }

        Transaction transaction = null;
        try {
            // Проверка пересечения данной задачи с другими у текущего работника
            List<Timesheet> existingTimesheets = getTimesheet(timesheet.getEmployee().getId());
            for (var t : existingTimesheets) {
                if ((timesheet.getStartTime().isAfter(t.getStartTime()) &&
                        timesheet.getStartTime().isBefore(t.getEndTime())) ||
                    (timesheet.getEndTime().isAfter(t.getStartTime()) &&
                        timesheet.getEndTime().isBefore(t.getEndTime()))) {
                    System.out.println("Save timesheet for task " + timesheet.getTask().getTitle() + " FAILED");
                    System.out.println("Intersection with existing task " + t.getTask().getTitle());

                    return;
                }
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // TODO В рамках транзакции необходимо именно здесь сохранять task, и в случае успеха
            // добавлять timesheet в БД, предварительно получив id

            if (timesheet.getId() == null) {
                session.save(timesheet);
            } else {
                session.update(timesheet);
            }

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
        Timesheet timesheet = null;

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();

            // Проверяем, что timesheet существует
            timesheet = (Timesheet) session.createQuery("from Timesheet where timesheet_id = :id")
                    .setParameter("id", id).uniqueResult();
            if (timesheet == null) {
                System.out.println("Timesheet " + id + " isn't found");
                transaction.commit();
                return null;
            }
            session.delete(timesheet);

            // TODO Здесь можно написать сразу where Task = task
            List<Timesheet> timesheetsWithTask= session.createQuery("from Timesheet where task_id = :id")
                    .setParameter("id", timesheet.getTask().getId()).list();
            if (timesheetsWithTask.isEmpty()) {
                session.delete(timesheet.getTask());
            }

            // TODO Здесь необходимо добавить таймшит в лог

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
