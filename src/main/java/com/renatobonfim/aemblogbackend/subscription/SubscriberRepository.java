package com.renatobonfim.aemblogbackend.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}
