package com.gautam.cards.service.impl;

import com.gautam.cards.constant.CardConstant;
import com.gautam.cards.dto.CardDto;
import com.gautam.cards.entity.Card;
import com.gautam.cards.exception.CardAlreadyExistsException;
import com.gautam.cards.exception.ResourceNotFoundException;
import com.gautam.cards.mapper.CardMapper;
import com.gautam.cards.repository.CardRepository;
import com.gautam.cards.service.ICardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public void createCard(String mobileNumber) {
        Optional<Card> optionalCard = cardRepository.findByMobileNumber(mobileNumber);

        if (optionalCard.isPresent()) {
            throw new CardAlreadyExistsException(
                    "Card already registered with given mobile number : " + mobileNumber);
        }
        cardRepository.save(createNewCard(mobileNumber));
    }

    @Override
    public CardDto getCardDetails(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber));
        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    public boolean updateCard(CardDto cardDto) {
        Card card = cardRepository.findByCardNumber(cardDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", cardDto.getCardNumber()));
        cardMapper.updateEntityFromDto(cardDto, card);
        return true;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber));
        cardRepository.deleteById(card.getCardId());
        return true;
    }

    private Card createNewCard(String mobileNumber) {
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        return Card.builder().cardNumber(Long.toString(randomCardNumber)).mobileNumber(mobileNumber)
                .cardType(CardConstant.CREDIT_CARD).totalLimit(CardConstant.NEW_CARD_LIMIT)
                .amountUsed(BigDecimal.valueOf(0)).availableAmount(CardConstant.NEW_CARD_LIMIT)
                .build();
    }
}
