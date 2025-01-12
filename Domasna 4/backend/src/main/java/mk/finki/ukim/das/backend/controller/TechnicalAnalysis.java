package mk.finki.ukim.das.backend.controller;

import com.google.gson.Gson;
import mk.finki.ukim.das.backend.functions.functions;
import mk.finki.ukim.das.backend.model.Issuer;
import mk.finki.ukim.das.backend.model.Signals;
import mk.finki.ukim.das.backend.service.IssuerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/technical")
public class TechnicalAnalysis {

    public final IssuerService issuerService;

    public TechnicalAnalysis(IssuerService issuerService) {
        this.issuerService = issuerService;
    }


    @GetMapping("/{code}")
    public String getIssuer(@PathVariable String code, @RequestParam(name = "period",required = false) String periodStr) {
        Issuer issuer = issuerService.getIssuer(code);
        int period = (periodStr != null)?Integer.parseInt(periodStr):1;

        List<Double> prices = functions.getPrices(issuer);
        List<Double> minPrices = functions.getMin(issuer);
        List<Double> maxPrices = functions.getMax(issuer);

        Signals signals = new Signals();

        signals.setRSI(functions.calculateRSI(prices,period));
        signals.setMACD(functions.calculateMACD(prices, 12, 26, 9));
        signals.setStochastic(functions.calculateStochastic(prices,maxPrices,minPrices,period));
        signals.setCCI(functions.calculateCCI(prices,maxPrices,minPrices,period));
        signals.setMomentum(functions.calculateMomentum(prices,period));
        signals.setSMA(functions.calculateSMA(prices,period));
        signals.setEMA(functions.calculateEMA(prices,period));
        signals.setWMA(functions.calculateWMA(prices,period));
        signals.setHMA(functions.calculateHMA(prices,period));
        signals.setEnvelopes(functions.calculateEnvelopes(prices,period,2));

        String RSISignal = functions.getRSISignal(signals.getRSI());
        String MACDSignal = functions.getMACDSignal(signals.getMACD());
        String stochasticSignal = functions.getStochasticSignal(signals.getStochastic());
        String cciSignal = functions.getCCISignal(signals.getCCI());
        String MomentumSignal = functions.getMomentumSignal(signals.getMomentum());

        Double lastPrice = functions.getLastPrice(issuer);

        String SMASignal = functions.getSMASignal(lastPrice,signals.getSMA());
        String EMASignal = functions.getEMASignal(lastPrice,signals.getEMA());
        String WMASignal = functions.getWMASignal(lastPrice,signals.getWMA());
        String HMASignal = functions.getHMASignal(lastPrice,signals.getHMA());
        String MAESignal = functions.getMAESignal(lastPrice,signals.getEnvelopes());

        String signalOsc = functions.combineSignals(RSISignal, MACDSignal, stochasticSignal, cciSignal, MomentumSignal);

        String signalMA = functions.combineSignals(SMASignal, EMASignal, WMASignal, HMASignal, MAESignal);

        signals.setSignalOsc(signalOsc);
        signals.setSignalMA(signalMA);

        return new Gson().newBuilder().serializeNulls().create().toJson(signals);
    }

}
