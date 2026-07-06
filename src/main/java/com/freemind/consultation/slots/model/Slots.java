package com.freemind.consultation.slots.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.freemind.consultation.orders.model.Orders;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "consultation_slots")
public class Slots {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //因為是AI就不用再寫@Notnull
	@Column(name = "timeslot_id")
	private Integer timeslotId; //not null(PK)(AI)
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "psych_id", referencedColumnName = "psych_id", nullable = false)
	@NotNull(message = "心理師編號：請勿空白")
	private Psychologist psychologist; //not null(FK), 缺FK>>已改
	
	@Column(name = "slot_date", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd") //pattern只可以寫日期格式字串，補充說明要在@NotNull
	@NotNull(message = "預約日期：請填入此格式 yyyy-MM-dd")
	private LocalDate slotDate; //not null
	
	@Column(name = "appt_status", nullable = false, length = 24)
	@NotNull(message = "預約狀態：請勿空白")
	@Size(min = 24, max = 24, message = "預約狀態長度必須為24") //為String，驗證字串長度一定是24
	@Pattern(regexp = "[012]{24}", message = "預約狀態只能為0、1或2") //驗證每個字元只能是012
	private String consStatus; //not null，0：營業時間、可預約，1：已預約、2：非營業時間

	@OneToOne(mappedBy = "slot")
	private Orders orders;
	
	public Integer getTimeslotId() {
		return timeslotId;
	}
	

	public void setTimeslotId(Integer timeslotId) {
		this.timeslotId = timeslotId;
	}

	public LocalDate getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(LocalDate slotDate) {
		this.slotDate = slotDate;
	}

	public String getConsStatus() {
		return consStatus;
	}

	public void setConsStatus(String consStatus) {
		this.consStatus = consStatus;
	}
	
	

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "Slots [timeslotId=" + timeslotId + ", slotDate=" + slotDate + ", consStatus="
				+ consStatus + "]";
	}


	public Psychologist getPsychologist() {
		return psychologist;
	}


	public void setPsychologist(Psychologist psychologist) {
		this.psychologist = psychologist;
	}
	
	
}

	

