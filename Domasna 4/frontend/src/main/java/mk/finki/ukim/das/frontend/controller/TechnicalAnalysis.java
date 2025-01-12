package mk.finki.ukim.das.frontend.controller;

import mk.finki.ukim.das.frontend.model.Signals;
import mk.finki.ukim.das.frontend.services.API;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/technical")
public class TechnicalAnalysis {
    public final API api;

    public TechnicalAnalysis(API api) {
        this.api = api;
    }


    @GetMapping("/{code}")
    public String getIssuer(@PathVariable String code, @RequestParam(name = "period",required = false) String periodStr, Model model) {

        model.addAttribute("issuers",api.getIssuers());
        model.addAttribute("selectedIssuer",code);

        int period = (periodStr != null)?Integer.parseInt(periodStr):1;

        Signals signals = api.getSignals(code, period);

        Double RSI = signals.getRSI();
        model.addAttribute("RSI", RSI != null? RSI : "Not Available");

        Double[] MACD = signals.getMACD();

        if (MACD == null || MACD[0] == null || MACD[1] == null) {
            model.addAttribute("MACD", "Not Available");
        }else model.addAttribute("MACD", "macdLine: " + MACD[0] + ", signalLine: " + MACD[1] + ", " + (MACD[0] > MACD[1]?"Buy":MACD[0] < MACD[1]?"Sell":"Hold"));

        Double Stochastic = signals.getStochastic();
        model.addAttribute("Stochastic", Stochastic != null? Stochastic: "Not Available");

        Double CCI = signals.getCCI();
        model.addAttribute("CCI", CCI != null? CCI: "Not Available");

        Double Momentum = signals.getMomentum();
        model.addAttribute("Momentum", Momentum != null? Momentum : "Not Available");

        Double SMA = signals.getSMA();
        model.addAttribute("SMA", SMA != null? SMA : "Not Available");

        Double EMA = signals.getEMA();
        model.addAttribute("EMA", EMA != null? EMA : "Not Available");

        Double WMA = signals.getWMA();
        model.addAttribute("WMA", WMA != null? WMA : "Not Available");

        Double HMA = signals.getHMA();
        model.addAttribute("HMA", HMA != null? HMA : "Not Available");

        Double[] Envelopes = signals.getEnvelopes();
        if (Envelopes == null) {
            model.addAttribute("Envelopes", "Not Available");
        }else {
            double upperEnvelope = Envelopes[0];
            double lowerEnvelope = Envelopes[1];
            Double lastPrice = api.getLastPrice(code);
            if (lastPrice == null) {
                model.addAttribute("Envelopes", "Not available");
            }else
                model.addAttribute("Envelopes", "upperEnvelope: " + upperEnvelope + ", lowerEnvelope: " + lowerEnvelope + ", " + (lastPrice > upperEnvelope?"Sell":lastPrice < lowerEnvelope?"Buy":"Hold"));
        }

        model.addAttribute("oscSignal", signals.getSignalOsc());
        model.addAttribute("maSignal", signals.getSignalMA());
        model.addAttribute("period",period);

        return "technical";
    }

}
