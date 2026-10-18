package com.rbdip.bookstore.confirmation;

import org.springframework.stereotype.Service;

@Service
public class SenderService {

    public void send(String message) {
        System.out.println(message);
    }
}
