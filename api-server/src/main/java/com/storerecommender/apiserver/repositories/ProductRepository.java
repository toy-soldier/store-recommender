package com.storerecommender.apiserver.repositories;

import com.storerecommender.apiserver.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}