package com.freemind.login.psychologist.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "psychologist_expertise")
public class PsychologistExpertise {
	
	@EmbeddedId
	private CompositeExpertiseDetail compositeExpertiseDetail;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("psychId")
	@JoinColumn(name = "psych_id")
	private Psychologist psychologist;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("expertiseId")
	@JoinColumn(name = "expertise_id")
	private Expertise expertise;
	
	public CompositeExpertiseDetail getCompositeExpertiseDetail() {
		return compositeExpertiseDetail;
	}
	
	public void setCompositeExpertiseDetail(CompositeExpertiseDetail compositeExpertiseDetail) {
		this.compositeExpertiseDetail = compositeExpertiseDetail;
	}
	
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
		
		@Column(name = "psych_id")
		private Integer psychId;
		
		@Column(name = "expertise_id")
		private Integer expertiseId;

		
		
		public CompositeExpertiseDetail() {
			super();
		}
		
		public CompositeExpertiseDetail(Integer psychId,Integer expertiseId) {
			super();
			this.expertiseId = expertiseId;
			this.psychId = psychId;
		}
		
		
		
		public Integer getPsychId() {
			return psychId;
		}

		public void setPsychId(Integer psychId) {
			this.psychId = psychId;
		}

		public Integer getExpertiseId() {
			return expertiseId;
		}

		public void setExpertiseId(Integer expertiseId) {
			this.expertiseId = expertiseId;
		}
		
		
		
		@Override
		public boolean equals(Object o) {
			if(this==o)
				return true;
			if(!(o instanceof CompositeExpertiseDetail))
				return false;
			
			CompositeExpertiseDetail that = (CompositeExpertiseDetail) o;
			
			return Objects.equals(psychId, that.psychId) && Objects.equals(expertiseId, that.expertiseId);
			
		}
		
		
		@Override
		public int hashCode() {
			return Objects.hash(psychId,expertiseId);
		}
		
		
		
	}
	
	
}
