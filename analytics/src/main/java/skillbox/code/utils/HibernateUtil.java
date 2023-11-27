package skillbox.code.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import skillbox.code.entity.Employee;
import skillbox.code.entity.Position;
import skillbox.code.entity.Task;
import skillbox.code.entity.Timesheet;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/analytics?serverTimezone=Etc/GMT-3");
                settings.put(Environment.USER, "root");
                settings.put(Environment.PASS, "pass");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");

                settings.put(Environment.SHOW_SQL, "true");

                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                configuration.setProperties(settings);

                configuration.addAnnotatedClass(Position.class);
                configuration.addAnnotatedClass(Employee.class);
                configuration.addAnnotatedClass(Task.class);
                configuration.addAnnotatedClass(Timesheet.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static SessionFactory test() {
        System.out.println("Test");
        return sessionFactory;
    }
}