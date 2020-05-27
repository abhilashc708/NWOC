package com.nwoc.a3gs.group.app.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "passwordreset")
@EntityListeners(AuditingEntityListener.class)
public class PasswordResetToken {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String token;

	    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	    @JoinColumn(nullable = false, name = "userid")
	    private User user;

	    @Column(nullable = false)
	    private Date expiryDate;
	    
	   public PasswordResetToken() {}

	    public PasswordResetToken(String token, User user) {
			super();
			this.token = token;
			this.user = user;
		}
	    
		public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }

	    public User getUser() {
	        return user;
	    }

	    public void setUser(User user) {
	        this.user = user;
	    }

	    public Date getExpiryDate() {
	        return expiryDate;
	    }

	    public void setExpiryDate(Date expiryDate) {
	        this.expiryDate = expiryDate;
	    }

	    public void setExpiryDate(int minutes){
		  Calendar now = Calendar.getInstance(); 
		  now.add(Calendar.MINUTE, minutes);
		  this.expiryDate = now.getTime();
		 
	    }

	    public boolean isExpired() {
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	    	SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date newtime = null;
			try {
				newtime = localDateFormat.parse( simpleDateFormat.format(new Date()) );
				Calendar now = Calendar.getInstance(); 
				now.setTime(newtime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        return newtime.after(this.expiryDate);
	    }
	}
