package com.flights.services;

import com.flights.DAO.Booking;
import com.flights.DAO.Client;
import com.flights.DAO.Flight;
import com.flights.DTO.BookingDTO;
import com.flights.Main;
import com.flights.repos.BookingRepository;
import com.flights.repos.ClientRepository;
import com.flights.repos.FlightRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final FlightRepository flightRepository;


    @Autowired
    public BookingService(BookingRepository bookingRepository, ClientRepository clientRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.clientRepository = clientRepository;
        this.flightRepository = flightRepository;
    }

    public String registrationCheck(Integer passport, Long id_flight, String name, String email) {

        Pattern regexMail = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[a-z]{2,4}\\b");
        Pattern regexName = Pattern.compile("^[a-zA-Z]*$");
        Matcher matcherName = regexName.matcher(name);
        Matcher matcherMail = regexMail.matcher(email);

        if (!matcherMail.matches() || !matcherName.matches()) {
            return "Incorrect personal data";
        }

        Optional<Client> clientOptional = clientRepository.findByPassport(passport);
        Optional<Flight> flightOptional = flightRepository.findById(id_flight);

        if (clientOptional.isPresent() && flightOptional.isPresent()) {
            Client client = clientOptional.get();
            Flight flight = flightOptional.get();

            return registration(client, flight);
        } else if (flightOptional.isPresent()){
            Client client = Client.newBuilder()
                    .setPassport(passport)
                    .setEmail(email)
                    .setName(name)
                    .build();

            Flight flight = flightOptional.get();

            clientRepository.save(client);

            return registration(client, flight);
        } else {
            return "This flight does not exist";
        }
    }

    public String get(Integer passport) {
        Optional<Client> clientOptional = clientRepository.findByPassport(passport);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            List<Booking> bookingList = bookingRepository.findByClientId(client.getId());
            List<BookingDTO> result = new ArrayList<>();

            for (Booking booking : bookingList) {
                Optional<Flight> flightOptional = flightRepository.findById(booking.getFlightId());

                if (flightOptional.isPresent()) {
                    Flight flight = flightOptional.get();

                    result.add(new BookingDTO(client.getName(), flight.getStartCity(), flight.getEndCity(),
                            flight.getDateStart(), flight.getTimeFlying()));

                }
            }
            return Main.GSON.toJson(result);
        } else {
            return Main.GSON.toJson("Заказов нет.");
        }
    }

    public String melt(Long idClient, Long idFlight) {
        Optional<Booking> booking = bookingRepository.findBookingByClientIdAndFlightId(idClient, idFlight);

        if (booking.isPresent()) {
            return Main.GSON.toJson("You paid for the order.");
        } else {
            return Main.GSON.toJson("Данного заказа не существует.");
        }
    }

    private String registration(@NotNull Client client, @NotNull Flight flight) {
        if (flight.getCount() == 0) {
            return Main.GSON.toJson("All seats on this plane are occupied.");
        } else {
            flight.setCount(flight.getCount() - 1);
            flightRepository.save(flight);
        }

        Booking booking = new Booking();
        booking.setClientId(client.getId());
        booking.setFlightId(flight.getId());
        booking.setStatus("Вылет вовремя");

        Optional<Booking> bookingOptional = bookingRepository.findBookingByClientIdAndFlightId(client.getId(), flight.getId());
        if (bookingOptional.isEmpty()) {
            try {
                bookingRepository.save(booking);

                return Main.GSON.toJson("Booking is created. " + client.getName() +
                        ", you have to pay your booking in order section");

            } catch (Exception e) {
                return Main.GSON.toJson(e.getMessage());
            }
        }

        return "You paid for the order.";
    }
}
