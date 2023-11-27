package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Task;
import skillbox.code.utils.HibernateUtil;

import java.util.List;

public class TaskDao {
    public void saveTask(Task task) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();
            session.save(task);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void saveTasks(List<Task> tasks) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();
            for (var task : tasks) {
                session.save(task);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Task> getTasks() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Task", Task.class).list();
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Task getTask(String title) {
        return null;
    }
}
