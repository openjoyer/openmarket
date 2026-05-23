package com.openjoyer.openmarket.order_service.application.port;

import com.openjoyer.openmarket.contracts.dto.cart.CartCheckoutView;

public interface CartQueryPort {
    CartCheckoutView getCartCheckout(String userId);
}
