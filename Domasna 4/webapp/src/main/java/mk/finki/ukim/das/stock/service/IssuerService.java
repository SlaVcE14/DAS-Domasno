package mk.finki.ukim.das.stock.service;

import mk.finki.ukim.das.stock.model.Issuer;
import mk.finki.ukim.das.stock.model.StockData;

import java.util.List;

public interface IssuerService {
    List<Issuer> getIssuers();
    Issuer getIssuer(String code);
    List<StockData> getStockData(Issuer issuer, String from, String to);
}
