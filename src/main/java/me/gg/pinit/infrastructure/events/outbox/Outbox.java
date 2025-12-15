package me.gg.pinit.infrastructure.events.outbox;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
public abstract class Outbox {

    @Id
    @GeneratedValue
    private Long id;

    public abstract String exchange();

    public abstract String routingKey();
}
