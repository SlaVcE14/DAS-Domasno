package mk.finki.ukim.das.prototype.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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

}
