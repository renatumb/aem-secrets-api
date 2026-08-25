package com.renatobonfim.aemblogbackend.notification;

import com.renatobonfim.aemblogbackend.contact.Contact;
import com.renatobonfim.aemblogbackend.contact.ContactService;
import com.renatobonfim.aemblogbackend.subscription.Subscriber;
import com.renatobonfim.aemblogbackend.subscription.SubscriberEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationListener {

    private final SubscriberEmailService subscriberEmailService;
    private final ContactService contactService;

    public EmailNotificationListener(SubscriberEmailService subscriberEmailService, ContactService contactService) {
        this.subscriberEmailService = subscriberEmailService;
        this.contactService = contactService;
    }

    @Async("mailTaskExecutor")
    @EventListener
    public void onWelcomeEmailRequested(WelcomeEmailRequestedEvent event) {
        try {
            Subscriber subscriber = Subscriber.builder()
                    .email(event.email())
                    .name(event.name())
                    .unsubscribeToken(event.unsubscribeToken())
                    .build();

            subscriberEmailService.sendWelcomeEmail(subscriber);
            log.info("Welcome email sent to: {}", event.email());
        } catch (Exception ex) {
            log.error("Failed to send welcome email to {} after retries", event.email(), ex);
        }
    }

    @Async("mailTaskExecutor")
    @EventListener
    public void onContactEmailRequested(ContactEmailRequestedEvent event) {
        try {
            Contact contact = new Contact();
            contact.setName(event.name());
            contact.setEmail(event.email());
            contact.setMessage(event.message());
            contactService.deliverContactEmail(contact);
            log.info("Contact confirmation email sent to: {}", event.email());
        } catch (Exception ex) {
            log.error("Failed to send contact email to {} after retries", event.email(), ex);
        }
    }
}
