package com.advocate.chamber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.advocate.chamber.model.ClientCase;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendChamberAlert(ClientCase clientData) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        // This is the destination email address
        message.setTo("amrendrasingh.advocate@gmail.com"); 
        message.setSubject("New Client Intake: " + clientData.getCaseType() + " - " + clientData.getName());
        
        // Added the Contact Number to the email body
        String emailBody = "Advocate Amrendra Singh,\n\n" +
                "A new client has submitted an intake form via the website.\n\n" +
                "Client Name: " + clientData.getName() + "\n" +
                "Contact Number: " + clientData.getContactNumber() + "\n" +
                "Case Stage: " + clientData.getCaseType() + "\n" +
                "Date Submitted: " + clientData.getSubmissionTime() + "\n\n" +
                "Brief Summary of the Matter:\n" + 
                clientData.getSummary() + "\n\n" +
                "---\nAutomated Chamber Notification System";
                
        message.setText(emailBody);
        
        // Triggers the email
        mailSender.send(message);
    }
}