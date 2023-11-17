package com.repo;

import com.model.cart.CartItem;
import com.model.cart.CartItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository <CartItem, CartItemPK> {
}
