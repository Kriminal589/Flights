package com.flights.controllers;

import com.flights.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PutMapping("/registration")
    public @ResponseBody String registration(@RequestParam Integer passport, @RequestParam Long id_flight) {
        return bookingService.registration(passport, id_flight);
    }

    @GetMapping
    public @ResponseBody String get(@RequestParam Integer passport) {
        return bookingService.get(passport);
    }

    @PutMapping("/melt")
    public @ResponseBody String melt(@RequestParam Long idFlight, @RequestParam Long idClient, @RequestParam Long card) {
        return bookingService.melt(idClient, idFlight);
    }

    @PutMapping("/mailing")
    public @ResponseBody String mailing(@RequestParam String status) {
        return bookingService.mailing(status);
    }
}
