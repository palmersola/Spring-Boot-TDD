package com.example.tdd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
public class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    public void testListOrders() throws Exception {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setCustomerName("Palmer Sola");
        order1.setOrderDate(LocalDate.now());
        order1.setShippingAddress("333 First St");
        order1.setTotal(100.0);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerName("Elon Musk");
        order2.setOrderDate(LocalDate.now());
        order2.setShippingAddress("6543 Asbury St");
        order2.setTotal(200.0);

        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("orders"))
                .andExpect(MockMvcResultMatchers.model().attribute("orders", orders))
                .andExpect(MockMvcResultMatchers.view().name("/list"));

        verify(orderRepository, times(1)).findAll();
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void testCreateOrderForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/create"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("order"))
                .andExpect(MockMvcResultMatchers.view().name("/create"));
    }

    @Test
    public void testCreateOrder() throws Exception {
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("123 Main St");
        order.setTotal(100.0);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customerName", "John Doe")
                        .param("orderDate", LocalDate.now().toString())
                        .param("shippingAddress", "123 Main St")
                        .param("total", "100.0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"));

        verify(orderRepository, times(1)).save(any(Order.class));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void testEditOrderForm() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("John Doe");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("123 Main St");
        order.setTotal(100.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("order"))
                .andExpect(MockMvcResultMatchers.model().attribute("order", order))
                .andExpect(MockMvcResultMatchers.view().name("/edit"));

        verify(orderRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void testEditOrder() throws Exception {
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setCustomerName("John Doe");
        existingOrder.setOrderDate(LocalDate.now());
        existingOrder.setShippingAddress("123 Main St");
        existingOrder.setTotal(100.0);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setCustomerName("Jane Smith");
        updatedOrder.setOrderDate(LocalDate.now());
        updatedOrder.setShippingAddress("456 Elm St");
        updatedOrder.setTotal(200.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/1/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customerName", "Jane Smith")
                        .param("orderDate", LocalDate.now().toString())
                        .param("shippingAddress", "456 Elm St")
                        .param("total", "200.0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void testDeleteOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"));

        verify(orderRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(orderRepository);
    }
}
