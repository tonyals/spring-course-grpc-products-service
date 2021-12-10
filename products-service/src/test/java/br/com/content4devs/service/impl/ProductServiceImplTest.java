package br.com.content4devs.service.impl;

import br.com.content4devs.domain.Product;
import br.com.content4devs.dto.ProductInputDTO;
import br.com.content4devs.dto.ProductOutputDTO;
import br.com.content4devs.exception.ProductAlreadyExistsException;
import br.com.content4devs.exception.ProductNotFoundException;
import br.com.content4devs.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("when create product service is call with valid data a product is returned")
    public void createProductSuccessTest() {
        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.save(any())).thenReturn(product);

        ProductInputDTO inputDTO = new ProductInputDTO("product name", 10.00, 10);
        ProductOutputDTO outputDTO = productService.create(inputDTO);

        assertThat(outputDTO)
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    @DisplayName("when create product service is call with duplicated name, throw ProductAlreadyExistsException")
    public void createProductExceptionTest() {
        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(product));

        ProductInputDTO inputDTO = new ProductInputDTO("product name", 10.00, 10);

        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> productService.create(inputDTO));
    }

    @Test
    @DisplayName("when findById product is call with valid id a product is returned")
    public void findByIdSuccessTest() {
        Long id = 1L;
        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        ProductOutputDTO outputDTO = productService.findById(id);

        assertThat(outputDTO)
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    @DisplayName("when findById product is call with invalid id throws ProductNotFoundException")
    public void findByIdExceptionTest() {
        Long id = 1L;

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.findById(id));
    }

    @Test
    @DisplayName("when delete product is call with id should does not throw")
    public void deleteSuccessTest() {
        Long id = 1L;
        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatNoException().isThrownBy(() -> productService.findById(id));
    }

    @Test
    @DisplayName("when delete product is call with invalid id throws ProductNotFoundException")
    public void deleteExceptionTest() {
        Long id = 1L;

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.delete(id));
    }

    @Test
    @DisplayName("when findAll product is call a list of product is returned")
    public void findAllSuccessTest() {
        List<Product> products = List.of(
                new Product(1L, "product name", 10.00, 10),
                new Product(2L, "other product name", 10.00, 100)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<ProductOutputDTO> outputDTOS = productService.findAll();

        assertThat(outputDTOS)
                .extracting("id", "name", "price", "quantityInStock")
                .contains(
                        tuple(1L, "product name", 10.00, 10),
                        tuple(2L, "other product name", 10.00, 100)
                );
    }
}
