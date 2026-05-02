package com.bookstore.mapper;

import com.bookstore.dto.response.WishlistResponse;
import com.bookstore.entity.WishlistItem;

public class WishlistMapper {

    private WishlistMapper() {}

    public static WishlistResponse toResponse(WishlistItem item) {
        if (item == null) return null;

        WishlistResponse r = new WishlistResponse();
        r.setId(item.getId());
        r.setBookId(item.getBook().getId());
        r.setBookTitle(item.getBook().getTitle());
        r.setBookAuthor(item.getBook().getAuthor());
        r.setBookPrice(item.getBook().getPrice());
        r.setBookImageUrl(item.getBook().getImageUrl());
        r.setAddedAt(item.getAddedAt());
        return r;
    }
}