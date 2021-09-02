package com.labijie.application.order.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.support.TransactionSynchronization

class OrderRefundTransactionHook(
  private val applicationEventPublisher: ApplicationEventPublisher,
  private val event: OrderRefundCompletedEvent,
): TransactionSynchronization {

  override fun afterCommit() {
    applicationEventPublisher.publishEvent(event)
  }
}