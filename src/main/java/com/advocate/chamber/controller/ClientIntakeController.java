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
import com.advocate.chamber.service.EmailNotificationService;

@Controller
public class ClientIntakeController {

    @Autowired
    private ClientCaseRepository repository;

    @Autowired
    private EmailNotificationService emailService;

    @PostMapping("/submit-intake")
    public String handleIntakeSubmission(
            @RequestParam("name") String name,
            @RequestParam("contactNumber") String contactNumber, 
            @RequestParam("caseType") String caseType,
            @RequestParam("summary") String summary,
            @RequestParam("g-recaptcha-response") String recaptchaResponse) { 

        // 1. Set up the reCAPTCHA verification request
        String secretKey = "6LfReQUtAAAAACN38x1LGZBeyZAwN3D0wt89Wb4q"; 
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", secretKey);
        requestMap.add("response", recaptchaResponse);

        // 2. Ask Google if the user is a human
        @SuppressWarnings("unchecked")
        Map<String, Object> apiResponse = restTemplate.postForObject(verifyUrl, requestMap, Map.class);
        
        // Added a safety check here to prevent NullPointerExceptions
        boolean isHuman = (apiResponse != null && Boolean.TRUE.equals(apiResponse.get("success")));

        // 3. If validation fails (it is a bot), reject and redirect away
        if (!isHuman) {
            return "redirect:/"; 
        }

        // 4. Map form data to the data model (Since they passed the test!)
        ClientCase newCase = new ClientCase();
        newCase.setName(name);
        newCase.setContactNumber(contactNumber); 
        newCase.setCaseType(caseType);
        newCase.setSummary(summary);

        // 5. Save securely to the Database
        repository.save(newCase);

        // 6. Trigger Automated Email
        emailService.sendChamberAlert(newCase);

        // 7. Redirect user to the payment page
        return "redirect:/payment.html";
    }
}