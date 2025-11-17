package dev.scastillo.user_tickets.shared.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Respuesta paginada genérica")
public class PagedResponse <T> {
    @Schema(description = "Número de página actual", example = "1")
    private int page;
    @Schema(description = "Tamaño de página", example = "10")
    private int size;
    @Schema(description = "Total de elementos disponibles", example = "100")
    private long totalItems;
    @Schema(description = "Total de páginas disponibles", example = "10")
    private int totalPages;
    @Schema(description = "Lista de elementos en la página actual")
    private List<T> data;
}
