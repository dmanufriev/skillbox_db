package skillbox.code.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skillbox.code.entity.Position;
import skillbox.code.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class PositionDao {
    public void savePosition(Position position) {
        List<Position> positions = new ArrayList<>();
        positions.add(position);
        savePositions(positions);
    }

    public void savePositions(List<Position> positions) {

        if (positions.isEmpty()) {
            return;
        }

        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();
            for (var position : positions) {
                session.save(position);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
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
