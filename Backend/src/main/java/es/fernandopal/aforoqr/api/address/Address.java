package es.fernandopal.aforoqr.api.address;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    protected String buildingNumber;
    protected String street;
    protected String city;
    protected String country;

    @Override
    public String toString() {
        return street + ", " + buildingNumber + " (" + city + ")";
    }
}