package com.fixedaccount.app.services.impl;

import com.fixedaccount.app.models.dao.FixedTermDao;
import com.fixedaccount.app.models.documents.FixedTerm;
import com.fixedaccount.app.models.dto.Customer;
import com.fixedaccount.app.services.FixedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class FixedTermServiceImpl implements FixedTermService {
    private final WebClient webClient;
    private final WebClient webClientNumber;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    @Value("${config.base.apigatewey}")
    private String url;

    @Value("${config.base.number}")
    private String urln;

    public FixedTermServiceImpl(ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = WebClient.builder().baseUrl(this.url).build();
        this.webClientNumber = WebClient.builder().baseUrl(this.urln).build();
        this.reactiveCircuitBreaker = circuitBreakerFactory.create("customer");
    }
    @Autowired
    FixedTermDao dao;

    @Override
    public Mono<Customer> findCustomerByDocumentNumber(String number) {
        return reactiveCircuitBreaker.run(webClient.get().uri(this.urln,number).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Customer.class),
                throwable -> {return this.getDefaultCustomer();});
    }

    @Override
    public Mono<Customer> findCustomerById(String id) {
        return reactiveCircuitBreaker.run(webClient.get().uri(this.url,id).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Customer.class),
                throwable -> {return this.getDefaultCustomer();});
    }

    public Mono<Customer> getDefaultCustomer() {
        Mono<Customer> customer = Mono.just(new Customer());
        return customer;
    }

    @Override
    public Mono<FixedTerm> create(FixedTerm fixedTerm) {
        return dao.save(fixedTerm);
    }

    @Override
    public Flux<FixedTerm> findAll() {
        return dao.findAll();
    }

    @Override
    public Mono<FixedTerm> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<FixedTerm> update(FixedTerm fixedTerm) {
        return dao.save(fixedTerm);
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return dao.findById(id)
                .flatMap(cf -> dao.delete(cf).then(Mono.just(Boolean.TRUE))
                ).defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> countCustomerAccountBank(String id) {
        return dao.findByCustomerId(id).count();
    }

    @Override
    public Mono<Long> countCustomerAccountBankDocumentNumber(String number) {
        return dao.findByCustomerDocumentNumber(number).count();
    }

    @Override
    public Mono<FixedTerm> findByCardNumber(String numberAccount) {
        return dao.findByCardNumber(numberAccount);
    }

    @Override
    public Flux<FixedTerm> findAccountCustomerById(String id) {
        return dao.findByCustomerId(id);
    }
}
