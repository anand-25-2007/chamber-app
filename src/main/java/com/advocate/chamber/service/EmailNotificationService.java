package com.advocate.chamber.service;

import com.resend.*;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.stereotype.Service;

import com.advocate.chamber.model.ClientCase;

@Service
public class EmailNotificationService {

    public void sendChamberAlert(ClientCase clientData) {
        // Fetch the API key safely from Railway Environment Variables
        Resend resend = new Resend(System.getenv("RESEND_API_KEY"));

        // Constructing the email body using HTML for professional formatting
        String emailBody = "<strong>Advocate Amrendra Singh,</strong><br><br>" +
                "A new client has submitted an intake form via the website.<br><br>" +
                "<strong>Client Name:</strong> " + clientData.getName() + "<br>" +
                "<strong>Contact Number:</strong> " + clientData.getContactNumber() + "<br>" +
                "<strong>Case Stage:</strong> " + clientData.getCaseType() + "<br>" +
                "<strong>Date Submitted:</strong> " + clientData.getSubmissionTime() + "<br><br>" +
                "<strong>Brief Summary of the Matter:</strong><br>" + 
                clientData.getSummary() + "<br><br>" +
                "---<br><em>Automated Chamber Notification System</em>";

        // Build the email request for the Resend API
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("onboarding@resend.dev") // Mandatory sender for Resend free test accounts
                .to("amrendrasingh.advocate@gmail.com") // Must match the email used to create the Resend account
                .subject("New Client Intake: " + clientData.getCaseType() + " - " + clientData.getName())
                .html(emailBody)
                .build();

        // Send the email via HTTPS (Bypassing Railway's SMTP Firewall)
        try {
            SendEmailResponse data = resend.emails().send(sendEmailRequest);
            System.out.println("Email sent successfully via Resend API! ID: " + data.getId());
        } catch (Exception e) {
            System.err.println("Failed to send email via Resend.");
            e.printStackTrace();
        }
    }
}