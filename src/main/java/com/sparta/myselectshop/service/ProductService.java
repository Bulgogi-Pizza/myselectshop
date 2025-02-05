package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public static final int MIN_MY_PRICE = 100;

  public ProductResponseDto createProduct(ProductRequestDto requestDto) {
    Product product = productRepository.save(new Product(requestDto));

    return new ProductResponseDto(product);
  }

  @Transactional
  public ProductResponseDto updateProduct(ProductMypriceRequestDto requestDto, Long id) {
    Product product = productRepository.findById(id).orElseThrow(
        () -> new EntityExistsException("존재하지 않는 마이 상품입니다."));

    if (requestDto.getMyprice() < MIN_MY_PRICE) {
      throw new IllegalArgumentException("내 가격 설정은 " + MIN_MY_PRICE + "원 이상이어야 합니다.");
    }

    product.update(requestDto);

    return new ProductResponseDto(product);
  }

  public List<ProductResponseDto> getProducts() {
    return productRepository.findAll().stream()
        .map(ProductResponseDto::new)
        .toList();
  }
}
