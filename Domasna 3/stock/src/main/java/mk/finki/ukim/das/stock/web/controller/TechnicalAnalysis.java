package mk.finki.ukim.das.stock.web.controller;

import mk.finki.ukim.das.stock.functions.functions;
import mk.finki.ukim.das.stock.model.Issuer;
import mk.finki.ukim.das.stock.service.IssuerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/technical")
public class TechnicalAnalysis {

    public final IssuerService issuerService;

    public TechnicalAnalysis(IssuerService issuerService) {
        this.issuerService = issuerService;
    }


    @GetMapping("/{code}")
    public String getIssuer(@PathVariable String code, @RequestParam(name = "period",required = false) String periodStr, Model model) {

        model.addAttribute("issuers",issuerService.getIssuers());
        Issuer issuer = issuerService.getIssuer(code);
        model.addAttribute("selectedIssuer",issuer);

        int period = (periodStr != null)?Integer.parseInt(periodStr):1;

        List<Double> prices = functions.getPrices(issuer);
        List<Double> minPrices = functions.getMin(issuer);
        List<Double> maxPrices = functions.getMax(issuer);

        Double RSI = functions.calculateRSI(prices,period);
        model.addAttribute("RSI", RSI != null? RSI : "Not Available");

        Double[] MACD = functions.calculateMACD(prices, 12, 26, 9); //TODO check

        if (MACD == null || MACD[0] == null || MACD[1] == null) {
            model.addAttribute("MACD", "Not Available");
        }else model.addAttribute("MACD", "macdLine: " + MACD[0] + ", signalLine: " + MACD[1] + ", " + (MACD[0] > MACD[1]?"Buy":MACD[0] < MACD[1]?"Sell":"Hold"));

        Double Stochastic = functions.calculateStochastic(prices,maxPrices,minPrices,period);
        model.addAttribute("Stochastic", Stochastic != null? Stochastic: "Not Available");

        Double CCI = functions.calculateCCI(prices,maxPrices,minPrices,period);
        model.addAttribute("CCI", CCI != null? CCI: "Not Available");

        Double Momentum = functions.calculateMomentum(prices,period);
        model.addAttribute("Momentum", Momentum != null? Momentum : "Not Available");

        Double SMA = functions.calculateSMA(prices,period);
        model.addAttribute("SMA", SMA != null? SMA : "Not Available");

        Double EMA = functions.calculateEMA(prices,period);
        model.addAttribute("EMA", EMA != null? EMA : "Not Available");

        Double WMA = functions.calculateWMA(prices,period);
        model.addAttribute("WMA", WMA != null? WMA : "Not Available");

        Double HMA = functions.calculateHMA(prices,period);
        model.addAttribute("HMA", HMA != null? HMA : "Not Available");

        Double[] Envelopes = functions.calculateEnvelopes(prices,period,50); //TODO check
        if (Envelopes == null) {
            model.addAttribute("Envelopes", "Not Available");
        }else {
            double upperEnvelope = Envelopes[0];
            double lowerEnvelope = Envelopes[1];
            Double lastPrice = functions.getLastPrice(issuer);
            if (lastPrice == null) {
                model.addAttribute("Envelopes", "Not available");
            }else
                model.addAttribute("Envelopes", "upperEnvelope: " + upperEnvelope + ", lowerEnvelope: " + lowerEnvelope + ", " + (lastPrice > upperEnvelope?"Sell":lastPrice < lowerEnvelope?"Buy":"Hold"));
        }


        String RSISignal = functions.getRSISignal(RSI);
        String MACDSignal = functions.getMACDSignal(MACD);
        String stochasticSignal = functions.getStochasticSignal(Stochastic);
        String cciSignal = functions.getCCISignal(CCI);
        String MomentumSignal = functions.getMomentumSignal(Momentum);

        Double lastPrice = functions.getLastPrice(issuer);

        String SMASignal = functions.getSMASignal(lastPrice,SMA);
        String EMASignal = functions.getEMASignal(lastPrice,EMA);
        String WMASignal = functions.getWMASignal(lastPrice,WMA);
        String HMASignal = functions.getHMASignal(lastPrice,HMA);
        String MAESignal = functions.getMAESignal(lastPrice,Envelopes);

        String signalSoc = functions.combineSignals(RSISignal, MACDSignal, stochasticSignal, cciSignal, MomentumSignal);

        String signalMA = functions.combineSignals(SMASignal, EMASignal, WMASignal, HMASignal, MAESignal);

        model.addAttribute("oscSignal", signalSoc);
        model.addAttribute("maSignal", signalMA);
        model.addAttribute("period",period);

        return "technical";
    }

}
