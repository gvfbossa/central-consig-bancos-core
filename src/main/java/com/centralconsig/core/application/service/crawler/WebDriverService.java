package com.centralconsig.core.application.service.crawler;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class WebDriverService {

    @Value("${sheet.download.dir:/app/data/spreadsheets}")
    private String DOWNLOAD_DIR;

    public WebDriver criarDriver() {
        String downloadPath = Paths.get(DOWNLOAD_DIR).toAbsolutePath().toString();
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
        options.addArguments("--disable-extensions");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);

        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    public WebDriverWait criarWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void fecharDriver(WebDriver driver) {
        try {
            if (driver != null) {
                driver.close();
            }
        } catch (Exception ignored) {}

        ExecutorService executor = Executors.newSingleThreadExecutor();
        WebDriver finalDriver = driver;
        Future<?> future = executor.submit(() -> {
            try {
                if (finalDriver != null) {
                    finalDriver.quit();
                }
            } catch (Exception ignored) {}
        });

        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        } finally {
            executor.shutdownNow();
            driver = null;
        }
    }


}
