package atar.bpmn.parking_reservations.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "price_rate")
@Getter
@Setter
public class PriceRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cost_per_standard_hour")
    private Long costPerStandardHour;

    @Column(name = "min_hours")
    private Integer minHours;
}
