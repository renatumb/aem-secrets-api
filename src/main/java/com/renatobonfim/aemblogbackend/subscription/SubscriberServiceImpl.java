package com.renatobonfim.aemblogbackend.subscription;

import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.InvalidFieldException;
import com.renatobonfim.aemblogbackend.customExceptions.SubscriberNotFoundException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscriberEmailService subscriberEmailService;

    @Override
    public Subscriber createSubscriber(Subscriber subscriber) {

        String subsEmail = subscriber.getEmail();
        String subsName = subscriber.getName();
        LocalDateTime now = LocalDateTime.now();

        if (subscriberRepository.findById(subsEmail).isPresent()) {
            throw new InvalidFieldException(Constants.SUBSCRIBER_EMAIL_ALREADY_EXIST);
        }

        Subscriber savedSubscriber = subscriberRepository.save(
                Subscriber.builder()
                        .email(subsEmail)
                        .name(subsName)
                        .dateCreation(now )
                        .dateStatus( now )
                        .enableSubscription(true)
                        .unsubscribeToken(UUID.randomUUID().toString())
                        .statusChangeSource(SubscriptionStatusChangeSource.SUBSCRIBE)
                        .build()
        );

        subscriberEmailService.sendWelcomeEmail(savedSubscriber);

        return savedSubscriber;
    }

    @Override
    public Subscriber updateSubscriber(Subscriber subscriber, String subscriberEmail) throws SubscriberNotFoundException {

        String subsName = subscriber.getName();
        String subsEmail = subscriberEmail;

        Subscriber oldSubscriber = findSubscriberByEmail(subsEmail);

        if ( Objects.nonNull(subsName) && !subsName.isBlank() ) {
            oldSubscriber.setName(subsName);
        }
        
        oldSubscriber.setEnableSubscription( subscriber.isEnableSubscription() );
        oldSubscriber.setDateStatus( LocalDateTime.now() );
        oldSubscriber.setStatusChangeSource(SubscriptionStatusChangeSource.EDITOR_PATCH);

        return subscriberRepository.save(oldSubscriber);
    }

    @Override
    public void unsubscribeByToken(String token) {
        Subscriber subscriber = subscriberRepository.findByUnsubscribeToken(token)
                .orElseThrow(() -> new InvalidFieldException(Constants.SUBSCRIBER_UNSUBSCRIBE_TOKEN_NOT_FOUND));

        subscriber.setEnableSubscription(false);
        subscriber.setDateStatus(LocalDateTime.now());
        subscriber.setStatusChangeSource(SubscriptionStatusChangeSource.UNSUBSCRIBE_LINK);

        subscriberRepository.save(subscriber);
    }

    @Override
    public Subscriber findSubscriberByEmail(String subscriberEmail) throws SubscriberNotFoundException {
        return subscriberRepository.findById(subscriberEmail).orElseThrow(() -> new SubscriberNotFoundException(subscriberEmail));
    }

    @Override
    public Page<Subscriber> findAllSubscribers(int page, int size, String sort, String[] properties) {
        return subscriberRepository.findAll(PageRequest.of(page, size, Sort.Direction.fromString(sort), properties));
    }
}
