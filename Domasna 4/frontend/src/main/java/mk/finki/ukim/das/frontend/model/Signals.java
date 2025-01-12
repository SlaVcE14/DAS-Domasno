package mk.finki.ukim.das.frontend.model;

import java.util.Arrays;

public class Signals {
    Double RSI;
    Double[] MACD;
    Double Stochastic;
    Double CCI;
    Double Momentum;
    Double SMA;
    Double EMA;
    Double WMA;
    Double HMA;
    Double[] Envelopes;

    String signalOsc;
    String signalMA;


    public Double getRSI() {
        return RSI;
    }

    public void setRSI(Double RSI) {
        this.RSI = RSI;
    }

    public Double[] getMACD() {
        return MACD;
    }

    public void setMACD(Double[] MACD) {
        this.MACD = MACD;
    }

    public Double getStochastic() {
        return Stochastic;
    }

    public void setStochastic(Double stochastic) {
        Stochastic = stochastic;
    }

    public Double getCCI() {
        return CCI;
    }

    public void setCCI(Double CCI) {
        this.CCI = CCI;
    }

    public Double getMomentum() {
        return Momentum;
    }

    public void setMomentum(Double momentum) {
        Momentum = momentum;
    }

    public Double getSMA() {
        return SMA;
    }

    public void setSMA(Double SMA) {
        this.SMA = SMA;
    }

    public Double getEMA() {
        return EMA;
    }

    public void setEMA(Double EMA) {
        this.EMA = EMA;
    }

    public Double getWMA() {
        return WMA;
    }

    public void setWMA(Double WMA) {
        this.WMA = WMA;
    }

    public Double getHMA() {
        return HMA;
    }

    public void setHMA(Double HMA) {
        this.HMA = HMA;
    }

    public Double[] getEnvelopes() {
        return Envelopes;
    }

    public void setEnvelopes(Double[] envelopes) {
        Envelopes = envelopes;
    }

    public String getSignalOsc() {
        return signalOsc;
    }

    public void setSignalOsc(String signalOsc) {
        this.signalOsc = signalOsc;
    }

    public String getSignalMA() {
        return signalMA;
    }

    public void setSignalMA(String signalMA) {
        this.signalMA = signalMA;
    }

    @Override
    public String toString() {
        return "Signals{" +
                "RSI=" + RSI +
                ", MACD=" + Arrays.toString(MACD) +
                ", Stochastic=" + Stochastic +
                ", CCI=" + CCI +
                ", Momentum=" + Momentum +
                ", SMA=" + SMA +
                ", EMA=" + EMA +
                ", WMA=" + WMA +
                ", HMA=" + HMA +
                ", Envelopes=" + Arrays.toString(Envelopes) +
                ", signalOsc='" + signalOsc + '\'' +
                ", signalMA='" + signalMA + '\'' +
                '}';
    }
}
