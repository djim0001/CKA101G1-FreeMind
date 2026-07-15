package com.freemind.course.course.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.freemind.course.order.model.OrderDetail;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "courses")
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private Integer courseId;
	@Column(name = "course_name")
	@NotBlank(message="課程名稱：請勿空白")
//	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", 
//		message = "課程名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String courseName;
	
	@ManyToOne
	@JoinColumn(name = "psych_id", referencedColumnName = "psych_id")
//	@NotNull // 這裡不做驗證，因為 psychId 是從 session 來的
	private Psychologist psychologist;
//	@Column(name = "psych_id")
//	private Integer psychId;
	
	@ManyToOne
	@JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
	private Admin admin;
	
	@ManyToOne
	@JoinColumn(name = "course_cat_id", referencedColumnName = "course_cat_id")
	@NotNull(message = "課程分類: 請選擇")
	private CourseCategories courseCategories;
	
	@Column(name = "video_src")
//	@NotEmpty(message="課程影片: 請勿空白")
	private String videoSrc;
	@Column(name = "video_src_pre")
//	@NotEmpty(message="課程預覽影片: 請勿空白")
	private String videoSrcPre;
	@Column(name = "outline")
	@NotBlank(message="課程預覽影片大綱: 請勿空白")
	private String outline;
	@Column(name = "listed_at")
	private LocalDateTime listedAt;
	@Column(name = "delisted_at")
	private LocalDateTime delistedAt;
	
	@Convert(converter = DelistReasonConverter.class)
	@Column(name = "delist_reason")
	private DelistReason delistReason;
	// 寫法: course.setDelistReason(DelistReason.REGULATION_CHANGE);
	
	@Column(name = "course_status")
	@NotNull
	@DecimalMin(value = "0")
	@DecimalMax(value = "5")
	private Byte courseStatus = 0;
	@Column(name = "save_count")
	@NotNull
	@DecimalMin(value = "0")
	private Integer saveCount = 0;
	@Column(name = "star_count")
	@NotNull
	@DecimalMin(value = "0")
	private Integer starCount = 0;
	@Column(name = "review_count")
	@NotNull
	@DecimalMin(value = "0")
	private Integer reviewCount = 0;
	@Column(name = "comment_count")
	@NotNull
	@DecimalMin(value = "0")
	private Integer commentCount = 0;
	@Column(name = "psych_discount")
	@Digits(integer = 1, fraction = 2, message = "價格格式錯誤，最多 1 位整數與 2 位小數")
	@DecimalMin(value = "0.01", message = "心理師折扣: 不能小於{value}")
	@DecimalMax(value = "1", message = "心理師折扣: 不能超過{value}")
	private BigDecimal psychDiscount;
	@Column(name = "discount_start")
	private LocalDateTime discountStart;
	@Column(name = "discount_end")
	private LocalDateTime discountEnd;
	@Column(name = "price")
	@NotNull(message="課程價格：請勿空白")
	@DecimalMin(value = "0", message = "課程價格: 不能小於{value}")
	@DecimalMax(value = "1000000000", message = "課程價格: 不能小於{value}")
	private Integer price;
	
	@Transient
	private Boolean saved = false;
	
	// 課程提問
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
	@OrderBy("question_id asc")
	private Set<CourseQaComment> courseQaComment;
	//訂單明細
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
	@OrderBy("course_order_id asc")
	private Set<OrderDetail> orderDetails;
	
	
	
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public Psychologist getPsychologist() {
		return psychologist;
	}
	public void setPsychologist(Psychologist psychologist) {
		this.psychologist = psychologist;
	}
	public Admin getAdmin() {
		return admin;
	}
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	public CourseCategories getCourseCategories() {
		return courseCategories;
	}
	public void setCourseCategories(CourseCategories courseCategories) {
		this.courseCategories = courseCategories;
	}
	public String getVideoSrc() {
		return videoSrc;
	}
	public void setVideoSrc(String videoSrc) {
		this.videoSrc = videoSrc;
	}
	public String getVideoSrcPre() {
		return videoSrcPre;
	}
	public void setVideoSrcPre(String videoSrcPre) {
		this.videoSrcPre = videoSrcPre;
	}
	public String getOutline() {
		return outline;
	}
	public void setOutline(String outline) {
		this.outline = outline;
	}
	public LocalDateTime getListedAt() {
		return listedAt;
	}
	public void setListedAt(LocalDateTime listedAt) {
		this.listedAt = listedAt;
	}
	public LocalDateTime getDelistedAt() {
		return delistedAt;
	}
	public void setDelistedAt(LocalDateTime delistedAt) {
		this.delistedAt = delistedAt;
	}
	public DelistReason getDelistReason() {
		return delistReason;
	}
	public void setDelistReason(DelistReason delistReason) {
		this.delistReason = delistReason;
	}
	public Byte getCourseStatus() {
		return courseStatus;
	}
	public void setCourseStatus(Byte courseStatus) {
		this.courseStatus = courseStatus;
	}
	public Integer getSaveCount() {
		return saveCount;
	}
	public void setSaveCount(Integer saveCount) {
		this.saveCount = saveCount;
	}
	public Integer getStarCount() {
		return starCount;
	}
	public void setStarCount(Integer starCount) {
		this.starCount = starCount;
	}
	public Integer getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public BigDecimal getPsychDiscount() {
		return psychDiscount;
	}
	public BigDecimal getValidPsychDiscount() {
	    return isDiscountValid() ? psychDiscount : null;
	}
	public void setPsychDiscount(BigDecimal psychDiscount) {
		this.psychDiscount = psychDiscount;
	}
	public LocalDateTime getDiscountStart() {
		return discountStart;
	}
	public void setDiscountStart(LocalDateTime discountStart) {
		this.discountStart = discountStart;
	}
	public LocalDateTime getDiscountEnd() {
		return discountEnd;
	}
	public void setDiscountEnd(LocalDateTime discountEnd) {
		this.discountEnd = discountEnd;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Set<CourseQaComment> getCourseQaComment() {
		return courseQaComment;
	}
	public void setCourseQaComment(Set<CourseQaComment> courseQaComment) {
		this.courseQaComment = courseQaComment;
	}
	
	public Set<OrderDetail> getOrderDetails() {
		return orderDetails;
	}
	public void setOrderDetails(Set<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	public Boolean getSaved() {
		return saved;
	}
	public void setSaved(Boolean saved) {
		this.saved = saved;
	}
	
	
	// 課程狀態文字版
	public String getCourseStatusText() {
	    if (courseStatus == null) {
	        return "未知狀態";
	    }
	    switch (courseStatus) {
	        case 0:
	            return "草稿";
	        case 1:
	            return "待審核";
	        case 2:
	            return "審核成功";
	        case 3:
	            return "審核失敗";
	        case 4:
	            return "已上架";
	        case 5:
	            return "已下架";
	        default:
	            return "未知狀態";
	    }
	}
	// 允許修改心理師折扣
	public boolean canAddPsychDiscount() {
	    boolean statusCanDiscount = courseStatus == 2 || courseStatus == 4;

	    return statusCanDiscount && !isDiscountValid();
	}
	// 心理師折扣是否有效
	public boolean isDiscountValid() {
	    LocalDateTime now = LocalDateTime.now();

	    return psychDiscount != null
	            && discountStart != null
	            && discountEnd != null
	            && !now.isBefore(discountStart)
	            && !now.isAfter(discountEnd);
	}
	
	
	

}
