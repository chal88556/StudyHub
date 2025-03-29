package com.example.WebChat.Service;

import com.example.WebChat.Entity.AuthenticationDoc;
import com.example.WebChat.Repo.AuthenticationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationRepo authenticationRepo;

    public AuthenticationDoc save(AuthenticationDoc authDoc) {
        return authenticationRepo.save(authDoc);
    }

    public AuthenticationDoc findByStudentEmail(String email) {
        return authenticationRepo.findByStudentEmail(email);
    }

    public void updateVerification(String email, boolean verified) {
        AuthenticationDoc authDoc = findByStudentEmail(email);
        if (authDoc != null) {
            authDoc.setVerified(verified);
            authenticationRepo.save(authDoc);
        }
    }
}
