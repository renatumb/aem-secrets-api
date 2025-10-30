package com.renatobonfim.aemblogbackend.subscription;

import jakarta.persistence.*;
import java.util.Date;
import jakarta.validation.constraints.*;
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
    @Email(message = "email provided is not valid",
            regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            flags = Pattern.Flag.CASE_INSENSITIVE
    )
    @NotNull(message = "email must be provided")
    private String email;

    @NotBlank(message = "'Name' must not be blank")
    @Size(min = 3, message = "'Name' must be at least 3 chars")
    private String name;

    
    private boolean enableSubscription;

    private Date dateSubscription;

    private Date dateUnsubscription;
}
