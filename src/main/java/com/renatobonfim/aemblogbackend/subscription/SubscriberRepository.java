package com.renatobonfim.aemblogbackend.subscription;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    Optional<Subscriber> findByUnsubscribeToken(String unsubscribeToken);
}
