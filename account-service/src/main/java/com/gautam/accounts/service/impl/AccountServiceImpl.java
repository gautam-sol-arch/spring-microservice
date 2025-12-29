package com.gautam.accounts.service.impl;

import com.gautam.accounts.constant.AccountConstant;
import com.gautam.accounts.dto.AccountDto;
import com.gautam.accounts.dto.CustomerDto;
import com.gautam.accounts.exception.CustomerAlreadyExistsException;
import com.gautam.accounts.exception.ResourceNotFoundException;
import com.gautam.accounts.mapper.AccountMapper;
import com.gautam.accounts.mapper.CustomerMapper;
import com.gautam.accounts.model.Account;
import com.gautam.accounts.model.Customer;
import com.gautam.accounts.repository.AccountRepository;
import com.gautam.accounts.repository.CustomerRepository;
import com.gautam.accounts.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(
                customer.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            System.out.println(
                    "Customer already register with given mobile" + " number " + customerDto.getMobileNumber());
            throw new CustomerAlreadyExistsException(
                    "Customer already register with given mobile" + " number " + customerDto.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
        accountRepository.save(createNewAccount(savedCustomer.getCustomerId()));

    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Account account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId",
                                                    customer.getCustomerId().toString()));

        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountDto(AccountMapper.mapToAccountDto(account, new AccountDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountDto accountDto = customerDto.getAccountDto();
        if (accountDto != null) {
            Account account = accountRepository.findById(accountDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "accountNumber",
                                                        accountDto.getAccountNumber().toString()));
            AccountMapper.mapToAccount(accountDto, account);
            account = accountRepository.save(account);

            Long customerId = account.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "customerId",
                                                        customerId.toString()));
            CustomerMapper.mapToCustomer(customerDto, customer);
            customer = customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    private Account createNewAccount(Long customerId) {
        Account account = new Account();
        account.setCustomerId(customerId);
        long randomNumber = 1000000000L + new Random().nextInt(900000000);
        account.setAccountNumber(randomNumber);
        account.setAccountType(AccountConstant.SAVINGS);
        account.setBranchAddress(AccountConstant.ADDRESS);
        return account;
    }
}
