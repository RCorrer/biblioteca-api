package com.example.biblioteca.controller;
import com.example.biblioteca.domain.*;
import com.example.biblioteca.repository.*;
import com.example.biblioteca.exception.NotFoundException;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/livros")
public class LivroController {
  private final LivroRepository livros;
  private final AutorRepository autores;

  public LivroController(LivroRepository livros, AutorRepository autores){
    this.livros = livros; this.autores = autores;
  }

  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<Livro>>> listar(
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="10") int size,
      @RequestParam(defaultValue="id") String sort,
      @RequestParam(defaultValue="asc") String order,
      @RequestParam(required=false) String autor,
      @RequestParam(required=false) Boolean disponivel
  ){
    Sort s = order.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
    Pageable pageable = PageRequest.of(page, size, s);
    Page<Livro> result;
    if(autor != null && disponivel != null){
      result = livros.findByAutor_NomeContainingIgnoreCaseAndDisponivel(autor, disponivel, pageable);
    }else if(autor != null){
      result = livros.findByAutor_NomeContainingIgnoreCase(autor, pageable);
    }else if(disponivel != null){
      result = livros.findByDisponivel(disponivel, pageable);
    }else{
      result = livros.findAll(pageable);
    }
    var models = result.getContent().stream().map(this::toModel).toList();
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LivroController.class)
      .listar(page, size, sort, order, autor, disponivel)).withSelfRel();
    return ResponseEntity.ok(CollectionModel.of(models, self));
  }

  @GetMapping("/{id}")
  public EntityModel<Livro> obter(@PathVariable Long id){
    var livro = livros.findById(id).orElseThrow(() -> new NotFoundException("Livro não encontrado"));
    return toModel(livro);
  }

  @PostMapping
  public ResponseEntity<EntityModel<Livro>> criar(@Valid @RequestBody Livro dto){
    Autor autor = autores.findById(dto.getAutor().getId())
      .orElseThrow(() -> new NotFoundException("Autor do livro não encontrado"));
    dto.setAutor(autor);
    var salvo = livros.save(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
  }

  @PutMapping("/{id}")
  public EntityModel<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dto){
    var livro = livros.findById(id).orElseThrow(() -> new NotFoundException("Livro não encontrado"));
    livro.setTitulo(dto.getTitulo());
    livro.setIsbn(dto.getIsbn());
    livro.setAnoPublicacao(dto.getAnoPublicacao());
    if(dto.getAutor()!=null && dto.getAutor().getId()!=null){
      Autor a = autores.findById(dto.getAutor().getId()).orElseThrow(() -> new NotFoundException("Autor do livro não encontrado"));
      livro.setAutor(a);
    }
    livro.setDisponivel(dto.isDisponivel());
    return toModel(livros.save(livro));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remover(@PathVariable Long id){
    if(!livros.existsById(id)) throw new NotFoundException("Livro não encontrado");
    livros.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private EntityModel<Livro> toModel(Livro l){
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LivroController.class).obter(l.getId())).withSelfRel();
    var update = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LivroController.class).atualizar(l.getId(), l)).withRel("update");
    var delete = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LivroController.class).remover(l.getId())).withRel("delete");
    Link contextual;
    if(l.isDisponivel()){
      contextual = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmprestimoController.class).criar(null)).withRel("emprestar");
    }else{
      contextual = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmprestimoController.class).devolver(null, l.getId())).withRel("devolver");
    }
    return EntityModel.of(l, self, update, delete, contextual);
  }
}
