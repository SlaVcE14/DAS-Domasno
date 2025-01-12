package mk.finki.ukim.das.backend.service.impl;

import mk.finki.ukim.das.backend.model.Issuer;
import mk.finki.ukim.das.backend.model.StockData;
import mk.finki.ukim.das.backend.repository.IssuerRepository;
import mk.finki.ukim.das.backend.service.IssuerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssuerServiceImpl implements IssuerService {

    private final IssuerRepository issuerRepository;

    public IssuerServiceImpl(IssuerRepository issuerRepository) {
        this.issuerRepository = issuerRepository;
    }

    @Override
    public List<String> getIssuers() {
        return issuerRepository.getIssuers();
    }

    @Override
    public Issuer getIssuer(String code) {
        return issuerRepository.getIssuer(code);
    }

    @Override
    public List<StockData> getStockData(Issuer issuer, String from, String to) {
        return issuerRepository.getStockData(issuer, from, to);
    }
}
