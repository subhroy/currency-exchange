package com.currency.exchange.ecb;

import com.currency.exchange.exceptions.CurrencyExchangeException;
import com.currency.exchange.model.Currency;
import com.currency.exchange.model.ExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class ECBRateExtractor {

  @Value("${exchange.currency.base}")
  private String baseCurrency;

  @Value("${exchange.baseURI}")
  private String baseUri;

  public Set<ExchangeRate> fetchExchangeRates() {
    log.info("Initiating exchange rates update");
    Document doc;
    try {
      doc = Jsoup.connect(baseUri + "index.en.html").get();
      Elements rateRows = doc.select("table.forextable > tbody > tr");
      Set<ExchangeRate> exchangeRates = new HashSet<>();
      if (rateRows.size() > 0) {
        rateRows.forEach(
            element -> {
              var rate = extractRateFromRow(element);
              exchangeRates.add(rate);
            });
      }
      log.info("Exchange Rates updated");
      return exchangeRates;
    } catch (IOException e) {
      log.error("Failed to fetch exchange rates.", e);
      throw new CurrencyExchangeException("Failed to parse Exchange rate page.", e);
    }
  }

  private ExchangeRate extractRateFromRow(Element elementRow) {
    Elements columns = elementRow.select("td");
    String cur = null;
    String desc = "";
    String spot = "";
    String url = "";

    if (columns.size() > 0) {
      cur = columns.get(0).select("a").text();
      desc = columns.get(1).select("a").text();
      spot = columns.get(2).select("span").text().trim();
      url = columns.get(1).select("a").attr("href");
    }
    var spotVal = BigDecimal.valueOf(Double.parseDouble(spot));
    var currency = Currency.from(cur, spotVal, desc);
    return ExchangeRate.builder()
        .currency(currency)
        .pageRef(baseUri + url)
        .baseCurrency(baseCurrency)
        .build();
  }
}
