package com.example.cartservice.controller;

import com.example.cartservice.repository.CartItemRepository;
import com.example.common.model.Book;
import com.example.common.model.CartItem;
import com.example.common.model.Order;
import com.example.common.repository.BookRepository;
import com.example.common.repository.OrderRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
     * get cart's books
     */
    @GetMapping
    public List<CartItem> getCart(@RequestParam String sessionId) {
        System.out.println("getCart sessionId------------" + sessionId);
        return cartItemRepository.findBySessionId(sessionId);
    }

    /**
     * add book to cart
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItem newItem) {
        System.out.println("addCart sessionId------------" + newItem.getSessionId());
        Optional<Book> optionalBook = bookRepository.findById(newItem.getBookId());

        if (optionalBook.isEmpty()) {
            return ResponseEntity.badRequest().body("book doesn't exist");
        }

        Book book = optionalBook.get();

        Optional<CartItem> existingCartItem = cartItemRepository.findBySessionIdAndBookId(newItem.getSessionId(), book.getId());

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
            return ResponseEntity.ok("cart has already update，" + book.getTitle() + " quantity +1");
        } else {
            // **store book to the cart**
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
            return ResponseEntity.ok("Book " + book.getTitle() + " has already add to cart");
        }
    }


    /**
     * update the quantity of books in the cart
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
     * delete books in the cart
     */
    @DeleteMapping("/{id}")
    public void removeFromCart(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
    }

    /**
     * clear the cart
     */
    @DeleteMapping
    public void clearCart() {
        cartItemRepository.deleteAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout() {
        List<CartItem> cartItems = cartItemRepository.findAll();

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("cart is empty，can't checkout");
        }

        double totalPrice = 0.0;

        List<Long> bookIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        // check the stock
        for (CartItem item : cartItems) {
            bookIds.add(item.getBookId());
            quantities.add(item.getQuantity());
            
            Optional<Book> bookOpt = bookRepository.findById(item.getBookId());

            if (bookOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Book ID " + item.getBookId() + " doesn't exist");
            }

            Book book = bookOpt.get();
            if (book.getStock() < item.getQuantity()) {
                return ResponseEntity.badRequest().body("Book " + book.getTitle() + " out of stock");
            }

            // calculate the total price
            totalPrice += book.getPrice() * item.getQuantity();
        }

        // minus stock
        for (CartItem item : cartItems) {
            Book book = bookRepository.findById(item.getBookId()).get();
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);
        }

        // create order
        Order order = new Order();
        order.setBookIds(bookIds);
        order.setQuantities(quantities);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // clear the cart
        cartItemRepository.deleteAll();

        DecimalFormat df = new DecimalFormat("#.00");
        totalPrice = Double.parseDouble(df.format(totalPrice));

        return ResponseEntity.ok("Checkout successful，total price: $" + totalPrice);
    }
}
