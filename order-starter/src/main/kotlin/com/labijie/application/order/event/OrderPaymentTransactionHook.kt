package com.labijie.application.order.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.support.TransactionSynchronization

class OrderPaymentTransactionHook(private val applicationEventPublisher: ApplicationEventPublisher, private val event: OrderPaymentCompletedEvent) : TransactionSynchronization {
    override fun afterCommit() {
        applicationEventPublisher.publishEvent(event)
    }
}