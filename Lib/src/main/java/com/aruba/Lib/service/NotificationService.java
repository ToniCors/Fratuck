package com.aruba.Lib.service;

import com.aruba.Lib.logging.logger.MsLogger;

public class NotificationService {

    static public void notifyAfterOrder() {
        MsLogger.logger.info("Order is created correctly");
    }

    static public void notifyAfterPayment() {
        MsLogger.logger.info("Payment is accepted correctly");

    }

    static public void notifyAfterDelivery() {
        MsLogger.logger.info("Order is shipped correctly");

    }


}
