package com.wcdemo.demo.service;

import com.wcdemo.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productServiceImpl;

    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestHeadersSpec requestHeadersSpec;

    @Mock
    WebClient.RequestBodySpec requestBodySpec;

    @Mock
    WebClient.ResponseSpec responseSpec;

    @Mock
    WebClient webClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.headers(any())).willReturn(requestBodySpec);
        given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
        given(requestBodySpec.accept(any())).willReturn(requestBodySpec);
        given(requestBodySpec.bodyValue(any())).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        when(responseSpec.bodyToMono(Product.class)).thenReturn(Mono.just(getDummyProductData()));
    }

    @Test
    public void testCreateProductSuccess() {
        CompletableFuture<Product> result = productServiceImpl.createProduct(getDummyProductData());
        assert result != null;
        Product pdt = result.join();
        assert pdt != null;
        assert pdt.getBrand() != null;
        assert pdt.getBrand().equals("vijay");
        assert pdt.getCategory() != null;
        assert pdt.getDescription() != null;
        assert pdt.getDiscountPercentage() != 0;
        assert pdt.getId() != null;
        assert pdt.getPrice() != 0;
        assert pdt.getRating() != 0;
        assert pdt.getStock() != 0;
        assert pdt.getThumbnail() != null;
        assert pdt.getTitle() != null;
        assert pdt.getImages() == null;
    }

    private Product getDummyProductData() {
        return Product.builder()
                .brand("vijay")
                .category("electronics")
                .description("dummy description")
                .discountPercentage(10)
                .id(1L)
                .price(100)
                .rating(4.5)
                .stock(10)
                .thumbnail("dummy thumbnail")
                .title("title")
                .images(null)
                .build();
    }
}
