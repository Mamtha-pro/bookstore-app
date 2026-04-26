package com.bookstore.repository;


import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);
}
