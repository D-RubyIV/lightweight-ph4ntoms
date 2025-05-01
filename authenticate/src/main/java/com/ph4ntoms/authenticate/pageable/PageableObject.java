package com.ph4ntoms.authenticate.pageable;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * A class representing pagination and sorting parameters for database queries.
 * This class provides functionality to convert pagination parameters into Spring's Pageable object.
 *
 * @author PHAH04
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableObject {

    /**
     * Inner class representing sort parameters
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CSort {
        @NotBlank(message = "Sort order cannot be blank")
        private String order;
        
        @NotBlank(message = "Sort key cannot be blank")
        private String key;
    }

    @NotNull(message = "Query cannot be null")
    private String query;

    @NotNull(message = "Page index cannot be null")
    @PositiveOrZero(message = "Page index must be positive or zero")
    private Integer pageIndex;

    @NotNull(message = "Page size cannot be null")
    @Positive(message = "Page size must be positive")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer pageSize;

    private CSort sort;

    private static final List<String> VALID_ORDERS = List.of("asc", "desc");

    /**
     * Converts this object to a Spring Pageable object with sorting.
     *
     * @return Pageable object configured with pagination and sorting
     * @throws IllegalArgumentException if sort parameters are invalid
     */
    public Pageable toPageRequest() {
        // Adjust page index to be zero-based
        int adjustedPageIndex = this.pageIndex - 1;
        
        // If no sort is specified, return simple pagination
        if (this.sort == null) {
            return PageRequest.of(adjustedPageIndex, this.pageSize);
        }

        // Validate and normalize sort order
        String normalizedOrder = this.sort.getOrder() == null || this.sort.getOrder().isEmpty() 
            ? "asc" 
            : this.sort.getOrder().toLowerCase();

        if (!VALID_ORDERS.contains(normalizedOrder)) {
            throw new IllegalArgumentException("Invalid sort order. Must be either 'asc' or 'desc'");
        }

        // If no sort key is specified, return simple pagination
        if (this.sort.getKey() == null || this.sort.getKey().isEmpty()) {
            return PageRequest.of(adjustedPageIndex, this.pageSize);
        }

        // Create sort object based on order
        Sort sorted = normalizedOrder.equals("asc")
                ? Sort.by(this.sort.getKey()).ascending()
                : Sort.by(this.sort.getKey()).descending();

        return PageRequest.of(adjustedPageIndex, this.pageSize, sorted);
    }
}