package com.platzi.market.persistence;

import com.platzi.market.domain.Category;
import com.platzi.market.domain.repository.CategoryRepository;
import com.platzi.market.persistence.crud.CategoriaCrudRepository;
import com.platzi.market.persistence.entity.Categoria;
import com.platzi.market.persistence.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CategoriaRepository implements CategoryRepository {
    @Autowired
    private CategoriaCrudRepository categoriaCrudRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> getAll() {
        List<Categoria> categorias = (List<Categoria>) categoriaCrudRepository.findAll();
        return categoryMapper.toCategory(categorias);
    }
}
