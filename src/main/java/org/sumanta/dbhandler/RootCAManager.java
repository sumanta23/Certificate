package org.sumanta.dbhandler;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.sumanta.bean.RootCA;

public class RootCAManager {

  private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

  public void addRootCA(RootCA rootca) {
    Session session = factory.openSession();
    Transaction tx = null;
    try {
      tx = session.beginTransaction();
      session.save(rootca);
      tx.commit();
    } catch (HibernateException e) {
      if (tx != null)
        tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }

  }

  public void removeRootCA(RootCA rootca) {
    Session session = factory.openSession();
    Transaction tx = null;
    try {
      tx = session.beginTransaction();
      session.delete(rootca);
      tx.commit();
    } catch (HibernateException e) {
      if (tx != null)
        tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }

  }

  public void updateRootCA(RootCA rootca, int id) {
    Session session = factory.openSession();
    Transaction tx = null;
    try {
      tx = session.beginTransaction();
      RootCA temp = (RootCA) session.get(RootCA.class, id);
      // temp.setFname(rootca.getFname());
      // temp.setLname(rootca.getLname());
      // temp.setMarks(rootca.getMarks());
      session.update(temp);
      tx.commit();
    } catch (HibernateException e) {
      if (tx != null)
        tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }
  }

  public List viewRootCAs(String condition) {
    Session session = factory.openSession();
    Transaction tx = null;
    List result = new ArrayList<RootCA>();
    try {
      tx = session.beginTransaction();
      // Query query =
      // session.createSQLQuery("select ID,FirstName,LastName,Marks from student_details");
      // System.out.println(Student.class.getSimpleName());
      Query query = session.createQuery("from org.sumanta.bean.RootCA " + condition);
      result = query.list();
      tx.commit();
    } catch (HibernateException e) {
      if (tx != null)
        tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }

    return result;

  }

  public RootCA singleRootCADetails(String id) {
    Session session = factory.openSession();
    Transaction tx = null;
    RootCA st = new RootCA();
    try {
      tx = session.beginTransaction();
      RootCA temp = (RootCA) session.get(RootCA.class, Integer.parseInt(id));
      st = temp;
      tx.commit();
    } catch (HibernateException e) {
      if (tx != null)
        tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }
    return st;

  }

}
