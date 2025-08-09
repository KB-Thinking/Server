package org.sopt.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sopt.common.type.ScenarioType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserProfileService {
    private final ObjectMapper om = new ObjectMapper();

    @Value("${mock.data-dir:./local-data}")
    private String dataDir;

    public ScenarioType resolveScenario(Long userId) {
        ScenarioType fromFile = readScenarioFromFile(userId);
        if (fromFile != null) return fromFile;

        if (userId != null) {
            if (userId == 1L) return ScenarioType.LIMIT;
            if (userId == 2L) return ScenarioType.FUND;
            if (userId == 3L) return ScenarioType.STOCK;
        }
        return ScenarioType.LIMIT;
    }

    public JsonNode readUserJson(Long userId) {
        try {
            Path p = Paths.get(dataDir, "user_" + userId + ".json");
            if (!Files.exists(p)) return null;
            return om.readTree(Files.readString(p));
        } catch (IOException e) {
            return null;
        }
    }

    private ScenarioType readScenarioFromFile(Long userId) {
        try {
            Path p = Paths.get(dataDir, "user_" + userId + ".json");
            if (!Files.exists(p)) return null;

            JsonNode root = om.readTree(Files.readString(p));
            String scenario = root.path("scenario").asText(null);
            if (scenario == null) return null;
            return ScenarioType.valueOf(scenario.toUpperCase());
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }
}
