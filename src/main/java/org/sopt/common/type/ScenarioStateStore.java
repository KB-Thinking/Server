package org.sopt.common.type;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScenarioStateStore {
    private final Map<Long, Integer> stepMap = new ConcurrentHashMap<>();

    public int getStep(Long userId) {
        return stepMap.getOrDefault(userId, 0);
    }

    public void setStep(Long userId, int step) {
        stepMap.put(userId, step);
    }

    public void reset(Long userId) {
        stepMap.remove(userId);
    }


}
