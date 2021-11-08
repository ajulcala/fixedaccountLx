package com.fixedaccount.app.services;

import com.fixedaccount.app.models.documents.FixedTerm;
import com.fixedaccount.app.models.dto.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FixedTermService {
    public Mono<FixedTerm> create(FixedTerm fixedTerm);
    public Flux<FixedTerm> findAll();
    public Mono<FixedTerm> findById(String id);
    public Mono<FixedTerm> update(FixedTerm fixedTerm);
    public Mono<Boolean> delete(String id);
    Mono<Long> countCustomerAccountBank(String id);
    Mono<Long> countCustomerAccountBankDocumentNumber(String number);
    Mono<Customer> findCustomerByDocumentNumber(String number);
    Mono<Customer> findCustomerById(String id);
    public Mono<FixedTerm> findByCardNumber(String numberAccount);
    Flux<FixedTerm> findAccountCustomerById(String id);
}
