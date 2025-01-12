package mk.finki.ukim.das.backend.repository;

import mk.finki.ukim.das.backend.database.FileSystem;
import mk.finki.ukim.das.backend.model.Issuer;
import mk.finki.ukim.das.backend.model.StockData;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IssuerRepository {

    List<Issuer> issuers;

    public IssuerRepository() {
        issuers = FileSystem.ReadData();
    }


    public List<String> getIssuers() {
        return issuers.stream().map(Issuer::getCode).toList();
    }

    public Issuer getIssuer(String code){
        return issuers.stream().filter(issuer -> issuer.getCode().equals(code)).findFirst().orElse(null);
    }

    public List<StockData> getStockData(Issuer issuer, String from, String to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

        LocalDate startDate = LocalDate.parse(from, formatter);
        LocalDate endDate = LocalDate.parse(to, formatter);

        if (issuer == null)
            return new ArrayList<>();

        return issuer.getStockDataList().stream().filter(stockData -> {
            LocalDate objDate = LocalDate.parse(stockData.getDate(), formatter);
            return !objDate.isBefore(startDate) && !objDate.isAfter(endDate);

        }).toList();
    }

}
