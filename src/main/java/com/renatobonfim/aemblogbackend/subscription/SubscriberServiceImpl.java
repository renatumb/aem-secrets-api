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

        if (subscriberRepository.findById(subsEmail).isPresent()) {
            throw new InvalidFieldException(Constants.SUBSCRIBER_EMAIL_ALREADY_EXIST);
        }

        return subscriberRepository.save(
                Subscriber.builder()
                        .email(subsEmail)
                        .dateSubscription(new Date())
                        .enableSubscription(true)
                        .name(subsName).build()
        );
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
