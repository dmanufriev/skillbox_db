package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Timesheet;
import skillbox.code.utils.HibernateUtil;

import java.util.List;

public class TimesheetDao {
    public void saveTimesheet(Timesheet timesheet) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();
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

    public List<Timesheet> getTimesheet(Employee employee) {
        return null;
    }
}
