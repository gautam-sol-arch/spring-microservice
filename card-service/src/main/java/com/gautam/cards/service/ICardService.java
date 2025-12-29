package com.gautam.cards.service;

import com.gautam.cards.dto.CardDto;

public interface ICardService {


    void createCard(String mobileNumber);

    CardDto getCardDetails(String mobileNumber);

    boolean updateCard(CardDto cardDto);

    boolean deleteCard(String mobileNumber);
}
