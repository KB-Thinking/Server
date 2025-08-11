package org.sopt.common.scenario;

import com.fasterxml.jackson.databind.JsonNode;
import org.sopt.IntentDetector;
import org.sopt.IntentType;
import org.sopt.common.type.Route;
import org.sopt.common.util.IntentUtils;
import org.sopt.dto.ChatResponse;
import org.sopt.service.UserProfileService;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

import static org.sopt.common.util.IntentUtils.includesAny;


@Component
public class StockScenario implements Scenario {
    private final UserProfileService profiles;

    public StockScenario(UserProfileService profiles) {
        this.profiles = profiles;
    }

    @Override
    public ChatResponse respond(Long userId, String text) {
        JsonNode root = profiles.readUserJson(userId);
        if (root == null) {
            return new ChatResponse("보유 종목 데이터를 찾지 못했어요. 데이터를 확인해 주세요.", Route.NONE.value());
        }

        IntentType intent = IntentDetector.detect(text);

        if (IntentUtils.includesAny(text,
                "추세", "시장 추세", "요새 시장", "요즘 시장", "최근 시장",
                "재분석", "다시 분석", "다시 분석해줘", "추세를 통해", "추세 통해")) {
            intent = IntentType.TREND;
        }

        if (IntentUtils.includesAny(text,
                "추천", "추천할 만한", "추가 투자", "추가적인 투자", "포트폴리오 외", "다른 종목",
                "nvidia", "엔비디아")) {
            intent = IntentType.RECOMMEND;
        }
        if (IntentUtils.includesAny(text,
                "알림 설정", "알람 설정", "알림을 설정", "알림 맞춰", "가격 알림", "알림 등록", "알림 추가", "알림 켜",
                "nvidia 알림", "엔비디아 알림", "알림", "알람")) {
            intent = IntentType.ALERT;
        }

        if (intent == IntentType.NONE || intent == IntentType.RESTART) intent = IntentType.BRIEFING;

        return switch (intent) {
            case BRIEFING -> new ChatResponse(buildBriefing(root), Route.NONE.value());
            case TREND -> new ChatResponse(buildTrendAnalysis(root), Route.NONE.value());
            case RECOMMEND -> new ChatResponse(buildRecommendation(root), Route.NONE.value());
            case ALERT -> handleAlert(root, text);
            case CONFIRM ->
                    new ChatResponse("원하시는 작업을 말씀해 주세요. 예: \"동향 브리핑\" / \"시장 추세 분석\" / \"알림 설정\"", Route.NONE.value());
            default -> new ChatResponse(buildBriefing(root), Route.NONE.value());
        };
    }

    private String buildBriefing(JsonNode root) {
        DecimalFormat df = new DecimalFormat("#,###");
        var prices = root.path("market").path("pricesKRW");
        int ss = prices.path("005930").asInt(95000);
        int kakao = prices.path("035720").asInt(130000);
        int hyundai = prices.path("005380").asInt(210000);

        var stocks = root.path("investments").path("stocks");
        int ssQty = findQty(stocks, "005930"), ssAvg = findAvg(stocks, "005930");
        int kkQty = findQty(stocks, "035720"), kkAvg = findAvg(stocks, "035720");
        int hdQty = findQty(stocks, "005380"), hdAvg = findAvg(stocks, "005380");

        return "이준호님, 오늘의 주식 동향 브리핑입니다.\n"
                + "- 삼성전자 주가는 " + df.format(ss) + "원으로 **0.5% 상승**했습니다. 글로벌 반도체 수요 증가와 안정적인 실적 덕분에 긍정적인 흐름이에요. "
                + "(보유 " + ssQty + "주, 평단 " + df.format(ssAvg) + "원, 현재가 대비 " + pctText(ss, ssAvg) + ")\n"
                + "- 카카오는 " + df.format(kakao) + "원으로 **0.3% 상승**했습니다. 광고 수익 증가와 콘텐츠 확장이 긍정적이지만, 규제 이슈는 여전히 변수입니다. "
                + "(보유 " + kkQty + "주, 평단 " + df.format(kkAvg) + "원, 현재가 대비 " + pctText(kakao, kkAvg) + ")\n"
                + "- 현대차는 " + df.format(hyundai) + "원으로 **1.2% 상승**했습니다. 전기차 시장 확장과 제품 믹스 개선으로 안정적인 상승 흐름을 보입니다. "
                + "(보유 " + hdQty + "주, 평단 " + df.format(hdAvg) + "원, 현재가 대비 " + pctText(hyundai, hdAvg) + ")\n"
                + "\n요약: 삼성전자·현대차는 안정적 흐름, 카카오는 단기 변동성 유의가 필요합니다.";
    }

