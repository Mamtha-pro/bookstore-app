package com.bookstore.repository;

import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.book WHERE ci.cart = :cart")
    List<CartItem> findByCart(@Param("cart") Cart cart);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.book WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartId(@Param("cartId") Long cartId);
}