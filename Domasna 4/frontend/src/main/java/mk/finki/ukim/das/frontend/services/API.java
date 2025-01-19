package mk.finki.ukim.das.frontend.services;

import mk.finki.ukim.das.frontend.model.Signals;
import mk.finki.ukim.das.frontend.model.StockData;

import java.util.List;

public interface API {
    /** Returns list of codes for all issuers */
    List<String> getIssuers();
    /** Returns stock data for a issuer */
    StockData[] getIssuer(String code, String fromDate, String toDate);
    /** Returns the calculated signals for the issuer */
    Signals getSignals(String code,int period);
    /** Returns the last price for the issuer */
    Double getLastPrice(String code);
}
