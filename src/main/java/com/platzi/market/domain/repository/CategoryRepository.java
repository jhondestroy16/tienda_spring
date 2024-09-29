package com.platzi.market.domain.repository;

import com.platzi.market.domain.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> getAll();
}
