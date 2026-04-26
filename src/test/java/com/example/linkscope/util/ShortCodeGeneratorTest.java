package com.example.linkscope.util;

import com.example.linkscope.exception.ShortCodeGenerationException;
import com.example.linkscope.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortCodeGeneratorTest {

    @Mock
    private LinkRepository linkRepository;

    @Test
    void shouldGenerateBase62CodeWithLength7() {
        when(linkRepository.existsByShortCode(anyString())).thenReturn(false);
        ShortCodeGenerator generator = new ShortCodeGenerator(linkRepository);

        String code = generator.generateUniqueCode();

        assertThat(code)
                .hasSize(7)
                .matches("^[A-Za-z0-9]{7}$");
    }

    @Test
    void shouldRetryOnCollision() {
        when(linkRepository.existsByShortCode(anyString())).thenReturn(true, true, false);
        ShortCodeGenerator generator = new ShortCodeGenerator(linkRepository);

        String code = generator.generateUniqueCode();

        assertThat(code).hasSize(7);
        verify(linkRepository, times(3)).existsByShortCode(anyString());
    }

    @Test
    void shouldThrowWhenExhaustedAttempts() {
        when(linkRepository.existsByShortCode(anyString())).thenReturn(true);
        ShortCodeGenerator generator = new ShortCodeGenerator(linkRepository);

        assertThatThrownBy(generator::generateUniqueCode)
                .isInstanceOf(ShortCodeGenerationException.class)
                .hasMessageContaining("Unable to generate unique short code");

        verify(linkRepository, times(10)).existsByShortCode(anyString());
    }
}
