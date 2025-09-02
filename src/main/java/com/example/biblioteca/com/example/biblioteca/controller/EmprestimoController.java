package com.example.biblioteca.controller;
import com.example.biblioteca.domain.*;
import com.example.biblioteca.repository.*;
import com.example.biblioteca.exception.NotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
  private final EmprestimoRepository emprestimos;
  private final LivroRepository livros;
  private final UsuarioRepository usuarios;
  public EmprestimoController(EmprestimoRepository e, LivroRepository l, UsuarioRepository u){
    this.emprestimos = e; this.livros = l; this.usuarios = u;
  }

  @PostMapping
  public ResponseEntity<EntityModel<Emprestimo>> criar(@RequestBody Map<String, Long> body){
    Long livroId = body.get("livroId"); Long usuarioId = body.get("usuarioId");
    var livro = livros.findById(livroId).orElseThrow(() -> new NotFoundException("Livro não encontrado"));
    if(!livro.isDisponivel()) return ResponseEntity.status(HttpStatus.CONFLICT).build();
    var usuario = usuarios.findById(usuarioId).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    var emp = Emprestimo.builder().livro(livro).usuario(usuario).dataEmprestimo(LocalDate.now()).devolvido(false).build();
    livro.setDisponivel(false); livros.save(livro);
    var salvo = emprestimos.save(emp);
    return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
  }

  @PutMapping("/{id}/devolucao")
  public EntityModel<Emprestimo> devolver(@PathVariable Long id, @RequestParam(required=false) Long livroId){
    var emp = emprestimos.findById(id).orElseThrow(() -> new NotFoundException("Empréstimo não encontrado"));
    emp.setDevolvido(true); emp.setDataDevolucao(LocalDate.now());
    var e = emprestimos.save(emp);
    var livro = e.getLivro(); livro.setDisponivel(true); livros.save(livro);
    return toModel(e);
  }

  @GetMapping("/{id}")
  public EntityModel<Emprestimo> obter(@PathVariable Long id){
    var emp = emprestimos.findById(id).orElseThrow(() -> new NotFoundException("Empréstimo não encontrado"));
    return toModel(emp);
  }

  private EntityModel<Emprestimo> toModel(Emprestimo e){
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmprestimoController.class).obter(e.getId())).withSelfRel();
    Link contextual = e.isDevolvido()
      ? WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LivroController.class).obter(e.getLivro().getId())).withRel("livro")
      : WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmprestimoController.class).devolver(e.getId(), e.getLivro().getId())).withRel("devolver");
    return EntityModel.of(e, self, contextual);
  }
}
