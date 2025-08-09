package org.sopt.dto;

import org.sopt.common.type.Route;

public record ChatResponse(String responseText, String route) {
    public static ChatResponse of(String text, Route route) {
        return new ChatResponse(text, route.value());
    }
}
