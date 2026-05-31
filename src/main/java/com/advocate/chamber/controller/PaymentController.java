package com.advocate.chamber.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
public class PaymentController {

    private String keyId = "rzp_test_SvvI66tE5b3jL2";
    private String keySecret = "9lsXdS8CB5TIAN6qvOU5yOFw";

    // 1. This handles the page display
    @GetMapping("/payment.html")
    public String showPaymentPage() {
        return "payment"; // Looks for payment.html in src/main/resources/templates/
    }

    // 2. This handles the API call for the payment order
    @PostMapping("/api/payment/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createOrder() {
        try {
            // Initialize the Razorpay client
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            
            // Create the order request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", 79900); // Amount in paise (799.00 INR)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");

            // Generate the order in Razorpay
            Order order = razorpay.orders.create(orderRequest);

            // Extract the generated Order ID and send it back to the frontend
            Map<String, String> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("status", order.get("status"));
            
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RazorpayException e) {
            // If something goes wrong, return the error safely
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}