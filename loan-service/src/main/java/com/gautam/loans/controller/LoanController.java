package com.gautam.loans.controller;

import com.gautam.loans.constant.LoanConstant;
import com.gautam.loans.dto.LoanDto;
import com.gautam.loans.dto.ResponseDto;
import com.gautam.loans.service.ILoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService iLoanService;

    @PostMapping
    public ResponseEntity<ResponseDto> createLoan(@RequestParam String mobileNumber) {
        iLoanService.createLoan(mobileNumber);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(LoanConstant.STATUS_201, LoanConstant.MESSAGE_201));
    }

    @GetMapping
    public ResponseEntity<LoanDto> getLoanDetails(@RequestParam String mobileNumber) {
        LoanDto loanDto = iLoanService.getLoanDetails(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(loanDto);
    }

    @PutMapping
    public ResponseEntity<ResponseDto> updateLoanDetails(@RequestBody LoanDto loanDto) {
        boolean isUpdatedd = iLoanService.updateLoanDetails(loanDto);
        if (isUpdatedd) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(LoanConstant.STATUS_200, LoanConstant.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoanConstant.STATUS_417,
                                          LoanConstant.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteLoan(@RequestParam String mobileNumber) {
        boolean isUpdatedd = iLoanService.deleteLoan(mobileNumber);
        if (isUpdatedd) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(LoanConstant.STATUS_200, LoanConstant.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoanConstant.STATUS_417,
                                          LoanConstant.MESSAGE_417_DELETE));
        }
    }

}
