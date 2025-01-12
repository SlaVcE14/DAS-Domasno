package mk.finki.ukim.das.frontend.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Issuer {
    String code;
    List<StockData> stockDataList = new ArrayList<>();

    public Issuer(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public List<StockData> getStockDataList() {
        return stockDataList;
    }
}

