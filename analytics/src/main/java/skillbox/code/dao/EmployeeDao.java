package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Position;
import skillbox.code.utils.HibernateUtil;

import java.util.List;

public class EmployeeDao {
    public void saveEmployee(Employee employee) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();
            if (employee.getId() == null) {
                session.save(employee);
            } else {
                session.update(employee);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Employee> getEmployees() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Employee", Employee.class).list();
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Employee getEmployee(String employeeName) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Employee employee = session.createQuery("from Employee where name = :emp_name", Employee.class)
                                            .setParameter("emp_name", employeeName).uniqueResult();
            return employee;
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
