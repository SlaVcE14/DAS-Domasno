package mk.finki.ukim.das.backend.service;

import mk.finki.ukim.das.backend.model.Issuer;
import mk.finki.ukim.das.backend.model.StockData;

import java.util.List;

public interface IssuerService {
    List<String> getIssuers();
    Issuer getIssuer(String code);
    List<StockData> getStockData(Issuer issuer, String from, String to);
    List<Double> getPrices(Issuer issuer);
    List<Double> getMin(Issuer issuer);
    List<Double> getMax(Issuer issuer);
    Double getLastPrice(Issuer issuer);
}
