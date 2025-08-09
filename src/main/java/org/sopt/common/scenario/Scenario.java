package org.sopt.common.scenario;


import org.sopt.dto.ChatResponse;

public interface Scenario {
    ChatResponse respond(Long userId, String text);
}