    private String buildTrendAnalysis(JsonNode root) {
        return "네, 최근 시장 추세를 반영해 다시 분석드릴게요.\n"
                + "• 삼성전자: 반도체 수요와 글로벌 경기 회복 효과로 **중장기적으로 긍정적**입니다.\n"
                + "• 카카오: 광고·콘텐츠는 개선 중이나 **규제 우려**로 단기 **변동성 확대 가능성**이 있습니다.\n"
                + "• 현대차: 전기차 라인업 확장, ASP 개선으로 **강한 성장 추세**를 유지하고 있습니다.\n"
                + "\n전체적으로 삼성전자·현대차는 안정적인 추세를, 카카오는 신중 접근이 바람직해 보입니다.";
    }

    private String buildRecommendation(JsonNode root) {
        return "이준호님, 현재 경제 전반적으로 **기술주가 강세**입니다. "
                + "최근 IT 분야의 긍정적 실적 발표 영향이 있어요. 추천드리는 주식은 **NVIDIA**입니다. "
                + "현재 **약 5% 상승 중**이며, AI 관련 투자 확대에 따른 **지속적 성장**이 기대됩니다. "
                + "관련 뉴스 브리핑이 필요하시면 말씀해 주세요.";
    }

    private ChatResponse handleAlert(JsonNode root, String text) {
        if (includesAny(text.toLowerCase(), "nvidia", "엔비디아", "알림", "알림 설정", "알람 설정", "알림을 설정", "알림 맞춰", "가격 알림", "알림 등록", "알림 추가", "알림 켜",
                "nvidia 알림", "엔비디아 알림", "알림", "알람")) {
            String nvda = buildNvdaAlert(root);
            return new ChatResponse(nvda + "로 알림 설정 **완료되었습니다**.", Route.ALERT.value());
        }
        String own = buildOwnAlerts(root);
        String tail = "\n원하시면 NVIDIA(엔비디아)에도 알림을 추가할게요. \"엔비디아 알림 설정\"이라고 말씀해 주세요.";
        return new ChatResponse(own + tail, Route.NONE.value());
    }

    private String buildOwnAlerts(JsonNode root) {
        DecimalFormat df = new DecimalFormat("#,###");
        double p = root.path("alerts").path("defaults").path("ownStockPercent").asDouble(0.003);
        var prices = root.path("market").path("pricesKRW");
        int ss = prices.path("005930").asInt(95000);
        int kakao = prices.path("035720").asInt(130000);
        int hyundai = prices.path("005380").asInt(210000);
        int ssUp = (int) Math.round(ss * (1 + p)), ssDn = (int) Math.round(ss * (1 - p));
        int kkUp = (int) Math.round(kakao * (1 + p)), kkDn = (int) Math.round(kakao * (1 - p));
        int hdUp = (int) Math.round(hyundai * (1 + p)), hdDn = (int) Math.round(hyundai * (1 - p));
        return "금일 보유 종목 알림(±" + (p * 100) + "%)을 설정했습니다.\n"
                + "- 삼성전자 " + df.format(ssUp) + "원 이상 또는 " + df.format(ssDn) + "원 이하\n"
                + "- 카카오 " + df.format(kkUp) + "원 이상 또는 " + df.format(kkDn) + "원 이하\n"
                + "- 현대차 " + df.format(hdUp) + "원 이상 또는 " + df.format(hdDn) + "원 이하";
    }

    private String buildNvdaAlert(JsonNode root) {
        double p = root.path("alerts").path("defaults").path("watchlistPercent").asDouble(0.05);
        double price = root.path("market").path("pricesUSD").path("NVDA").asDouble(215.00);
        double up = roundToNearestHalf(price * (1 + p));
        double dn = roundToNearestHalf(price * (1 - p));
        return "NVIDIA 주식에 대해 **5% 상승** 또는 **5% 하락** 시 알림: **$"
                + fmt2(up) + " 이상** 또는 **$" + fmt2(dn) + " 이하**";
    }

    private int findQty(JsonNode stocks, String ticker) {
        for (JsonNode n : stocks) if (ticker.equals(n.path("ticker").asText())) return n.path("quantity").asInt(0);
        return 0;
    }

    private int findAvg(JsonNode stocks, String ticker) {
        for (JsonNode n : stocks) if (ticker.equals(n.path("ticker").asText())) return n.path("avgPrice").asInt(0);
        return 0;
    }

    private String pctText(int price, int avg) {
        if (avg <= 0) return "-";
        double pct = ((double) price / avg - 1.0) * 100.0;
        return String.format("%.2f%%", pct);
    }

    private static String fmt2(double v) {
        return String.format("%.2f", v);
    }

    private static double roundToNearestHalf(double x) {
        return Math.round(x * 2.0) / 2.0;
    }
}