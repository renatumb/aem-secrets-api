package com.renatobonfim.aemblogbackend.contact;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/contact")
@AllArgsConstructor
public class ContactController {

    private final ContactService contactService;


    @PostMapping("/send")
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody Contact contact) throws URISyntaxException {
        contactService.sendEmail(contact);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
