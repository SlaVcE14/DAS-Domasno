package mk.ukim.finki;

import java.util.Objects;

public class StockData {
    private String date;
    private String lastTransactionPrice;
    private String maxPrice;
    private String minPrice;
    private String averagePrice;
    private String percentageChange;
    private String quantity;
    private String turnoverBest;
    private String totalTurnover;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastTransactionPrice() {
        return lastTransactionPrice;
    }

    public void setLastTransactionPrice(String lastTransactionPrice) {
        this.lastTransactionPrice = lastTransactionPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(String percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTurnoverBest() {
        return turnoverBest;
    }

    public void setTurnoverBest(String turnoverBest) {
        this.turnoverBest = turnoverBest;
    }

    public String getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(String totalTurnover) {
        this.totalTurnover = totalTurnover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockData stockData = (StockData) o;
        return Objects.equals(date, stockData.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }

    @Override
    public String toString() {
        return "StockData{" +
                "date='" + date + '\'' +
                ", lastTransactionPrice='" + lastTransactionPrice + '\'' +
                ", maxPrice='" + maxPrice + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", averagePrice='" + averagePrice + '\'' +
                ", percentageChange='" + percentageChange + '\'' +
                ", quantity='" + quantity + '\'' +
                ", turnoverBest='" + turnoverBest + '\'' +
                ", totalTurnover='" + totalTurnover + '\'' +
                '}';
    }
}
