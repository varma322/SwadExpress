package com.fooddelivery.payment.service;

import com.fooddelivery.common.exception.BadRequestException;
import com.fooddelivery.payment.entity.PaymentTransaction;
import com.fooddelivery.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentTransaction processPayment(Long orderId, Long userId, BigDecimal amount, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Invalid payment amount: " + amount);
        }

        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentTransaction txn = new PaymentTransaction(
                orderId,
                userId,
                amount,
                paymentMethod.toUpperCase(),
                txnRef,
                "SUCCESS"
        );

        return paymentRepository.save(txn);
    }
}
