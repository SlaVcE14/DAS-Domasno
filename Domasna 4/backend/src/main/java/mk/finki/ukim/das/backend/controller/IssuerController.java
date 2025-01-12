package mk.finki.ukim.das.backend.controller;

import com.google.gson.Gson;
import mk.finki.ukim.das.backend.model.Issuer;
import mk.finki.ukim.das.backend.service.IssuerService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/issuers")
public class IssuerController {
     public final IssuerService issuerService;

    public IssuerController(IssuerService issuerService) {
        this.issuerService = issuerService;
    }

    @GetMapping
    public String getIssuer() {
        return new Gson().toJson(issuerService.getIssuers());
    }

    @GetMapping("/{code}")
    public String getIssuer(@PathVariable String code,
                            @RequestParam(required = false, name = "fromDate") String fromDateParam,
                            @RequestParam(required = false,name = "toDate") String toDateParam) {

        if (toDateParam == null || toDateParam.isEmpty()) {
            LocalDateTime toDate = LocalDateTime.now();
            toDateParam = String.format("%d.%d.%d",toDate.getDayOfMonth(),toDate.getMonthValue(),toDate.getYear());
            if (fromDateParam == null || fromDateParam.isEmpty()) {
                fromDateParam = String.format("%02d.%02d.%d",toDate.minusYears(1).getDayOfMonth(),toDate.minusYears(1).getMonthValue(),toDate.minusYears(1).getYear());
            }
        }
        if (fromDateParam == null || fromDateParam.isEmpty()) {
            LocalDateTime toDate = LocalDateTime.now().minusYears(1);
            String fromDateString = String.format("%d.%d.%d",toDate.getDayOfMonth(),toDate.getMonthValue(),toDate.getYear());
            fromDateParam = fromDateString;
        }

        Issuer issuer = issuerService.getIssuer(code);
        return new Gson().toJson(issuerService.getStockData(issuer, fromDateParam, toDateParam));
    }

}
