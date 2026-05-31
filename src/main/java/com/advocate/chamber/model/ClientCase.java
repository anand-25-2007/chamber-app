package com.advocate.chamber.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "client_intake")
public class ClientCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactNumber;
    private String caseType;
    
    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime submissionTime;

    // PAYMENT FIELDS
    private String paymentStatus;   // "PENDING" or "PAID"
    private String razorpayOrderId; // ID received from Razorpay

    // Default Constructor
    public ClientCase() {
        this.submissionTime = LocalDateTime.now();
        this.paymentStatus = "PENDING"; // Default state
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public LocalDateTime getSubmissionTime() { return submissionTime; }
    
    // PAYMENT GETTERS/SETTERS
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
}