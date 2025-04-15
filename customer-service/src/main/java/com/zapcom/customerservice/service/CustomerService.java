
package com.zapcom.customerservice.service;

import com.zapcom.customerservice.model.Customer;
import com.zapcom.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {
        // Check if customer exists
        if (!customerRepository.existsById(customer.getId())) {
            throw new RuntimeException("Customer not found with id: " + customer.getId());
        }
        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        // Check if customer exists
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
