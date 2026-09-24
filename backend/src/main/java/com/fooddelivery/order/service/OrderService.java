package com.fooddelivery.order.service;

import com.fooddelivery.common.exception.BadRequestException;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.order.dto.CreateOrderRequestDto;
import com.fooddelivery.order.dto.OrderItemRequestDto;
import com.fooddelivery.order.dto.OrderItemResponseDto;
import com.fooddelivery.order.dto.OrderResponseDto;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderItemRepository;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.payment.entity.PaymentTransaction;
import com.fooddelivery.payment.service.PaymentService;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import com.fooddelivery.tracking.service.TrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final PaymentService paymentService;
    private final TrackingService trackingService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        RestaurantRepository restaurantRepository,
                        MenuItemRepository menuItemRepository,
                        PaymentService paymentService,
                        TrackingService trackingService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.paymentService = paymentService;
        this.trackingService = trackingService;
    }

    public OrderResponseDto createOrder(CreateOrderRequestDto dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + dto.getRestaurantId()));

        if (dto.getDeliveryAddress() == null || dto.getDeliveryAddress().trim().isEmpty()) {
            throw new BadRequestException("Delivery address is required. Please select or add a delivery address.");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for (OrderItemRequestDto itemReq : dto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemReq.getMenuItemId()));

            if (!menuItem.getRestaurantId().equals(restaurant.getId())) {
                throw new BadRequestException("Item " + menuItem.getName() + " does not belong to " + restaurant.getName());
            }

            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = new OrderItem(
                    null,
                    menuItem.getId(),
                    menuItem.getName(),
                    menuItem.getPrice(),
                    itemReq.getQuantity(),
                    itemTotal
            );
            orderItemsToSave.add(orderItem);
        }

        if (subtotal.compareTo(restaurant.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Order subtotal (₹" + subtotal + ") does not meet minimum order of ₹" + restaurant.getMinOrderAmount());
        }

        BigDecimal deliveryFee = restaurant.getDeliveryFee();
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(deliveryFee).add(tax);

        Order order = new Order(
                dto.getUserId(),
                restaurant.getId(),
                restaurant.getName(),
                subtotal,
                deliveryFee,
                tax,
                totalAmount,
                dto.getDeliveryAddress(),
                dto.getDeliveryInstructions(),
                dto.getPaymentMethod(),
                "PENDING",
                null
        );

        Order savedOrder = orderRepository.save(order);

        // Process payment
        PaymentTransaction payment = paymentService.processPayment(
                savedOrder.getId(),
                dto.getUserId(),
                totalAmount,
                dto.getPaymentMethod()
        );

        savedOrder.setPaymentStatus(payment.getStatus());
        savedOrder.setPaymentTxnId(payment.getTransactionRef());
        orderRepository.save(savedOrder);

        // Save order items
        for (OrderItem item : orderItemsToSave) {
            item.setOrderId(savedOrder.getId());
        }
        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItemsToSave);

        // Initialize tracking
        trackingService.initializeTracking(savedOrder.getId());

        return toDto(savedOrder, savedItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(o -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
                    return toDto(o, items);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return toDto(order, items);
    }

    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return toDto(updated, items);
    }

    private OrderResponseDto toDto(Order o, List<OrderItem> items) {
        List<OrderItemResponseDto> itemDtos = items.stream()
                .map(i -> new OrderItemResponseDto(
                        i.getId(),
                        i.getMenuItemId(),
                        i.getItemName(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getTotalPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                o.getId(),
                o.getUserId(),
                o.getRestaurantId(),
                o.getRestaurantName(),
                o.getStatus(),
                o.getSubtotal(),
                o.getDeliveryFee(),
                o.getTax(),
                o.getTotalAmount(),
                o.getDeliveryAddress(),
                o.getDeliveryInstructions(),
                o.getPaymentMethod(),
                o.getPaymentStatus(),
                o.getPaymentTxnId(),
                o.getCreatedAt(),
                itemDtos
        );
    }
}
