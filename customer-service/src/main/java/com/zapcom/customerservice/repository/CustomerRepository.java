
package com.zapcom.customerservice.repository;

import com.zapcom.customerservice.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    // Additional query methods can be added here if needed
}
