package com.gautam.loans.service.impl;

import com.gautam.loans.constant.LoanConstant;
import com.gautam.loans.dto.LoanDto;
import com.gautam.loans.entity.Loan;
import com.gautam.loans.exception.LoanAlreadyExistsException;
import com.gautam.loans.exception.ResourceNotFoundException;
import com.gautam.loans.mapper.LoanMapper;
import com.gautam.loans.repository.LoanRepository;
import com.gautam.loans.service.ILoanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements ILoanService {

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loan> optionalLoan = loanRepository.findByMobileNumber(mobileNumber);
        if (optionalLoan.isPresent()) {
            throw new LoanAlreadyExistsException(
                    "Loan already registered with given mobile number " + mobileNumber);
        }
        loanRepository.save(createNewLoan(mobileNumber));
    }

    @Override
    public LoanDto getLoanDetails(String mobileNumber) {
        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber));
        return loanMapper.toDto(loan);
    }

    @Override
    @Transactional
    public boolean updateLoanDetails(LoanDto loanDto) {
        Loan loan = loanRepository.findByLoanNumber(loanDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "loanNumber", loanDto.getLoanNumber()));
        loanMapper.updateEntityFromDto(loanDto, loan);
        return true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber));
        loanRepository.deleteById(loan.getLoanId());
        return true;
    }

    private Loan createNewLoan(String mobileNumber) {
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        return Loan.builder().loanNumber(Long.toString(randomLoanNumber)).mobileNumber(mobileNumber)
                .loanType(LoanConstant.HOME_LOAN).totalLoan(LoanConstant.NEW_LOAN_LIMIT)
                .amountPaid(BigDecimal.valueOf(0)).outstandingAmount(LoanConstant.NEW_LOAN_LIMIT)
                .build();
    }
}
