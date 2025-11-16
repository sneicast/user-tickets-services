package dev.scastillo.user_tickets.shared.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedResponse <T> {
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private List<T> data;
}
