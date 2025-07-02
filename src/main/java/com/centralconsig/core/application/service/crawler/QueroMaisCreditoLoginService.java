package com.centralconsig.core.application.service.crawler;

import com.centralconsig.core.application.utils.CrawlerUtils;
import com.centralconsig.core.domain.entity.UsuarioLoginQueroMaisCredito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QueroMaisCreditoLoginService {

    private final WebDriverService webDriverService;

    @Value("${base.url.quero.mais.credito}")
    private String BASE_URL;

    private static final Logger log = LoggerFactory.getLogger(QueroMaisCreditoLoginService.class);

    public QueroMaisCreditoLoginService(WebDriverService webDriverService) {
        this.webDriverService = webDriverService;
    }

    public boolean seleniumLogin(WebDriver driver, UsuarioLoginQueroMaisCredito usuario) {
        WebDriverWait wait = webDriverService.criarWait(driver);
        try {
            driver.get(BASE_URL);

            WebElement userField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("EUsuario_CAMPO")));
            WebElement passwordField = driver.findElement(By.id("ESenha_CAMPO"));
            WebElement loginButton = driver.findElement(By.id("lnkEntrar"));

            userField.sendKeys(usuario.getUsername());
            passwordField.sendKeys(usuario.getPassword());
            loginButton.click();

            Thread.sleep(3000);
            CrawlerUtils.interagirComAlert(driver);

            WebElement negociacaoDiv = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'NEGOCIAÇÃO')]")));
            negociacaoDiv.click();

            WebElement autorizadorLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='submenu-item']//a[contains(text(),'Autorizador')]")));
            autorizadorLink.click();

            wait.until(ExpectedConditions.urlContains("WebAutorizador"));

            return true;
        } catch (Exception e) {
           log.error("Não foi possível fazer login. Erro: " + e.getMessage().substring(0, e.getMessage().indexOf("\n")));
            return false;
        }
    }

}
