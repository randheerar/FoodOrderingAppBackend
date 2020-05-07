package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired private PaymentDao paymentDao;

    /**
     * Logic to get all the payment methods
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }

    /**
     * Logic to get payment method based on Payment method UUID
     * @param paymentUUID UUID of the payment that is to be fetched
     */
    public PaymentEntity getPaymentByUUID(String paymentUUID) throws PaymentMethodNotFoundException {
        PaymentEntity payment = paymentDao.getPaymentByUUID(paymentUUID);
        if (payment == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        return payment;
    }
}
