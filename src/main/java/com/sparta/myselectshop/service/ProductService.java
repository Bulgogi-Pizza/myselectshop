package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public static final int MIN_MY_PRICE = 100;

  public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
    Product product = productRepository.save(new Product(requestDto, user));

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

  public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy,
      boolean isAsc) {

    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    UserRoleEnum userRoleEnum = user.getRole();

    Page<Product> productList;

    if (userRoleEnum == UserRoleEnum.ADMIN) {
      productList = productRepository.findAll(pageable);
    } else {
      productList = productRepository.findAllByUser(user, pageable);
    }

    return productList.map(ProductResponseDto::new);
  }

  @Transactional
  public void updateBySearch(Long id, ItemDto itemDto) {
    Product product = productRepository.findById(id).orElseThrow(() ->
        new NullPointerException("해당 상품은 존재하지 않습니다."));

    product.updateByItemDto(itemDto);
  }
}
