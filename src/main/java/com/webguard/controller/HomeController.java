package com.webguard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URL;
import java.net.URLConnection;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/scan")
    public String scanWebsite(@RequestParam String url, Model model) {
        try {
            // Normalize the URL
            String normalizedUrl = normalizeUrl(url);
            
            // Check if the website uses HTTPS
            boolean isHttps = checkHttps(normalizedUrl);
            
            // Add result to model
            model.addAttribute("url", normalizedUrl);
            model.addAttribute("isHttps", isHttps);
            model.addAttribute("status", isHttps ? "SECURE" : "NOT SECURE");
            model.addAttribute("message", isHttps ? 
                "✓ This website uses HTTPS and is secure." : 
                "✗ This website does not use HTTPS. Your data may not be encrypted.");
            
        } catch (Exception e) {
            model.addAttribute("url", url);
            model.addAttribute("isHttps", false);
            model.addAttribute("status", "ERROR");
            model.addAttribute("message", "Error scanning website: " + e.getMessage());
        }
        
        return "result";
    }

    /**
     * Normalizes the URL by adding https:// if no protocol is specified
     */
    private String normalizeUrl(String url) throws Exception {
        url = url.trim();
        
        // Add https:// if no protocol is specified
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        
        return url;
    }

    /**
     * Checks if a website uses HTTPS
     */
    private boolean checkHttps(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        
        // Set a timeout to avoid hanging
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        // Connect to check if reachable
        connection.connect();
        
        // Check if protocol is HTTPS
        return urlString.startsWith("https://");
    }
}
