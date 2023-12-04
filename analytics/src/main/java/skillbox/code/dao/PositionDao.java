package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Position;
import skillbox.code.utils.HibernateUtil;
import java.util.List;

public class PositionDao {
    public int savePositions(List<Position> positions) {

        if (positions.isEmpty()) {
            return 0;
        }

        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            for (var position : positions) {
                session.save(position);
            }
            transaction.commit();
            return 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return -1;
        }
    }

    public List<Position> getPositions() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Position", Position.class).list();
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
