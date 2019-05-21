package com.example.esdemo.dao;

import com.example.esdemo.pojo.Category;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryDao extends ElasticsearchRepository<Category,Integer> {
}
