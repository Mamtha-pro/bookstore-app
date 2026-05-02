package com.bookstore.mapper;

import com.bookstore.dto.response.CartItemResponse;
import com.bookstore.dto.response.CartResponse;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    private CartMapper() {}

    public static CartResponse toResponse(Cart cart) {
        if (cart == null) return null;

        List<CartItemResponse> items = new ArrayList<>();
        if (cart.getItems() != null) {
            items = cart.getItems()
                    .stream()
                    .map(CartMapper::toItemResponse)
                    .collect(Collectors.toList());
        }

        double total = items.stream()
                .mapToDouble(i -> i.getSubtotal() != null
                        ? i.getSubtotal() : 0.0)
                .sum();

        CartResponse r = new CartResponse();
        r.setCartId(cart.getId());
        r.setItems(items);
        r.setTotalAmount(total);
        return r;
    }

    private static CartItemResponse toItemResponse(CartItem item) {
        CartItemResponse r = new CartItemResponse();
        r.setItemId(item.getId());
        r.setBookId(item.getBook().getId());
        r.setBookTitle(item.getBook().getTitle());
        r.setQuantity(item.getQuantity());
        r.setUnitPrice(item.getUnitPrice());
        r.setSubtotal(item.getUnitPrice() * item.getQuantity());
        return r;
    }
}
