package com.weighttracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    // デフォルトの都市（Tokyo）
    @Value("${openweathermap.default.city:Tokyo}")
    private String defaultCity;

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 指定された都市の現在の天気データを取得します
     * @param city 都市名
     * @return 天気情報を含むマップ
     */
    @SuppressWarnings("UseSpecificCatch")
    public Map<String, Object> getCurrentWeather(String city) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ja", 
                    apiUrl, city, apiKey);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return response;
        } catch (Exception e) {
            System.err.println("天気情報の取得に失敗しました: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * デフォルト都市（Tokyo）の天気を取得します
     * @return 天気情報を含むマップ
     */
    public Map<String, Object> getDefaultCityWeather() {
        return getCurrentWeather(defaultCity);
    }

    /**
     * 天気に基づいた健康アドバイスを生成します
     * @param weatherData 天気データ
     * @return 健康アドバイス
     */
    public String generateHealthAdvice(Map<String, Object> weatherData) {
        if (weatherData == null || weatherData.isEmpty()) {
            return "天気情報が取得できませんでした。一般的に、定期的な運動と十分な水分摂取を心がけましょう。";
        }

        try {
            // 気温の取得
            Map<String, Object> main = (Map<String, Object>) weatherData.get("main");
            double temperature = (Double) main.get("temp");
            int humidity = ((Number) main.get("humidity")).intValue();
            
            // 天気の取得
            Map<String, Object> weatherInfo = ((java.util.List<Map<String, Object>>) weatherData.get("weather")).get(0);
            String weatherMain = (String) weatherInfo.get("main");
            
            // 気温に基づいたアドバイス
            if (temperature > 28) {
                return "気温が高いため、屋内での軽いエクササイズがおすすめです。十分な水分補給も忘れずに。";
            } else if (temperature < 10) {
                return "気温が低いため、外出前の十分なウォームアップを行いましょう。屋内での有酸素運動も効果的です。";
            }
            
            // 天気に基づいたアドバイス
            if ("Rain".equalsIgnoreCase(weatherMain) || "Drizzle".equalsIgnoreCase(weatherMain)) {
                return "雨の日は室内でのストレッチや筋トレがおすすめです。湿度が高いので水分補給を心がけましょう。";
            } else if ("Clear".equalsIgnoreCase(weatherMain)) {
                return "晴れた日は軽いジョギングや散歩で体を動かすのに最適です。日焼け対策もお忘れなく。";
            } else if ("Clouds".equalsIgnoreCase(weatherMain)) {
                return "曇りの日は、日差しを気にせず屋外でのウォーキングやサイクリングがおすすめです。";
            } else if ("Snow".equalsIgnoreCase(weatherMain)) {
                return "雪の日は転倒に注意し、室内でのヨガや軽いストレッチで体を温めましょう。";
            }
            
            // 湿度に基づいたアドバイス
            if (humidity > 80) {
                return "湿度が高いため、激しい運動は控えめにし、こまめな水分補給を心がけましょう。";
            } else if (humidity < 40) {
                return "乾燥しているため、運動前後の水分補給を特に心がけましょう。";
            }
            
            // デフォルトのアドバイス
            return "今日は適度な運動と十分な水分摂取を心がけましょう。体重管理のためには規則正しい生活も大切です。";
        } catch (Exception e) {
            System.err.println("健康アドバイスの生成に失敗しました: " + e.getMessage());
            return "健康的な生活のためには、バランスの取れた食事と定期的な運動を心がけましょう。";
        }
    }
}