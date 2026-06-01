package com.advocate.chamber.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.advocate.chamber.model.ClientCase;
import com.advocate.chamber.repository.ClientCaseRepository;
import com.advocate.chamber.service.EmailNotificationService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
public class PaymentController {

    @Autowired
    private ClientCaseRepository repository;

    @Autowired
    private EmailNotificationService emailService;

    private String keyId = System.getenv("RAZORPAY_KEY_ID");
    private String keySecret = System.getenv("RAZORPAY_KEY_SECRET");

    @GetMapping("/payment.html")
    public String showPaymentPage() {
        return "payment"; 
    }

    @PostMapping("/api/payment/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createOrder() {
        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", 79900); 
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");

            Order order = razorpay.orders.create(orderRequest);

            Map<String, String> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("status", order.get("status"));
            
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RazorpayException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // NEW ENDPOINT: Triggers AFTER successful payment
    @PostMapping("/api/payment/verify")
    @ResponseBody
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, Object> payload) {
        try {
            Long caseId = Long.valueOf(payload.get("caseId").toString());
            String paymentId = payload.get("paymentId").toString();

            // Find the case in the database
            ClientCase clientCase = repository.findById(caseId).orElse(null);
            
            if (clientCase != null) {
                // Update payment status
                clientCase.setPaymentStatus("PAID");
                clientCase.setRazorpayOrderId(paymentId);
                repository.save(clientCase);

                // FIRE THE EMAIL ONLY NOW
                emailService.sendChamberAlert(clientCase);
                
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}