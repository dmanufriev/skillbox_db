package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Timesheet;
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

            transaction = session.beginTransaction();

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

    public Timesheet removeTimesheet(Integer id) {
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
