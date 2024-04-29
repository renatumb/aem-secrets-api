package com.renatobonfim.aemblogbackend.subscription;

import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.InvalidFieldException;
import com.renatobonfim.aemblogbackend.customExceptions.SubscriberNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public Subscriber createSubscriber(Subscriber subscriber) {

        String subsEmail = subscriber.getEmail();
        String subsName = subscriber.getName();

        try {
            Objects.requireNonNull(subsEmail, Constants.SUBSCRIBER_EMAIL_MISSING);
            Objects.requireNonNull(subsName, Constants.SUBSCRIBER_NAME_MISSING);

            if (subsEmail.isBlank() | subsEmail.isEmpty()) {
                throw new InvalidFieldException(Constants.SUBSCRIBER_EMAIL_INVALID);
            }

            if (subsName.isBlank() | subsName.isEmpty()) {
                throw new InvalidFieldException(Constants.SUBSCRIBER_NAME_INVALID);
            }

            if (subscriberRepository.findById(subsEmail).isPresent()) {
                throw new InvalidFieldException(Constants.SUBSCRIBER_EMAIL_ALREADY_EXIST);
            }

        } catch (NullPointerException | InvalidFieldException ex) {
            throw new InvalidFieldException(ex.getMessage());
        }

        return subscriberRepository.save(
                Subscriber.builder()
                        .email(subsEmail)
                        .dateSubscription(new Date())
                        .enableSubscription(false)
                        .name(subsName).build()
        );
    }

    @Override
    public Subscriber updateSubscriber(Subscriber subscriber, String subscriberEmail) throws SubscriberNotFoundException {

        String subsName = subscriber.getName();
        String subsEmail = subscriberEmail;

        try {
            Objects.requireNonNull(subsEmail, Constants.SUBSCRIBER_EMAIL_MISSING);
            Objects.requireNonNull(subsName, Constants.SUBSCRIBER_NAME_MISSING);

            if (subsEmail.isBlank() | subsEmail.isEmpty()) {
                throw new InvalidFieldException(Constants.SUBSCRIBER_EMAIL_INVALID);
            }

            if (subsName.isBlank() | subsName.isEmpty()) {
                throw new InvalidFieldException(Constants.SUBSCRIBER_NAME_INVALID);
            }
        } catch (NullPointerException | InvalidFieldException ex) {
            throw new InvalidFieldException(ex.getMessage());
        }

        Subscriber oldSubscriber = findSubscriberByEmail(subsEmail);

        oldSubscriber.setName( subsName);
        oldSubscriber.setEnableSubscription( subscriber.isEnableSubscription() );

        if( subscriber.isEnableSubscription() ){
            oldSubscriber.setDateUnsubscription(null);
        }else{
            oldSubscriber.setDateUnsubscription(new Date());
        }

        return subscriberRepository.save(oldSubscriber);
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
