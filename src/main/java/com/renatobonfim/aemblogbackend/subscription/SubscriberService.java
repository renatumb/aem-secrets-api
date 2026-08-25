package com.renatobonfim.aemblogbackend.subscription;

import com.renatobonfim.aemblogbackend.customExceptions.SubscriberNotFoundException;
import org.springframework.data.domain.Page;

public interface SubscriberService {

    Subscriber createSubscriber(Subscriber subscriber);

    Subscriber updateSubscriber(Subscriber subscriber, String subscriberEmail) throws SubscriberNotFoundException;

    void unsubscribeByToken(String token);

    Subscriber findSubscriberByEmail(String subscriberEmail) throws SubscriberNotFoundException;

    Page<Subscriber> findAllSubscribers(int page, int size, String sort, String[] properties);
}
