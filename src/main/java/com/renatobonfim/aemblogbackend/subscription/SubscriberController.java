package com.renatobonfim.aemblogbackend.subscription;

import com.renatobonfim.aemblogbackend.customExceptions.SubscriberNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriber")
public class SubscriberController {

    @Autowired
    SubscriberService subscriberService;

    @GetMapping
    public ResponseEntity<Page<Subscriber>> findAllSubscribers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "5") int size,
                                                               @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                               @RequestParam(value = "fields", defaultValue = "email") String properties) {

        return ResponseEntity.ok().body(subscriberService.findAllSubscribers(page, size, sort, properties.split(",")));
    }

    @GetMapping("/{subscriberEmail}")
    public ResponseEntity<Subscriber> findSubscriberByEmail(@Validated @PathVariable String subscriberEmail) throws SubscriberNotFoundException {
        return ResponseEntity.ok(subscriberService.findSubscriberByEmail(subscriberEmail));
    }

    @PostMapping
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws URISyntaxException {
        Subscriber subscriberCreated = subscriberService.createSubscriber(subscriber);
        return ResponseEntity.created(new URI(subscriberCreated.getEmail())).body(subscriberCreated);
    }

    @PatchMapping("/{subscriberEmail}")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber, @Validated @PathVariable String subscriberEmail) throws SubscriberNotFoundException {
        return ResponseEntity.ok(subscriberService.updateSubscriber(subscriber, subscriberEmail));
    }

}
