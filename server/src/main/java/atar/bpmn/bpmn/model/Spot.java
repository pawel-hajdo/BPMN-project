package atar.bpmn.bpmn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "spot")
@Getter
@Setter
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parking_id", nullable = false)
    private Parking parking;

    @Column(name = "space_code")
    private String spaceCode;

    @ManyToOne
    @JoinColumn(name = "spot_type_id", nullable = false)
    private SpotType spotType;
}
