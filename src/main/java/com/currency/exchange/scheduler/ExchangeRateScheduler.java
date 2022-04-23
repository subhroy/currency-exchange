package com.currency.exchange.scheduler;

import com.currency.exchange.ecb.ECBRateExtractor;
import com.currency.exchange.service.ExchangeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class ExchangeRateScheduler {
  private final ECBRateExtractor rateExtractor;
  private final ExchangeStorageService exchangeStorageService;


  @Scheduled(fixedRateString = "${exchange.scheduler.interval}")
  public void fetchExchangeRates() {
    var rates = rateExtractor.fetchExchangeRates();
    exchangeStorageService.updateExchangeRates(rates);
  }
}
