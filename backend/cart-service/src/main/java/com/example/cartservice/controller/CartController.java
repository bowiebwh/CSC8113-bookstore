package com.example.cartservice.controller;

import com.example.cartservice.repository.CartItemRepository;
import com.example.common.model.Book;
import com.example.common.model.CartItem;
import com.example.common.model.Order;
import com.example.common.repository.BookRepository;
import com.example.common.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 获取购物车内容
     */
    @GetMapping
    public List<CartItem> getCart() {
        return cartItemRepository.findAll();
    }

    /**
     * 添加书籍到购物车
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItem newItem) {
        // 查找书籍
        Optional<Book> optionalBook = bookRepository.findById(newItem.getBookId());

        if (optionalBook.isEmpty()) {
            return ResponseEntity.badRequest().body("book doesn't exist");
        }

        Book book = optionalBook.get();
        
        Optional<CartItem> existingCartItem = cartItemRepository.findByBookId(book.getId());

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
            return ResponseEntity.ok("cart has already update，" + book.getTitle() + " quantity +1");
        } else {
            // **如果购物车中没有该书，创建新条目**
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
            return ResponseEntity.ok("Book " + book.getTitle() + " has already add to cart");
        }
    }


    /**
     * 更新购物车中的书籍数量
     */
    @PutMapping("/{id}")
    public CartItem updateCartItem(@PathVariable Long id, @RequestBody CartItem updatedItem) {
        return cartItemRepository.findById(id)
                .map(item -> {
                    Optional<Book> book = bookRepository.findById(updatedItem.getBookId());
                    if (book.isPresent() && book.get().getStock() >= updatedItem.getQuantity()) {
                        item.setQuantity(updatedItem.getQuantity());
                        return cartItemRepository.save(item);
                    } else {
                        throw new RuntimeException("Out of stock or book doesn't exist");
                    }
                })
                .orElseThrow(() -> new RuntimeException("cart item doesn't find"));
    }

    /**
     * 删除购物车中的书籍
     */
    @DeleteMapping("/{id}")
    public void removeFromCart(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    public void clearCart() {
        cartItemRepository.deleteAll();
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout() {
        List<CartItem> cartItems = cartItemRepository.findAll();

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("购物车为空，无法结算");
        }

        double totalPrice = 0.0;

        List<Long> bookIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        // 检查库存
        for (CartItem item : cartItems) {
            bookIds.add(item.getBookId());
            quantities.add(item.getQuantity());
            
            Optional<Book> bookOpt = bookRepository.findById(item.getBookId());

            if (bookOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("书籍ID " + item.getBookId() + " 不存在");
            }

            Book book = bookOpt.get();
            if (book.getStock() < item.getQuantity()) {
                return ResponseEntity.badRequest().body("书籍 " + book.getTitle() + " 库存不足");
            }

            // 计算总价
            totalPrice += book.getPrice() * item.getQuantity();
        }

        // 扣减库存
        for (CartItem item : cartItems) {
            Book book = bookRepository.findById(item.getBookId()).get();
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);
        }

        // 创建订单
        Order order = new Order();
        order.setBookIds(bookIds);
        order.setQuantities(quantities);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // 清空购物车
        cartItemRepository.deleteAll();

        DecimalFormat df = new DecimalFormat("#.00");
        totalPrice = Double.parseDouble(df.format(totalPrice));

        return ResponseEntity.ok("Checkout successful，total price: $" + totalPrice);
    }

}
