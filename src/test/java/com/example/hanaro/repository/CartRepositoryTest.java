package com.example.hanaro.repository;

import com.example.hanaro.domain.cart.entity.Cart;
import com.example.hanaro.domain.cart.entity.CartItem;
import com.example.hanaro.domain.cart.repository.CartRepository;
import com.example.hanaro.domain.cart.repository.CartItemRepository;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.repository.ProductRepository;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CartRepositoryTest extends RepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DailySalesStatsRepository dailySalesStatsRepository;

    @Autowired
    private DailyProductStatsRepository dailyProductStatsRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // clean up repositories to ensure test isolation
        dailySalesStatsRepository.deleteAll();
        dailyProductStatsRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        testUser = User.builder()
                .email("cart_test@example.com")
                .password("password123")
                .nickname("cartTester")
                .role("ROLE_USER")
                .build();
        userRepository.save(testUser);

        testProduct = Product.builder()
                .name("Test Product")
                .price(1000)
                .stockQuantity(50)
                .build();
        productRepository.save(testProduct);
    }

    @Test
    @Order(1)
    void addCartAndCartItem() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cartRepository.save(cart);

        CartItem cartItem = new CartItem(cart, testProduct, 2);
        cartItemRepository.save(cartItem);

        Cart foundCart = cartRepository.findById(cart.getId()).orElseThrow();
        CartItem foundItem = cartItemRepository.findById(cartItem.getId()).orElseThrow();

        assertThat(foundCart.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundItem.getCart().getId()).isEqualTo(cart.getId());
        assertThat(foundItem.getProduct().getId()).isEqualTo(testProduct.getId());
        assertThat(foundItem.getQuantity()).isEqualTo(2);

        Optional<Cart> optionalCart = cartRepository.findByUser(testUser);
        assertThat(optionalCart).isPresent();
        assertThat(optionalCart.get().getId()).isEqualTo(cart.getId());
    }

    @Test
    @Order(2)
    void findCartItemByCartAndProduct() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cartRepository.save(cart);

        CartItem cartItem = new CartItem(cart, testProduct, 3);
        cartItemRepository.save(cartItem);

        Optional<CartItem> found = cartItemRepository.findByCartAndProduct(cart, testProduct);

        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(3);
        assertThat(found.get().getProduct().getName()).isEqualTo("Test Product");
    }

    @Test
    @Order(3)
    void updateCartItemQuantity() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cartRepository.save(cart);
        CartItem cartItem = new CartItem(cart, testProduct, 1);
        cartItemRepository.save(cartItem);

        CartItem saved = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        saved.setQuantity(5);
        cartItemRepository.save(saved);

        CartItem updated = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(5);
    }

    @Test
    @Order(4)
    void deleteCartItem() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cartRepository.save(cart);

        CartItem cartItem = new CartItem(cart, testProduct, 1);
        cartItemRepository.save(cartItem);

        Long cartItemId = cartItem.getId();

        cartItemRepository.deleteById(cartItemId);

        assertThat(cartItemRepository.findById(cartItemId)).isEmpty();
    }
}