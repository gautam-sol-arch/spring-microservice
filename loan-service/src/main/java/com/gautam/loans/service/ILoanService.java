package com.gautam.loans.service;

import com.gautam.loans.dto.LoanDto;

public interface ILoanService {
    void createLoan(String mobileNumber);

    LoanDto getLoanDetails(String mobileNumber);

    boolean updateLoanDetails(LoanDto loanDto);

    boolean deleteLoan(String mobileNumber);
}
