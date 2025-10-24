package com.renatobonfim.aemblogbackend.subscription;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "_subscriber")
public class Subscriber {

    private static final long serialVersionUID = 1L;

    @Id
    private String email;

    private String name;

    private boolean enableSubscription;

    private Date dateSubscription;

    private Date dateUnsubscription;
}
