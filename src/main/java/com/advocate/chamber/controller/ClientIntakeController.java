package com.advocate.chamber.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.advocate.chamber.model.ClientCase;
import com.advocate.chamber.repository.ClientCaseRepository;

@Controller
public class ClientIntakeController {

    @Autowired
    private ClientCaseRepository repository;

    @PostMapping("/submit-intake")
    public String handleIntakeSubmission(
            @RequestParam("name") String name,
            @RequestParam("contactNumber") String contactNumber, 
            @RequestParam("caseType") String caseType,
            @RequestParam("summary") String summary,
            @RequestParam("g-recaptcha-response") String recaptchaResponse) { 

        String secretKey = System.getenv("RECAPTCHA_SECRET");
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", secretKey);
        requestMap.add("response", recaptchaResponse);

        @SuppressWarnings("unchecked")
        Map<String, Object> apiResponse = restTemplate.postForObject(verifyUrl, requestMap, Map.class);
        
        boolean isHuman = (apiResponse != null && Boolean.TRUE.equals(apiResponse.get("success")));

        if (!isHuman) {
            return "redirect:/"; 
        }

        ClientCase newCase = new ClientCase();
        newCase.setName(name);
        newCase.setContactNumber(contactNumber); 
        newCase.setCaseType(caseType);
        newCase.setSummary(summary);
        // paymentStatus is automatically "PENDING" based on your model constructor

        // Save to Database to generate the ID
        repository.save(newCase);

        // Redirect to payment page and carry the ID in the URL
        return "redirect:/payment.html?caseId=" + newCase.getId();
    }
}