package com.codesolutions.pmt.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {
    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void hashUsesSaltAndMatchesOriginalPassword() {
        String first = passwordHasher.hash("Password123!");
        String second = passwordHasher.hash("Password123!");

        assertThat(first).isNotEqualTo(second);
        assertThat(passwordHasher.matches("Password123!", first)).isTrue();
        assertThat(passwordHasher.matches("wrong-password", first)).isFalse();
    }
}
