package org.sopt.common.scenario;

import com.fasterxml.jackson.databind.JsonNode;
import org.sopt.common.util.IntentUtils;
import org.sopt.common.type.Route;
import org.sopt.common.type.ScenarioStateStore;
import org.sopt.service.UserProfileService;
import org.sopt.dto.ChatResponse;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class StockScenario implements Scenario {
    private final ScenarioStateStore state;
    private final UserProfileService profiles;

    public StockScenario(ScenarioStateStore state, UserProfileService profiles) {
        this.state = state;
        this.profiles = profiles;
    }

    @Override
    public ChatResponse respond(Long userId, String text) {
        int step = state.getStep(userId);
        JsonNode root = profiles.readUserJson(userId);
        if (root == null) {
            return ChatResponse.of("보유 종목 데이터를 찾지 못했어요. 데이터를 확인해 주세요.", Route.NONE);
        }

        if (IntentUtils.includesAny(text, "동향", "레포트", "브리핑", "오늘")) step = Math.max(step, 0);
        if (IntentUtils.includesAny(text, "추세", "시장", "재분석")) step = Math.max(step, 1);
        if (IntentUtils.includesAny(text, "추천", "추가", "다른 종목", "nvidia", "엔비디아")) step = Math.max(step, 2);
        if (IntentUtils.includesAny(text, "알림", "알람", "설정")) step = Math.max(step, 3);

        switch (step) {
            case 0 -> {
                String briefing = buildBriefing(root);
                state.setStep(userId, 1);
                return ChatResponse.of(briefing, Route.NONE);
            }
            case 1 -> {
                String trend = buildTrendAnalysis();
                state.setStep(userId, 2);
                return ChatResponse.of(trend, Route.NONE);
            }
            case 2 -> {
                String reco = "이준호님, 기술주 강세가 이어지고 있어요. 추천 종목은 NVIDIA입니다. "
                        + "최근 AI 수요 확대로 중장기 성장성이 큽니다. 뉴스 설명이 필요 없으시면 알림을 설정해 드릴까요?";
                state.setStep(userId, 3);
                return ChatResponse.of(reco, Route.NONE);
            }
            case 3 -> {
                String ownAlerts = buildOwnAlerts(root);
                state.setStep(userId, 4);
                return ChatResponse.of(ownAlerts, Route.NONE);
            }
            default -> {
                String nvdaAlert = buildNvdaAlert(root);
                state.setStep(userId, 4);
                return ChatResponse.of(nvdaAlert + "\n지금 알림 설정 화면으로 이동할게요.", Route.ALERT);
            }
        }
    }

    private String buildBriefing(JsonNode root) {
        DecimalFormat df = new DecimalFormat("#,###");
        JsonNode prices = root.path("market").path("pricesKRW");
        int ss = prices.path("005930").asInt(95000);
        int kakao = prices.path("035720").asInt(130000);
        int hyundai = prices.path("005380").asInt(210000);

        return "이준호님의 오늘 주식 동향 브리핑입니다.\n"
                + "- 삼성전자 주가는 " + df.format(ss) + "원으로 0.5% 상승했습니다. 반도체 수요와 실적 안정이 긍정적입니다.\n"
                + "- 카카오는 " + df.format(kakao) + "원으로 0.3% 상승했습니다. 광고/콘텐츠는 긍정적이나 규제 변수는 유의입니다.\n"
                + "- 현대차는 " + df.format(hyundai) + "원으로 1.2% 상승했습니다. 전기차 시장 확대로 장기 성장세가 예상됩니다.";
    }

    private String buildTrendAnalysis() {
        return "최근 시장 추세를 보면, 삼성전자는 반도체 수요 회복으로 중장기 우상향, "
                + "카카오는 변동성 확대 가능성, 현대차는 전기차 확대에 따른 안정적 성장 추세입니다.";
    }

    private String buildOwnAlerts(JsonNode root) {
        DecimalFormat df = new DecimalFormat("#,###");
        JsonNode defaults = root.path("alerts").path("defaults");
        double p = defaults.path("ownStockPercent").asDouble(0.003);
        JsonNode prices = root.path("market").path("pricesKRW");

        int ss = prices.path("005930").asInt(95000);
        int kakao = prices.path("035720").asInt(130000);
        int hyundai = prices.path("005380").asInt(210000);

        int ssUp = (int) Math.round(ss * (1 + p));
        int ssDn = (int) Math.round(ss * (1 - p));
        int kkUp = (int) Math.round(kakao * (1 + p));
        int kkDn = (int) Math.round(kakao * (1 - p));
        int hdUp = (int) Math.round(hyundai * (1 + p));
        int hdDn = (int) Math.round(hyundai * (1 - p));

        return "금일 보유 종목 알림을 설정했어요 (±" + (p * 100) + "%).\n"
                + "- 삼성전자 " + df.format(ssUp) + "원 이상 또는 " + df.format(ssDn) + "원 이하\n"
                + "- 카카오 " + df.format(kkUp) + "원 이상 또는 " + df.format(kkDn) + "원 이하\n"
                + "- 현대차 " + df.format(hdUp) + "원 이상 또는 " + df.format(hdDn) + "원 이하";
    }

    private String buildNvdaAlert(JsonNode root) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        JsonNode defaults = root.path("alerts").path("defaults");
        double p = defaults.path("watchlistPercent").asDouble(0.05); // 5%
        double price = root.path("market").path("pricesUSD").path("NVDA").asDouble(215.00);
        double up = Math.round(price * (1 + p) * 100.0) / 100.0;
        double dn = Math.round(price * (1 - p) * 100.0) / 100.0;
        return "NVIDIA 알림을 설정했습니다 (±" + (p * 100) + "%). $" + df.format(up) + " 이상 또는 $" + df.format(dn) + " 이하";
    }
}