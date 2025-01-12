package mk.finki.ukim.das.frontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mk.finki.ukim.das.frontend.model.Signals;
import mk.finki.ukim.das.frontend.model.StockData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;

import java.util.List;

@Service
public class API {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "http://localhost:8080/api/";


    public List<String> getIssuers(){
        String data = restTemplate.getForObject(apiUrl + "/issuers",String.class);

        Type listType = new TypeToken<List<String>>(){}.getType();

        return new Gson().fromJson(data, listType);
    }

    public StockData[] getIssuer(String code, String fromDate, String toDate) {
        String url = apiUrl + "/issuers/" + code + "?fromDate=" + fromDate + "&toDate=" + toDate;
        String data = restTemplate.getForObject(url,String.class);

        return new Gson().fromJson(data,StockData[].class);
    }

    public Signals getSignals(String code,int period){
        String url = apiUrl + "/technical/" + code + "?period=" + period;
        String data = restTemplate.getForObject(url,String.class);

        return new Gson().fromJson(data,Signals.class);
    }

    public Double getLastPrice(String code){
        String url = apiUrl + "/issuers/" + code + "/lastPrice";
        String data = restTemplate.getForObject(url,String.class);

        return new Gson().fromJson(data,Double.class);
    }

}
