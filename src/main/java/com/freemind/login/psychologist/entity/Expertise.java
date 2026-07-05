package com.freemind.login.psychologist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "expertise")
public class Expertise implements java.io.Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "expertise_id")
	private Integer expertiseId;
	
	@Column(name = "expertise_name" , nullable = false)
	private String expertiseName;
	
}
