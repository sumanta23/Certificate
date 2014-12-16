package org.sumanta.dbhandler;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.sumanta.bean.CA;

public class CAManager {

	private static SessionFactory factory = new Configuration().configure()
			.buildSessionFactory();

	public void addCA(CA ca) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(ca);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	public void removeCA(CA ca) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(ca);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	public void updateCA(CA ca, int id) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			CA temp = (CA) session.get(CA.class, id);
			temp.setDn(ca.getDn());
			temp.setIssuer(ca.getIssuer());
			temp.setCertificate(ca.getCertificate());
			temp.setKeypair(ca.getKeypair());
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

	public List viewCAs(String condition) {
		Session session = factory.openSession();
		Transaction tx = null;
		List result = new ArrayList<CA>();
		try {
			tx = session.beginTransaction();
			// Query query =
			// session.createSQLQuery("select ID,FirstName,LastName,Marks from student_details");
			// System.out.println(Student.class.getSimpleName());
			Query query = session.createQuery("from org.sumanta.bean.CA "
					+ condition);
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

	public CA singleCADetails(String id) {
		Session session = factory.openSession();
		Transaction tx = null;
		CA st = new CA();
		try {
			tx = session.beginTransaction();
			CA temp = (CA) session.get(CA.class, Integer.parseInt(id));
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
