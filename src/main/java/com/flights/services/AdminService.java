package com.flights.services;

import com.flights.DAO.Admin;
import com.flights.DAO.Client;
import com.flights.Main;
import com.flights.repos.AdminRepository;
import com.flights.repos.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository, ClientRepository clientRepository) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
    }

    public String get(Integer id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();

            return Main.GSON.toJson(admin);
        } else {
            return Main.GSON.toJson("Такого админа не существует.");
        }
    }

    public String registration(String name, String email, Integer passport) {

        Pattern regexMail = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[a-z]{2,4}\\b");
        Pattern regexName = Pattern.compile("^[a-zA-Z]*$");
        Matcher matcherName = regexName.matcher(name);
        Matcher matcherMail = regexMail.matcher(email);

        if (!matcherMail.matches() || !matcherName.matches()) {
            return "Incorrect personal data";
        }

        Client client = Client.newBuilder()
                .setName(name)
                .setEmail(email)
                .setPassport(passport)
                .build();

        try {
            clientRepository.save(client);

            return Main.GSON.toJson("200");
        } catch (Exception e) {
            return Main.GSON.toJson(e.getMessage());
        }
    }
}
