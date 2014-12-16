package org.sumanta.bean;

import java.sql.Date;

import javax.persistence.*;

@Entity(name = "CRL")
public class CRL {
	@Id
	@GeneratedValue
	int id;
	@Column(name = "serialno", nullable = false)
	String serialno;
	@Column(name = "revokeddate", nullable = false)
	Date revokeddate;
}
