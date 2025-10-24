package com.renatobonfim.aemblogbackend.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDTO(
        String message,
        String timestamp
) {
}
