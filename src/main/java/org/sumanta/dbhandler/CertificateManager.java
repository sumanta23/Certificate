package org.sumanta.dbhandler;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.sumanta.bean.Certificate;

public class CertificateManager {

	private static SessionFactory factory = new Configuration().configure()
			.buildSessionFactory();

	public void addCertificate(Certificate ca) {
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

	public void removeCertificate(Certificate ca) {
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

	public void updateCertificate(Certificate ca, int id) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Certificate temp = (Certificate) session.get(Certificate.class, id);
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

	public List viewCertificates(String qry) {
		Session session = factory.openSession();
		Transaction tx = null;
		List result = new ArrayList<Certificate>();
		try {
			tx = session.beginTransaction();
			// Query query =
			// session.createSQLQuery("select ID,FirstName,LastName,Marks from student_details");
			// System.out.println(Student.class.getSimpleName());
			Query query = session
					.createQuery("from org.sumanta.bean.Certificate "+ qry);
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

	public Certificate singleCertificateDetails(String id) {
		Session session = factory.openSession();
		Transaction tx = null;
		Certificate st = new Certificate();
		try {
			tx = session.beginTransaction();
			Certificate temp = (Certificate) session.get(Certificate.class,
					Integer.parseInt(id));
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
