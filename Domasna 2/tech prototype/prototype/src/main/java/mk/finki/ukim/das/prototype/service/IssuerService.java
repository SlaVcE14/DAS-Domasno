package mk.finki.ukim.das.prototype.service;

import mk.finki.ukim.das.prototype.model.Issuer;
import mk.finki.ukim.das.prototype.model.StockData;

import java.util.List;

public interface IssuerService {
    List<Issuer> getIssuers();
    Issuer getIssuer(String code);
    List<StockData> getStockData(Issuer issuer, String from, String to);
}
