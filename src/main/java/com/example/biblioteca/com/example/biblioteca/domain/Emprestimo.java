package com.example.biblioteca.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Emprestimo {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(optional=false)
  private Livro livro;
  @ManyToOne(optional=false)
  private Usuario usuario;
  private LocalDate dataEmprestimo = LocalDate.now();
  private LocalDate dataDevolucao;
  private boolean devolvido = false;
}
