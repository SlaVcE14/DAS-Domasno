package mk.finki.ukim.das.backend.service.impl;

import mk.finki.ukim.das.backend.model.Issuer;
import mk.finki.ukim.das.backend.model.StockData;
import mk.finki.ukim.das.backend.repository.IssuerRepository;
import mk.finki.ukim.das.backend.service.IssuerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IssuerServiceImpl implements IssuerService {

    private final IssuerRepository issuerRepository;

    public IssuerServiceImpl(IssuerRepository issuerRepository) {
        this.issuerRepository = issuerRepository;
    }

    @Override
    public List<String> getIssuers() {
        return issuerRepository.getIssuers();
    }

    @Override
    public Issuer getIssuer(String code) {
        return issuerRepository.getIssuer(code);
    }

    @Override
    public List<StockData> getStockData(Issuer issuer, String from, String to) {
        return issuerRepository.getStockData(issuer, from, to);
    }

    @Override
    public List<Double> getPrices(Issuer issuer) {
        List<Double> prices = new ArrayList<>();
        for (StockData data : issuer.getStockDataList()){
            String price = data.getLastTransactionPrice();
            if (price == null || price.isEmpty()) {
                continue;
            }

            price = price.replaceAll("\\.","").replaceAll(",",".");

            prices.add(Double.valueOf(price));
        }
        return prices;
    }

    @Override
    public List<Double> getMin(Issuer issuer) {
        List<Double> prices = new ArrayList<>();
        for (StockData data : issuer.getStockDataList()){
            String price = data.getMinPrice();
            getPrice(prices, data, price);
        }
        return prices;
    }

    @Override
    public List<Double> getMax(Issuer issuer) {
        List<Double> prices = new ArrayList<>();
        for (StockData data : issuer.getStockDataList()){
            String price = data.getMaxPrice();
            getPrice(prices, data, price);
        }
        return prices;
    }

    @Override
    public Double getLastPrice(Issuer issuer) {
        List<Double> prices = getPrices(issuer);
        if (prices.isEmpty()) return null;
        return prices.get(0);
    }

    private static void getPrice(List<Double> prices, StockData data, String price) {
        if (price.isEmpty()) {
            price = data.getLastTransactionPrice();
        }
        if (price == null || price.isEmpty()) {
            return;
        }

        price = price.replaceAll("\\.","").replaceAll(",",".");

        prices.add(Double.valueOf(price));
    }
}
