package com.example.tdd;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String listOrders(Model model){
        model.addAttribute("orders", orderRepository.findAll());
        return "/list";
    }

    @GetMapping("/create")
    public String createOrderForm(Model model){
        model.addAttribute("order", new Order());
        return "/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute("order") Order order){
        orderRepository.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/edit")
    public String editAuthorForm(@PathVariable("id") Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author id: " + id));
        model.addAttribute("order", order);
        return "/edit";
    }

    @PostMapping("/{id}/edit")
    public String editAuthor(@PathVariable("id") Long id, @ModelAttribute("order") Order order) {
        order.setId(id);
        orderRepository.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable("id") Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }
}
