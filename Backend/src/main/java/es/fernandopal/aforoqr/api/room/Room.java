package es.fernandopal.aforoqr.api.room;

import es.fernandopal.aforoqr.api.address.Address;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    @Column(unique = true)
    private String code;
    private Integer capacity;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, targetEntity = Address.class)
    @JoinColumn(nullable = false, name = "address_id")
    private Address address;

    @ColumnDefault("true")
    private boolean active;

}