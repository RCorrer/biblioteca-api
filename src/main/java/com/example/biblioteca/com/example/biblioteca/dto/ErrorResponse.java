package com.example.biblioteca.dto;
import lombok.*;
import java.time.Instant;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class ErrorResponse {
  private String erro;
  private int codigo;
  private Instant timestamp;
  private String caminho;
}
