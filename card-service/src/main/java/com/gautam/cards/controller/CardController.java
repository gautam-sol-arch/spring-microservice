package com.gautam.cards.controller;

import com.gautam.cards.constant.CardConstant;
import com.gautam.cards.dto.CardDto;
import com.gautam.cards.dto.ResponseDto;
import com.gautam.cards.service.ICardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final ICardService iCardService;

    @PostMapping
    public ResponseEntity<ResponseDto> createCard(@RequestParam String mobileNumber) {
        iCardService.createCard(mobileNumber);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(CardConstant.STATUS_201, CardConstant.MESSAGE_201));
    }

    @GetMapping
    public ResponseEntity<CardDto> getCardDetails(@RequestParam String mobileNumber) {
        CardDto cardDto = iCardService.getCardDetails(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(cardDto);
    }

    @PutMapping
    public ResponseEntity<ResponseDto> updateCardDetails(@RequestBody CardDto cardDto) {
        boolean isUpdated = iCardService.updateCard(cardDto);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(CardConstant.STATUS_200, CardConstant.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CardConstant.STATUS_417,
                                          CardConstant.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteCard(@RequestParam String mobileNumber) {
        boolean isDeleted = iCardService.deleteCard(mobileNumber);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(CardConstant.STATUS_200, CardConstant.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CardConstant.STATUS_417,
                                          CardConstant.MESSAGE_417_DELETE));
        }
    }
}
