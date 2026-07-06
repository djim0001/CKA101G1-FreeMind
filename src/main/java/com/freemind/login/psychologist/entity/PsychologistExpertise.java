package com.freemind.login.psychologist.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "psychologist_expertise")
public class PsychologistExpertise {
	
	@ManyToOne
	@MapsId("psychId")
	@JoinColumn(name = "psych_id")
	private Psychologist psychologist;
	
	@ManyToOne
	@MapsId("expsrtiseID")
	@JoinColumn(name = "expertise_id")
	private Expertise expertise;
	
	
	
	public Psychologist getPsychologist() {
		return psychologist;
	}

	public void setPsychologist(Psychologist psychologist) {
		this.psychologist = psychologist;
	}

	public Expertise getExpertise() {
		return expertise;
	}

	public void setExpertise(Expertise expertise) {
		this.expertise = expertise;
	}


	// =====================複合主鍵=====================
	@Embeddable
	public static class CompositeExpertiseDetail implements Serializable{
		
		@Column(name  = "psych_id")
		private Integer psychID;
		
		@Column(name = "expertise_id")
		private Integer expertiseId;

		
		
		public CompositeExpertiseDetail() {
			super();
		}
		
		public CompositeExpertiseDetail(Integer pstchId,Integer expertiseId) {
			super();
			this.expertiseId = expertiseId;
			this.psychID = pstchId;
		}
		
		
		
		public Integer getPsychID() {
			return psychID;
		}

		public void setPsychID(Integer psychID) {
			this.psychID = psychID;
		}

		public Integer getExpertiseId() {
			return expertiseId;
		}

		public void setExpertiseId(Integer expertiseId) {
			this.expertiseId = expertiseId;
		}
		
		
	}
	
	
}
