package com.fixedaccount.app.controllers;

import com.fixedaccount.app.models.documents.FixedTerm;
import com.fixedaccount.app.models.dto.TypeCustomer;
import com.fixedaccount.app.services.FixedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;

@RefreshScope
@RestController
@RequestMapping("/fixedTerm")
public class FixedTermController {
    @Autowired
    FixedTermService service ;

    @GetMapping("/list")
    public Flux<FixedTerm> list(){
        return service.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<FixedTerm> findById(@PathVariable String id){
        return service.findById(id);
    }

    @GetMapping("/findAccountByCustomerId/{id}")
    public Flux<FixedTerm> findAccountCustomerById(@PathVariable String id){
        return service.findAccountCustomerById(id);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<FixedTerm>> create(@Valid @RequestBody FixedTerm fixedTerm){

        return service.findCustomerByDocumentNumber(fixedTerm.getCustomer().getDocumentNumber())
                .filter(customer -> customer.getTypeCustomer().getValue().equals(TypeCustomer.EnumTypeCustomer.PERSONAL) & fixedTerm.getBalance() >= 0)
                .flatMap(customer -> service.countCustomerAccountBank(customer.getId())
                        .filter(count ->  count <1)
                        .flatMap(count -> {
                            fixedTerm.setCustomer(customer);
                            fixedTerm.setBalance(fixedTerm.getBalance() != null ? fixedTerm.getBalance() : 0.0);
                            fixedTerm.setLimitDeposits(1);
                            fixedTerm.setLimitDraft(1);
                            fixedTerm.setCreateAt(LocalDateTime.now());
                            return service.create(fixedTerm).map(ft -> new ResponseEntity<>(ft, HttpStatus.CREATED));
                        })
                ).defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<FixedTerm>> update(@Valid @RequestBody FixedTerm fixedTerm) {

        return service.findById(fixedTerm.getId())
                .flatMap(ftDB -> service.findCustomerById(fixedTerm.getCustomer().getId())
                        .filter(customer -> customer.getTypeCustomer().getValue().equals(TypeCustomer.EnumTypeCustomer.PERSONAL) & fixedTerm.getBalance() >= 0)
                        .flatMap(customer -> {
                            fixedTerm.setCustomer(customer);
                            fixedTerm.setBalance(fixedTerm.getBalance() != null ? fixedTerm.getBalance() : 0.0);
                            fixedTerm.setLimitDeposits(1);
                            fixedTerm.setLimitDraft(1);
                            fixedTerm.setCreateAt(LocalDateTime.now());
                            return service.create(fixedTerm)
                                    .map(ft -> new ResponseEntity<>(ft, HttpStatus.CREATED));
                        })

                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return service.delete(id)
                .filter(deleteFixedTerm -> deleteFixedTerm)
                .map(deleteFixedTerm -> new ResponseEntity<>("Fixed Account Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/findByAccountNumber/{numberAccount}")
    public Mono<FixedTerm> findByAccountNumber(@PathVariable String numberAccount){
        return service.findByCardNumber(numberAccount);
    }

    @PutMapping("/updateTransference")
    public Mono<ResponseEntity<FixedTerm>> updateForTransference(@Valid @RequestBody FixedTerm fixedTerm) {
        return service.create(fixedTerm)
                .filter(customer -> fixedTerm.getBalance() >= 0)
                .map(ft -> new ResponseEntity<>(ft, HttpStatus.CREATED));
    }
}
