package com.codesolutions.pmt.service;

public interface EmailGateway {
    void send(String recipient, String subject, String body);
}
