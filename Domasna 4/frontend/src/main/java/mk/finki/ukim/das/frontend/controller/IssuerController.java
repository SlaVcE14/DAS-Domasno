package mk.finki.ukim.das.frontend.controller;

import mk.finki.ukim.das.frontend.model.StockData;
import mk.finki.ukim.das.frontend.services.API;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;


@Controller
@RequestMapping("/")
public class IssuerController {

    public final API api;

    public IssuerController(API api) {
        this.api = api;
    }

    @GetMapping
    public String getIssuer(Model model){
        model.addAttribute("issuers",api.getIssuers());
        return "issuer";
    }

    @GetMapping("/{code}")
    public String getIssuer(@PathVariable String code,
                            @RequestParam(required = false, name = "fromDate") String fromDateParam,
                            @RequestParam(required = false,name = "toDate") String toDateParam,
                            Model model) {

        if (toDateParam == null || toDateParam.isEmpty()) {
            LocalDateTime toDate = LocalDateTime.now();
            String toDateString = String.format("%d.%d.%d",toDate.getDayOfMonth(),toDate.getMonthValue(),toDate.getYear());
            model.addAttribute("toDate",toDateString);
            toDateParam = toDateString;
            if (fromDateParam == null || fromDateParam.isEmpty()) {
                String fromDateString = String.format("%02d.%02d.%d",toDate.minusYears(1).getDayOfMonth(),toDate.minusYears(1).getMonthValue(),toDate.minusYears(1).getYear());
                model.addAttribute("fromDate",fromDateString);
                fromDateParam = fromDateString;
            }else model.addAttribute("fromDate",fromDateParam);

        }else model.addAttribute("toDate",toDateParam);

        if (fromDateParam == null || fromDateParam.isEmpty()) {
            LocalDateTime toDate = LocalDateTime.now().minusYears(1);
            String fromDateString = String.format("%d.%d.%d",toDate.getDayOfMonth(),toDate.getMonthValue(),toDate.getYear());
            model.addAttribute("fromDate",fromDateString);
            fromDateParam = fromDateString;
        } else model.addAttribute("fromDate",fromDateParam);

        model.addAttribute("issuers",api.getIssuers());

        StockData[] issuer = api.getIssuer(code, fromDateParam, toDateParam);
        model.addAttribute("selectedIssuer",code);
        model.addAttribute("stockData",issuer);
        return "issuer";
    }

}
