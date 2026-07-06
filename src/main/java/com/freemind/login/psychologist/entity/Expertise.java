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
	
	private static final long serialVersionUID = 1L;
	


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "expertise_id")
	private Integer expertiseId;
	
	@Column(name = "expertise_name" , nullable = false , length = 50)
	private String expertiseName;
	
	
	
	
	
	
	
	
	
	public Integer getExpertiseId() {
		return expertiseId;
	}

	public void setExpertiseId(Integer expertiseId) {
		this.expertiseId = expertiseId;
	}

	public String getExpertiseName() {
		return expertiseName;
	}

	public void setExpertiseName(String expertiseName) {
		this.expertiseName = expertiseName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
	
	
	
	
}
