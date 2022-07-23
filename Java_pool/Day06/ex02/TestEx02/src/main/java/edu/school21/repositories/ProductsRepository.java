package main.java.edu.school21.repositories;

import main.java.edu.school21.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRepository {
    List<Product>       findAll();
    Optional<Product>   findById(Long id);
    void                save(Product product);
    void                update(Product product);
    void                delete(Product product);
}
