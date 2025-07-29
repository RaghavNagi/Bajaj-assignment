package com.example.springbootassignment;

import com.example.springbootassignment.model.WebhookRequest;
import com.example.springbootassignment.model.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.example.springbootassignment.model.FinalQueryPayload;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class StartupTask implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        WebhookRequest request = new WebhookRequest(
                "John Doe", "REG12347", "john@example.com"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<WebhookResponse> response =
                restTemplate.postForEntity(url, entity, WebhookResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String webhookUrl = response.getBody().getWebhook();
            String accessToken = response.getBody().getAccessToken();

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            String finalQuery = solveQuestion2();

            sendFinalQuery(webhookUrl, accessToken, finalQuery);
        } else {
            System.err.println("Webhook generation failed: " + response.getStatusCode());
        }
        System.out.println("StartupTask triggered!");
    }

    private String solveQuestion2() {
        return "SELECT " +
                "e1.EMP_ID, " +
                "e1.FIRST_NAME, " +
                "e1.LAST_NAME, " +
                "d.DEPARTMENT_NAME, " +
                "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                "FROM EMPLOYEE e1 " +
                "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                "LEFT JOIN EMPLOYEE e2 " +
                "ON e1.DEPARTMENT = e2.DEPARTMENT " +
                "AND e2.DOB > e1.DOB " +
                "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                "ORDER BY e1.EMP_ID DESC;";
    }

    private void sendFinalQuery(String webhookUrl, String token, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        FinalQueryPayload payload = new FinalQueryPayload(finalQuery);

        HttpEntity<FinalQueryPayload> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("SQL answer submitted successfully!");
            } else {
                System.err.println("Submission failed: " + response.getStatusCode());

                System.err.println("Response body: " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("401 Unauthorized. Check token or format.");
            System.err.println("Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Other exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}