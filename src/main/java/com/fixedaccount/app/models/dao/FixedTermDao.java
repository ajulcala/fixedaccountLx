package com.fixedaccount.app.models.dao;

import com.fixedaccount.app.models.documents.FixedTerm;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FixedTermDao extends ReactiveMongoRepository<FixedTerm, String> {
    Flux<FixedTerm> findByCustomerId(String id);
    Flux<FixedTerm> findByCustomerDocumentNumber(String number);
    Mono<FixedTerm> findByCardNumber(String id);
}
