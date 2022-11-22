package com.flights.DAO;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
public final class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long clientId;
    private Long flightId;
    private String status;

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
