package com.example.linkscope.util;

import com.example.linkscope.exception.ShortCodeGenerationException;
import com.example.linkscope.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class ShortCodeGenerator {

    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int CODE_LENGTH = 7;
    private static final int MAX_ATTEMPTS = 10;

    private final LinkRepository linkRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String candidate = generateCandidate();
            if (!linkRepository.existsByShortCode(candidate)) {
                return candidate;
            }
        }
        throw new ShortCodeGenerationException("Unable to generate unique short code after " + MAX_ATTEMPTS + " attempts");
    }

    private String generateCandidate() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHABET.length);
            builder.append(ALPHABET[index]);
        }
        return builder.toString();
    }
}
