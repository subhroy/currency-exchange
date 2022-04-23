package com.currency.exchange;

import com.currency.exchange.ecb.ECBRateExtractor;
import com.currency.exchange.model.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class ECBExtractorTest {
  private ECBRateExtractor rateExtractor = new ECBRateExtractor();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        rateExtractor,
        "baseUri",
        "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/");
    ReflectionTestUtils.setField(rateExtractor, "baseCurrency", "EUR");
  }

  @Test
  void shouldFetchExchangeRates() {
    var exchangeRates = rateExtractor.fetchExchangeRates();
    assertThat(exchangeRates).hasSizeGreaterThanOrEqualTo(31);
  }

  @Test
  void shouldSetFetchExchangeRates() {
    var exchangeRates = rateExtractor.fetchExchangeRates();

    assertThat(exchangeRates).extracting("baseCurrency","pageRef");
  }
}
