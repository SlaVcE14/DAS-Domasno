package mk.finki.ukim.das.stock.functions;

import mk.finki.ukim.das.stock.model.Issuer;
import mk.finki.ukim.das.stock.model.StockData;

import java.util.ArrayList;
import java.util.List;

public class functions {

    public static double calculateOscillation(double currentPrice, double previousPrice) {
        if (previousPrice == 0) return 0;
        return ((currentPrice - previousPrice) / previousPrice) * 100;
    }


    public static List<Double> getPrices(Issuer issuer){
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

    //TODO refactor this!!

    public static List<Double> getMin(Issuer issuer){
        List<Double> prices = new ArrayList<>();
        for (StockData data : issuer.getStockDataList()){
            String price = data.getMinPrice();
            if (price.isEmpty()) {
                price = data.getLastTransactionPrice();
            }
            if (price == null || price.isEmpty()) {
                continue;
            }

            price = price.replaceAll("\\.","").replaceAll(",",".");

            prices.add(Double.valueOf(price));
        }
        return prices;
    }

    public static List<Double> getMax(Issuer issuer){
        List<Double> prices = new ArrayList<>();
        for (StockData data : issuer.getStockDataList()){
            String price = data.getMaxPrice();
            if (price.isEmpty()) {
                price = data.getLastTransactionPrice();
            }
            if (price == null || price.isEmpty()) {
                continue;
            }

            price = price.replaceAll("\\.","").replaceAll(",",".");

            prices.add(Double.valueOf(price));
        }
        return prices;
    }

    public static Double getLastPrice(Issuer issuer){
        List<Double> prices = getPrices(issuer);
        if (prices.isEmpty()) return null;
        return prices.get(0);
    }


    //RSI
    public static Double calculateRSI(List<Double> closingPrices, int period) {
        double gain = 0, loss = 0;
        if (period > closingPrices.size()) {
            return null;
        }
        for (int i = 1; i <= period; i++) {
            double change = closingPrices.get(i) - closingPrices.get(i - 1);
            if (change > 0) gain += change;
            else loss -= change;
        }
        double averageGain = gain / period;
        double averageLoss = loss / period;
        if (averageLoss == 0) return 100.0; // No losses means RSI is 100
        double rs = averageGain / averageLoss;
        return 100 - (100 / (1 + rs));
    }



    //MACD TODO
    public static Double[] calculateMACD(List<Double> closingPrices, int shortPeriod, int longPeriod, int signalPeriod) {
        Double shortEMA = calculateEMA(closingPrices, shortPeriod);
        Double longEMA = calculateEMA(closingPrices, longPeriod);
        if (shortEMA == null || longEMA == null) return null;

        double macdLine = shortEMA - longEMA;

        // Generate MACD Signal Line using EMA of MACD Line
        List<Double> macdLineValues = new ArrayList<>();
        for (int i = longPeriod - 1; i < closingPrices.size(); i++) {
            macdLineValues.add(calculateEMA(closingPrices.subList(i - shortPeriod + 1, i + 1), signalPeriod));
        }
        Double signalLine = calculateEMA(macdLineValues, signalPeriod);

        return new Double[]{macdLine, signalLine};
    }




    //Stochastic Oscillator TODO
    public static Double calculateStochastic(List<Double> closingPrices, List<Double> highs, List<Double> lows, int period) {
        if (closingPrices.size() < period || highs.size() < period || lows.size() < period)
//            throw new IllegalArgumentException("Not enough data");
            return null;


        double currentClose = closingPrices.get(closingPrices.size() - 1);
        double highestHigh = highs.subList(highs.size() - period, highs.size())
                .stream().max(Double::compare).get();
        double lowestLow = lows.subList(lows.size() - period, lows.size())
                .stream().min(Double::compare).get();

        return ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100;
    }


    //CCI TODO
    public static Double calculateCCI(List<Double> closingPrices, List<Double> highs, List<Double> lows, int period) {
        if (closingPrices.size() < period || highs.size() < period || lows.size() < period)
            return null;

        // Calculate the Typical Price (TP) for each period
        List<Double> typicalPrices = new ArrayList<>();
        for (int i = 0; i < closingPrices.size(); i++) {
            double tp = (highs.get(i) + lows.get(i) + closingPrices.get(i)) / 3;
            typicalPrices.add(tp);
        }

        // Calculate the Simple Moving Average (SMA) of the Typical Prices
        List<Double> smaValues = new ArrayList<>();
        for (int i = 0; i <= typicalPrices.size() - period; i++) {
            double sum = 0;
            for (int j = i; j < i + period; j++) {
                sum += typicalPrices.get(j);
            }
            smaValues.add(sum / period);
        }

        // Calculate the Mean Deviation
        double lastSMA = smaValues.get(smaValues.size() - 1); // Latest SMA value
        double meanDeviation = 0;
        for (int i = typicalPrices.size() - period; i < typicalPrices.size(); i++) {
            meanDeviation += Math.abs(typicalPrices.get(i) - lastSMA);
        }
        meanDeviation /= period;

        // Calculate the CCI
        double lastTypicalPrice = typicalPrices.get(typicalPrices.size() - 1); // Latest Typical Price

        return (lastTypicalPrice - lastSMA) / (0.015 * meanDeviation);
    }


    //Momentum Indicator TODO
    public static Double calculateMomentum(List<Double> closingPrices, int period) {
        if (closingPrices.size() < period) return null; //throw new IllegalArgumentException("Not enough data");
        return closingPrices.get(closingPrices.size() - 1) - closingPrices.get(closingPrices.size() - period - 1);
    }


    //SMA
    public static Double calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) return null;//throw new IllegalArgumentException("Not enough data");
        double sum = 0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / period;
    }


    //EMA
    public static Double calculateEMA(List<Double> prices, int period) {
        if (prices.size() < period) return null;// throw new IllegalArgumentException("Not enough data");
        double smoothing = 2.0 / (period + 1);
        double ema = prices.get(prices.size() - period); // Start with the first SMA
        for (int i = prices.size() - period + 1; i < prices.size(); i++) {
            ema = (prices.get(i) - ema) * smoothing + ema;
        }
        return ema;
    }


    //WMA TODO
    public static Double calculateWMA(List<Double> prices, int period) {
        int weightSum = (period * (period + 1)) / 2;
        double wma = 0;
        if (prices.size() < period) {
            return null;
        }
        for (int i = 0; i < period; i++) {
            wma += prices.get(prices.size() - period + i) * (i + 1);
        }
        return wma / weightSum;
    }


    //Hull Moving Average (HMA) TODO
    public static Double calculateHMA(List<Double> prices, int period) {
        List<Double> halfWMA = new ArrayList<>();
        if (prices.size() < period) {
            return null;
        }
        for (int i = period / 2; i < prices.size(); i++) {
            halfWMA.add(calculateWMA(prices.subList(i - period / 2, i), period / 2));
        }

        List<Double> fullWMA = new ArrayList<>();
        for (int i = period; i < prices.size(); i++) {
            fullWMA.add(calculateWMA(prices.subList(i - period, i), period));
        }

        double weightedHMA = 2 * halfWMA.get(halfWMA.size() - 1) - fullWMA.get(fullWMA.size() - 1);
        return Math.sqrt(weightedHMA);
    }

    //Moving Average Envelopes TODO
    public static Double[] calculateEnvelopes(List<Double> prices, int period, double percentage) {
        Double sma = calculateSMA(prices, period);
        if (sma == null) return null;
        double upperEnvelope = sma * (1 + percentage / 100);
        double lowerEnvelope = sma * (1 - percentage / 100);
        return new Double[]{upperEnvelope, lowerEnvelope};
    }


    public static String getRSISignal(Double rsi) {
        if (rsi == null) return null;
        if (rsi > 70) return "Sell";
        if (rsi < 30) return "Buy";
        return "Hold";
    }

    public static String getMACDSignal(Double[] macd) {
        if (macd == null || macd[0] == null || macd[1] == null)
            return null;
        if (macd[0] > macd[1]) return "Buy";
        if (macd[0] < macd[1]) return "Sell";
        return "Hold";
    }

    public static String getStochasticSignal(Double stochastic) {
        if (stochastic == null) return null;
        if (stochastic > 80) return "Sell";
        if (stochastic < 20) return "Buy";
        return "Hold";
    }

    public static String getCCISignal(Double cci) {
        if (cci == null) return null;
        if (cci > 100) return "Sell";
        if (cci < -100) return "Buy";
        return "Hold";
    }

    public static String getMomentumSignal(Double momentum) {
        if (momentum == null) return null;
        if (momentum > 0) return "Buy";
        if (momentum < 0) return "Sell";
        return "Hold";
    }


    public static String getSMASignal(Double lastPrice, Double sma) {
        if (lastPrice == null || sma == null) return null;
        if (lastPrice > sma) return "Buy";
        if (lastPrice < sma) return "Sell";
        return "Hold";
    }

    public static String getEMASignal(Double lastPrice, Double ema) {
        if (lastPrice == null || ema == null) return null;
        if (lastPrice > ema) return "Buy";
        if (lastPrice < ema) return "Sell";
        return "Hold";
    }

    public static String getWMASignal(Double lastPrice, Double wma) {
        if (lastPrice == null || wma == null) return null;
        if (lastPrice > wma) return "Buy";
        if (lastPrice < wma) return "Sell";
        return "Hold";
    }


    public static String getHMASignal(Double lastPrice, Double hma) {
        if (lastPrice == null || hma == null) return null;
        if (lastPrice > hma) return "Buy";
        if (lastPrice < hma) return "Sell";
        return "Hold";
    }

    public static String getMAESignal(Double lastPrice, Double[] mae) {
        if (lastPrice == null || mae == null) return null;
        return (lastPrice > mae[0]?"Sell":lastPrice < mae[1]?"Buy":"Hold");
    }

    public static String combineSignals(String... signals) {
        int buyCount = 0, sellCount = 0, holdCount = 0;
        for (String signal : signals) {
            if (signal == null) continue;
            switch (signal) {
                case "Buy": buyCount++; break;
                case "Sell": sellCount++; break;
                case "Hold": holdCount++; break;
            }
        }
        if (buyCount > sellCount && buyCount > holdCount) return "Buy";
        if (sellCount > buyCount && sellCount > holdCount) return "Sell";
        return "Hold";
    }


}
