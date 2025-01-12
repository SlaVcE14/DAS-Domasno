package mk.ukim.finki;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Issuer {

    String code;
    List<StockData> stockDataList = new ArrayList<>();

    public Issuer(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<StockData> getStockDataList() {
        return stockDataList;
    }

    public void setStockDataList(List<StockData> stockDataList) {
        this.stockDataList = stockDataList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issuer issuer = (Issuer) o;
        return Objects.equals(code, issuer.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
